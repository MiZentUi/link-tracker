package backend.academy.linktracker.scrapper.service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import backend.academy.linktracker.scrapper.client.BotClient;
import backend.academy.linktracker.scrapper.dto.LinkUpdate;
import backend.academy.linktracker.scrapper.model.Chat;
import backend.academy.linktracker.scrapper.repository.LinksRepository;
import backend.academy.linktracker.scrapper.service.api.ScrapingApiService;
import backend.academy.linktracker.scrapper.service.sender.MessageSender;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final LinksRepository linksRepository;

    private final MessageSender sender;
    private final List<ScrapingApiService> apiServices;

    @Transactional
    @Scheduled(fixedDelayString = "${app.updates.fixed-delay.in.seconds}", timeUnit = TimeUnit.SECONDS)
    public void checkUpdates() {
        var links = linksRepository.findAll();
        for (var link : links) {
            var url = link.getUrl();
            var apiService = apiServices.stream()
                    .filter(s -> url.contains(s.getBaseUrl()))
                    .findAny()
                    .orElse(null);

            if (apiService == null) {
                log.atWarn().addKeyValue("url", url).log("api service not exists for link");
                continue;
            }

            log.atInfo().addKeyValue("url", url).log("check link");

            try {
                // var updatedAt = apiService.getLastUpdate(link);
                var lastUpdate = link.getLastUpdate();

                // log.atInfo()
                // .addKeyValue(url, url)
                // .addKeyValue("updated", updatedAt.format(DateTimeFormatter.ISO_DATE_TIME))
                // .addKeyValue("last_update",
                // lastUpdate.format(DateTimeFormatter.ISO_DATE_TIME))
                // .log("link updates");

                // if (updatedAt != null && updatedAt.isAfter(lastUpdate)) {
                var descriptions = apiService.getChangesDescriptions(link, lastUpdate);
                for (var description : descriptions) {
                    sender.sendLinkUpdate(LinkUpdate.builder()
                            .id(link.getId())
                            .url(link.getUrl())
                            .description(description)
                            .tgChatIds(link.getChats().stream().map(Chat::getId).toList())
                            .build());
                }
                if (!descriptions.isEmpty()) {
                    link.setLastUpdate(OffsetDateTime.now());
                    linksRepository.save(link);
                }
                // }
            } catch (Exception e) {
                log.atError().addKeyValue("exception", e.getMessage()).log();
                sender.sendLinkUpdate(LinkUpdate.builder()
                        .id(link.getId())
                        .url(link.getUrl())
                        .description("Ошибка обработки!")
                        .tgChatIds(link.getChats().stream().map(Chat::getId).toList())
                        .build());
            }
        }
    }
}
