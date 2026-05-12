package backend.academy.linktracker.scrapper.service.api;

import backend.academy.linktracker.scrapper.model.Link;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

public interface ScrapingApiService {
    String getBaseUrl();

    LocalDateTime getLastUpdate(Link link) throws URISyntaxException;

    List<String> getChangesDescriptions(Link link, OffsetDateTime since) throws URISyntaxException;
}
