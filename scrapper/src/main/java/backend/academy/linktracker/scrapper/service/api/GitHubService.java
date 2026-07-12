package backend.academy.linktracker.scrapper.service.api;

import backend.academy.linktracker.scrapper.client.GitHubClient;
import backend.academy.linktracker.scrapper.exception.LinkException;
import backend.academy.linktracker.scrapper.model.Link;
import backend.academy.linktracker.scrapper.properties.GithubProperties;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitHubService implements ScrapingApiService {
    private final GitHubClient client;
    private final GithubProperties properties;

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
            throw new LinkException(e);
        }
    }

    @Override
    public List<String> getChangesDescriptions(Link link, OffsetDateTime since) {
        try {
            var repoParts =
                    new URI(link.getUrl()).getPath().replaceFirst("/", "").split("/+");
            var owner = repoParts[0];
            var repo = repoParts[1];
            var descriptions = new ArrayList<String>();
            var params = properties.getParams();
            for (var issue : client.repoIssues(
                    owner, repo, since, params.state(), params.sort(), params.direction(), params.perPage())) {
                var description = new StringBuilder();
                description
                        .append(issue.isPullRequest() ? "Pull Request" : "Issue")
                        .append("\n");
                description.append("Название: ").append(issue.getTitle()).append("\n");
                description
                        .append("Пользователь: ")
                        .append(issue.getUserLogin())
                        .append("\n");
                description
                        .append("Время создания: ")
                        .append(issue.getCreatedAt().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")))
                        .append("\n");
                var body = issue.getBody();
                if (body == null) {
                    log.atInfo().addKeyValue("title", issue.getTitle()).log("body is null");
                    body = "";
                }
                var maxPreviewLen = 200;
                description
                        .append("Превью: ")
                        .append(body.substring(0, Math.min(maxPreviewLen, body.length())))
                        .append(body.length() > maxPreviewLen ? "..." : "");
                descriptions.add(description.toString());
            }
            return descriptions;
        } catch (URISyntaxException e) {
            throw new LinkException(e);
        }
    }
}
