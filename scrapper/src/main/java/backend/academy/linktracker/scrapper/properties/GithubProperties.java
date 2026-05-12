package backend.academy.linktracker.scrapper.properties;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "app.github")
@Validated
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class GithubProperties {
    public record Params(
            String state,
            String sort,
            String direction,
            int perPage) {
    }

    @NotEmpty
    private String baseUrl;

    @NotEmpty
    private String token;

    @NotNull
    private Params params;
}
