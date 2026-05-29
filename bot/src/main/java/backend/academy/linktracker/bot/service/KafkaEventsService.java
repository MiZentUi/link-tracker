package backend.academy.linktracker.bot.service;

import backend.academy.linktracker.bot.dto.LinkUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaEventsService {
    private final BotService botService;

    @KafkaListener(topics = "${app.kafka.topic.link-update.name}")
    public void listenLinkUpdate(LinkUpdate update) {
        botService.sendLinkUpdate(update);
    }
}
