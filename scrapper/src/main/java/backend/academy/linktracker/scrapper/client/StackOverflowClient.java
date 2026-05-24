package backend.academy.linktracker.scrapper.client;

import backend.academy.linktracker.scrapper.dto.StackOverflowAnswerResponse;
import backend.academy.linktracker.scrapper.dto.StackOverflowCommentResponse;
import backend.academy.linktracker.scrapper.dto.StackOverflowItemsResponse;
import backend.academy.linktracker.scrapper.dto.StackOverflowQuestionResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

public interface StackOverflowClient {

    @GetExchange("/questions/{id}")
    StackOverflowItemsResponse<StackOverflowQuestionResponse> questions(
            @PathVariable int id,
            @RequestParam String site,
            @RequestParam String key,
            @RequestParam("access_token") String accessToken);

    @GetExchange("/questions/{id}/answers")
    StackOverflowItemsResponse<StackOverflowAnswerResponse> questionsAnswers(
            @PathVariable int id,
            @RequestParam Long fromdate,
            @RequestParam String site,
            @RequestParam String sort,
            @RequestParam String order,
            @RequestParam String filter,
            @RequestParam String key,
            @RequestParam("access_token") String accessToken);

    @GetExchange("/questions/{id}/comments")
    StackOverflowItemsResponse<StackOverflowCommentResponse> questionsComments(
            @PathVariable int id,
            @RequestParam Long fromdate,
            @RequestParam String site,
            @RequestParam String sort,
            @RequestParam String order,
            @RequestParam String filter,
            @RequestParam String key,
            @RequestParam("access_token") String accessToken);
}
