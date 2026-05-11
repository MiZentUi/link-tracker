package backend.academy.linktracker.scrapper.service.sender;

import backend.academy.linktracker.scrapper.dto.LinkUpdate;

public interface MessageSender {
    void sendLinkUpdate(LinkUpdate linkUpdate);
}
