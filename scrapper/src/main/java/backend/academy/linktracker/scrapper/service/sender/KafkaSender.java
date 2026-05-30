package backend.academy.linktracker.scrapper.service.sender;

import backend.academy.linktracker.event.LinkUpdateEvent;
import backend.academy.linktracker.scrapper.model.Chat;
import backend.academy.linktracker.scrapper.model.Link;
import backend.academy.linktracker.scrapper.properties.LinkUpdateTopicProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "app", name = "sender", havingValue = "kafka", matchIfMissing = true)
@RequiredArgsConstructor
public class KafkaSender implements MessageSender {
    private final KafkaTemplate<String, LinkUpdateEvent> template;
    private final LinkUpdateTopicProperties properties;

    @Override
    public void sendLinkUpdate(Link link, String description) {
        template.send(
                properties.getName(),
                LinkUpdateEvent.newBuilder()
                        .setId(link.getId())
                        .setUrl(link.getUrl())
                        .setDescription(description)
                        .setTgChatIds(link.getChats().stream().map(Chat::getId).toList())
                        .build());
    }
}
