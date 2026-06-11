package backend.academy.linktracker.bot.properties;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "app.redis")
@Getter
@Setter
public class RedisCacheProperties {
    private List<String> nodes;
    private Long ttlDuration;
}
