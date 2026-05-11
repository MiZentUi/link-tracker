package backend.academy.linktracker.scrapper.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import backend.academy.linktracker.scrapper.client.BotClient;
import backend.academy.linktracker.scrapper.dto.LinkUpdate;
import backend.academy.linktracker.scrapper.model.Chat;
import backend.academy.linktracker.scrapper.repository.LinksRepository;
import backend.academy.linktracker.scrapper.service.api.ScrapingApiService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final LinksRepository linksRepository;

    private final BotClient botClient;
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
                log.atWarn().addKeyValue("link", url).log("api service not exists");
                continue;
            }

            log.atInfo().addKeyValue("link", link).log("check link");

            try {
                var updatedAt = apiService.getLastUpdate(link);
                var lastUpdate = link.getLastUpdate();
                if (updatedAt != null && updatedAt.isAfter(lastUpdate)) {
                    for (var description : apiService.getChangesDescriptions(link, lastUpdate)) {
                        botClient.update(LinkUpdate.builder()
                                .id(link.getId())
                                .url(link.getUrl())
                                .description(description)
                                .tgChatIds(link.getChats().stream().map(Chat::getId).toList())
                                .build());
                    }
                    link.setLastUpdate(updatedAt);
                    linksRepository.save(link);
                }
            } catch (Exception e) {
                log.atError().addKeyValue("exception", e.getMessage()).log();
                botClient.update(LinkUpdate.builder()
                        .id(link.getId())
                        .url(link.getUrl())
                        .description("Ошибка обработки!")
                        .tgChatIds(link.getChats().stream().map(Chat::getId).toList())
                        .build());
            }
        }
    }
}
