package backend.academy.linktracker.scrapper.client;

import backend.academy.linktracker.scrapper.dto.StackOverflowQuestionResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

public interface StackOverflowClient {

    @GetExchange("/questions/{id}")
    StackOverflowQuestionResponse questions(
            @PathVariable int id,
            @RequestParam String site,
            @RequestParam String key,
            @RequestParam String access_token);
}
