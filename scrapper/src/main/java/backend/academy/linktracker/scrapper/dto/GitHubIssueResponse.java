package backend.academy.linktracker.scrapper.dto;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubIssueResponse {
    private String title;
    private String body;
    private String userLogin;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonIgnore
    @Getter(AccessLevel.NONE)
    private boolean pullRequest;

    @JsonProperty("user")
    private void unpackUserLogin(Map<String, Object> user) {
        userLogin = (String) user.get("login");
    }

    @JsonProperty("pull_request")
    private void checkPullRequest(Map<String, Object> pullRequest) {
        this.pullRequest = pullRequest != null;
    }

    public boolean isPullRequest() {
        return pullRequest;
    }
}
