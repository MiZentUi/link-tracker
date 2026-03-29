package backend.academy.linktracker.scrapper.service.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import backend.academy.linktracker.scrapper.client.GitHubClient;
import backend.academy.linktracker.scrapper.model.Link;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
            var owner = repoParts[0];
            var repo = repoParts[1];
            return client.repos(owner, repo).getUpdatedAt();
        } catch (URISyntaxException e) {
            log.atError().addKeyValue("exception", e).log(e.getMessage());
        }
        return null;
    }
}
