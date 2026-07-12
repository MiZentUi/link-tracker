package backend.academy.linktracker.scrapper.service.sender;

import backend.academy.linktracker.scrapper.model.Link;

public interface MessageSender {
    void sendLinkUpdate(Link link, String description);
}
