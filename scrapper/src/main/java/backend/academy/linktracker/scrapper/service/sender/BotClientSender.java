package backend.academy.linktracker.scrapper.service.sender;

import org.springframework.stereotype.Service;

import backend.academy.linktracker.scrapper.client.BotClient;
import backend.academy.linktracker.scrapper.dto.LinkUpdate;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BotClientSender implements MessageSender {
    private final BotClient client;

    @Override
    public void sendLinkUpdate(LinkUpdate linkUpdate) {
        client.update(linkUpdate);
    }
}
