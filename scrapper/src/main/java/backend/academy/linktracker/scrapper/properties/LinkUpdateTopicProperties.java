package backend.academy.linktracker.scrapper.properties;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "app.kafka.topic.link-update")
@Validated
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class LinkUpdateTopicProperties {

    @NotEmpty
    private String name;

    @Positive
    private Integer partitionsCount;

    @PositiveOrZero
    private Integer replicas;
}
