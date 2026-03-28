package backend.academy.linktracker.scrapper.service.api;

import java.time.LocalDateTime;

import backend.academy.linktracker.scrapper.model.Link;

public interface ApiService {
    String getBaseUrl();

    LocalDateTime getLastUpdate(Link link);
}
