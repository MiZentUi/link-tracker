package backend.academy.linktracker.scrapper.config;

import backend.academy.linktracker.scrapper.properties.LinkUpdateTopicProperties;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@ConditionalOnProperty(prefix = "app.kafka", name = "enabled", havingValue = "true", matchIfMissing = true)
public class KafkaConfig {

    @Bean
    NewTopic linkUpdateTopic(LinkUpdateTopicProperties properties) {
        return TopicBuilder.name(properties.getName())
                .partitions(properties.getPartitionsCount())
                .replicas(properties.getReplicas())
                .build();
    }
}
