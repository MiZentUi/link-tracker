package backend.academy.linktracker.scrapper.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotEmpty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ConfigurationProperties(prefix = "app.repository")
@Validated
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class RepositoryProperties {

    @NotEmpty
    private String accessType;
}