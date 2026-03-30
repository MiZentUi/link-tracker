package backend.academy.linktracker.scrapper.service.api;

import backend.academy.linktracker.scrapper.client.GitHubClient;
import backend.academy.linktracker.scrapper.model.Link;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitHubService implements ScrapingApiService {
    private final GitHubClient client;

    @Override
    public String getBaseUrl() {
        return "https://github.com";
    }

    @Override
    public LocalDateTime getLastUpdate(Link link) {
        try {
            var repoParts =
                    new URI(link.getUrl()).getPath().replaceFirst("/", "").split("/+");
            var owner = repoParts[0];
            var repo = repoParts[1];
            return client.repos(owner, repo).getUpdatedAt();
        } catch (URISyntaxException e) {
            log.atError().addKeyValue("exception", e.getMessage()).log();
        }
        return null;
    }
}
