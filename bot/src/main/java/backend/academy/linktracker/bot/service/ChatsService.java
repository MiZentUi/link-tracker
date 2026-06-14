package backend.academy.linktracker.bot.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import backend.academy.linktracker.bot.client.ScrapperClient;
import backend.academy.linktracker.bot.exception.ApiErrorException;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatsService {
    private final ScrapperClient client;

    @TimeLimiter(name = "chats-service")
    public CompletableFuture<Void> createChat(Long id) {
        return CompletableFuture.runAsync(() -> {
            try {
                client.createChat(id);
            } catch (ApiErrorException e) {
                log.atInfo().addKeyValue("exception", e.getMessage()).log("start api error");
            }
        });
    }
}
