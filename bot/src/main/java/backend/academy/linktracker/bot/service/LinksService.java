package backend.academy.linktracker.bot.service;

import backend.academy.linktracker.bot.client.ScrapperClient;
import backend.academy.linktracker.bot.dto.AddLinkRequest;
import backend.academy.linktracker.bot.dto.LinkResponse;
import backend.academy.linktracker.bot.dto.RemoveLinkRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LinksService {
    private final ScrapperClient scrapperClient;

    @Cacheable(value = "links", key = "#chatId")
    public List<LinkResponse> getLinks(Long chatId) {
        return scrapperClient.getLinks(chatId).getLinks();
    }

    @CacheEvict(value = "links", key = "#chatId")
    public LinkResponse track(Long chatId, AddLinkRequest request) {
        return scrapperClient.createLink(chatId, request);
    }

    @CacheEvict(value = "links", key = "#chatId")
    public LinkResponse untrack(Long chatId, String link) {
        var request = new RemoveLinkRequest(link);
        return scrapperClient.deleteLink(chatId, request);
    }
}
