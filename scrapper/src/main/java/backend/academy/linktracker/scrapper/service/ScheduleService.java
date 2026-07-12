package backend.academy.linktracker.scrapper.service;

import backend.academy.linktracker.scrapper.model.Link;
import backend.academy.linktracker.scrapper.properties.SchedulerProperties;
import backend.academy.linktracker.scrapper.repository.LinksRepository;
import backend.academy.linktracker.scrapper.service.api.ScrapingApiService;
import backend.academy.linktracker.scrapper.service.sender.MessageSender;
import jakarta.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final SchedulerProperties properties;

    private final LinksRepository linksRepository;

    private final MessageSender sender;
    private final List<ScrapingApiService> apiServices;

    @Transactional
    @Scheduled(fixedDelayString = "#{@schedulerProperties.updatesDelay}")
    public void checkUpdates() {
        var pageable = PageRequest.of(0, properties.getLinksPerPage());
        Slice<Link> links;
        do {
            links = linksRepository.findAll(pageable);
            updateSlice(links);
            pageable = pageable.next();
        } while (links.hasNext());
    }

    private void updateSlice(Slice<Link> links) {
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
                var lastUpdate = link.getLastUpdate();
                var descriptions = apiService.getChangesDescriptions(link, lastUpdate);
                for (var description : descriptions) {
                    sender.sendLinkUpdate(link, description);
                }
                if (!descriptions.isEmpty()) {
                    link.setLastUpdate(OffsetDateTime.now());
                    linksRepository.save(link);
                }
            } catch (Exception e) {
                log.atError().addKeyValue("exception", e.getMessage()).log();
                sender.sendLinkUpdate(link, "Ошибка обработки!");
                link.setLastUpdate(OffsetDateTime.now());
                linksRepository.save(link);
            }
        }
    }
}
