package backend.academy.linktracker.scrapper.client;

import backend.academy.linktracker.scrapper.dto.GitHubRepoResponse;
import edu.umd.cs.findbugs.annotations.NonNull;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

@Validated
public interface GitHubClient {

    @GetExchange("/repos/{owner}/{repo}")
    GitHubRepoResponse repos(@NotNull @PathVariable String owner, @NonNull @PathVariable String repo);
}
