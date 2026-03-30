package backend.academy.linktracker.scrapper.service;

import backend.academy.linktracker.scrapper.client.BotClient;
import backend.academy.linktracker.scrapper.dto.LinkUpdate;
import backend.academy.linktracker.scrapper.repository.LinksRepository;
import backend.academy.linktracker.scrapper.service.api.ScrapingApiService;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final LinksRepository linksRepository;

    private final BotClient botClient;
    private final List<ScrapingApiService> apiServices;

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

            var updatedAt = apiService.getLastUpdate(link);

            if (updatedAt != null && updatedAt.isAfter(link.getLastUpdate())) {
                botClient.update(LinkUpdate.builder()
                        .id(link.getId())
                        .url(link.getUrl())
                        .description("Что-то изменилось!")
                        .tgChatIds(link.getChatIds())
                        .build());
                link.setLastUpdate(updatedAt);
            }
        }
    }
}
