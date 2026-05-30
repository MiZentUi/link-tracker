package backend.academy.linktracker.scrapper.service.sender;

import backend.academy.linktracker.scrapper.client.BotClient;
import backend.academy.linktracker.scrapper.dto.LinkUpdate;
import backend.academy.linktracker.scrapper.model.Chat;
import backend.academy.linktracker.scrapper.model.Link;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "app", name = "sender", havingValue = "http")
@RequiredArgsConstructor
public class BotClientSender implements MessageSender {
    private final BotClient client;

    @Override
    public void sendLinkUpdate(Link link, String description) {
        client.update(LinkUpdate.builder()
                .id(link.getId())
                .url(link.getUrl())
                .description(description)
                .tgChatIds(link.getChats().stream().map(Chat::getId).toList())
                .build());
    }
}
