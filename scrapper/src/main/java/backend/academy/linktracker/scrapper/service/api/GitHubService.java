package backend.academy.linktracker.scrapper.service.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import backend.academy.linktracker.scrapper.client.GitHubClient;
import backend.academy.linktracker.scrapper.model.Link;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class GitHubService implements ApiService {
    private GitHubClient client;

    @Override
    public String getBaseUrl() {
        return "https://github.com";
    }

    @Override
    public LocalDateTime getLastUpdate(Link link) {
        try {
            var repoParts = new URI(link.getUrl()).getPath().replaceFirst("/", "").split("/+");
            return client.repos(repoParts[0], repoParts[1]).getUpdatedAt();
        } catch (URISyntaxException e) {
            new RuntimeException(e);
        }
        return null;
    }
}
