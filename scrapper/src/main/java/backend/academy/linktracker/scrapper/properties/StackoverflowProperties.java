package backend.academy.linktracker.scrapper.properties;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "app.stackoverflow")
@Validated
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class StackoverflowProperties {
    public record Params(String order, String sort, String answersFilter, String commentsFilter) {}

    @NotEmpty
    private String baseUrl;

    @NotEmpty
    private String key;

    @NotEmpty
    private String accessToken;

    @NotNull
    private Params params;
}
