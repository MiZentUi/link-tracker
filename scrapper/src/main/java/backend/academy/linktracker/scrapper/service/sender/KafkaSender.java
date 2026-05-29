package backend.academy.linktracker.scrapper.service.sender;

import backend.academy.linktracker.scrapper.dto.LinkUpdate;
import backend.academy.linktracker.scrapper.properties.LinkUpdateTopicProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "app", name = "sender", havingValue = "kafka", matchIfMissing = true)
@RequiredArgsConstructor
public class KafkaSender implements MessageSender {
    private final KafkaTemplate<String, LinkUpdate> template;
    private final LinkUpdateTopicProperties properties;

    @Override
    public void sendLinkUpdate(LinkUpdate linkUpdate) {
        template.send(properties.getName(), linkUpdate);
    }
}
