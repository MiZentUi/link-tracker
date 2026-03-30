package backend.academy.linktracker.scrapper.service.api;

import backend.academy.linktracker.scrapper.model.Link;
import java.time.LocalDateTime;

public interface ScrapingApiService {
    String getBaseUrl();

    LocalDateTime getLastUpdate(Link link);
}
