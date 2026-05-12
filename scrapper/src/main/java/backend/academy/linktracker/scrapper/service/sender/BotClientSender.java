package backend.academy.linktracker.scrapper.service.sender;

import backend.academy.linktracker.scrapper.client.BotClient;
import backend.academy.linktracker.scrapper.dto.LinkUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BotClientSender implements MessageSender {
    private final BotClient client;

    @Override
    public void sendLinkUpdate(LinkUpdate linkUpdate) {
        client.update(linkUpdate);
    }
}
