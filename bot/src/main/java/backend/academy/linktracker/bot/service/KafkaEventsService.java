package backend.academy.linktracker.bot.service;

import backend.academy.linktracker.event.LinkUpdateEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaEventsService {
    private final BotService botService;

    @KafkaListener(topics = "${app.kafka.topic.link-update.name}")
    public void listenLinkUpdateEvent(LinkUpdateEvent update) {
        botService.sendLinkUpdateEvent(update);
    }
}
