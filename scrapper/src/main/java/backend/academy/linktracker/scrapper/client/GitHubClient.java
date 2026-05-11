package backend.academy.linktracker.scrapper.client;

import backend.academy.linktracker.scrapper.dto.GitHubIssueResponse;
import backend.academy.linktracker.scrapper.dto.GitHubRepoResponse;
import edu.umd.cs.findbugs.annotations.NonNull;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

@Validated
public interface GitHubClient {

    @GetExchange("/repos/{owner}/{repo}")
    GitHubRepoResponse repos(@NotNull @PathVariable String owner, @NonNull @PathVariable String repo);

    @GetExchange("/repos/{owner}/{repo}/issues")
    List<GitHubIssueResponse> repoIssues(
            @NotNull @PathVariable String owner,
            @NotNull @PathVariable String repo,
            @NotNull @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime since,
            @PathVariable String state,
            @PathVariable String sort,
            @PathVariable String direction);
}
