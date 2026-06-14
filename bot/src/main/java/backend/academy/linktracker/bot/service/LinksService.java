package backend.academy.linktracker.bot.service;

import backend.academy.linktracker.bot.client.ScrapperClient;
import backend.academy.linktracker.bot.dto.AddLinkRequest;
import backend.academy.linktracker.bot.dto.LinkResponse;
import backend.academy.linktracker.bot.dto.RemoveLinkRequest;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LinksService {
    private final ScrapperClient scrapperClient;

    @Cacheable(value = "links", key = "#chatId")
    @TimeLimiter(name = "links-service")
    @Retry(name = "links-service")
    public CompletableFuture<List<LinkResponse>> getLinks(Long chatId) {
        return CompletableFuture.supplyAsync(() -> scrapperClient.getLinks(chatId).getLinks());
    }

    @CacheEvict(value = "links", key = "#chatId")
    @TimeLimiter(name = "links-service")
    @Retry(name = "links-service")
    public CompletableFuture<LinkResponse> track(Long chatId, AddLinkRequest request) {
        return CompletableFuture.supplyAsync(() -> scrapperClient.createLink(chatId, request));
    }

    @CacheEvict(value = "links", key = "#chatId")
    @TimeLimiter(name = "links-service")
    @Retry(name = "links-service")
    public CompletableFuture<LinkResponse> untrack(Long chatId, String link) {
        var request = new RemoveLinkRequest(link);
        return CompletableFuture.supplyAsync(() -> scrapperClient.deleteLink(chatId, request));
    }
}
