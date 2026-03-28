package backend.academy.linktracker.scrapper.client;

import org.springframework.web.service.annotation.PostExchange;

import backend.academy.linktracker.scrapper.dto.ApiErrorResponse;
import backend.academy.linktracker.scrapper.dto.LinkUpdate;
import org.springframework.web.bind.annotation.RequestBody;

public interface BotClient {

    @PostExchange("/updates")
    ApiErrorResponse update(@RequestBody LinkUpdate linkUpdate);
}
