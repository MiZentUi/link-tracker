package backend.academy.linktracker.scrapper.service.api;

import backend.academy.linktracker.scrapper.model.Link;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

public interface ScrapingApiService {
    String getBaseUrl();

    LocalDateTime getLastUpdate(Link link);

    List<String> getChangesDescriptions(Link link, OffsetDateTime since);
}
