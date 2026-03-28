package backend.academy.linktracker.scrapper.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import backend.academy.linktracker.scrapper.client.BotClient;
import backend.academy.linktracker.scrapper.dto.LinkResponse;
import backend.academy.linktracker.scrapper.dto.LinkUpdate;
import backend.academy.linktracker.scrapper.dto.ListLinksResponse;
import backend.academy.linktracker.scrapper.exception.ChatNotFoundException;
import backend.academy.linktracker.scrapper.exception.LinkAlreadyExistsException;
import backend.academy.linktracker.scrapper.exception.LinkNotFoundException;
import backend.academy.linktracker.scrapper.model.Link;
import backend.academy.linktracker.scrapper.repository.ChatsRepository;
import backend.academy.linktracker.scrapper.repository.LinksRepository;
import backend.academy.linktracker.scrapper.service.api.ApiService;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@Validated
public class LinksService {
    private LinksRepository linksRepository;
    private ChatsRepository chatsRepository;

    private BotClient botClient;
    private List<ApiService> apiServices;

    public ListLinksResponse getListResponse(Long chatId) {
        if (!chatsRepository.exists(chatId)) {
            throw new ChatNotFoundException(String.format("Chat id=%d not exists in repository", chatId));
        }
        var linkResponses = linksRepository.findAllByChatId(chatId).stream()
                .map(l -> new LinkResponse(l.getId(), l.getUrl(), new ArrayList<>(l.getTags()))).toList();
        return new ListLinksResponse(linkResponses, linkResponses.size());
    }

    public Link addLink(@NotNull String url, @NotNull List<String> tags, @NotNull Long chatId) {
        if (!chatsRepository.exists(chatId)) {
            throw new ChatNotFoundException(String.format("Chat id=%d not exists in repository", chatId));
        }
        var link = linksRepository.findByUrl(url);
        if (link == null) {
            link = Link.builder().url(url).tags(new HashSet<>(tags)).chatIds(new ArrayList<>(List.of(chatId))).build();
            linksRepository.save(link);
        } else {
            if (link.getChatIds().contains(chatId)) {
                throw new LinkAlreadyExistsException("Link is already being tracked on chat (id=" + chatId + ")");
            }
            link.getTags().addAll(tags);
            link.getChatIds().add(chatId);
        }
        return link;
    }

    public Link removeLink(String url, Long chatId) {
        if (!chatsRepository.exists(chatId)) {
            throw new ChatNotFoundException(String.format("Chat id=%d not exists in repository", chatId));
        }
        var link = linksRepository.findByUrl(url);
        if (link == null) {
            throw new LinkNotFoundException(String.format("Link with url=%s not exists in repository", url));
        } else {
            link.getChatIds().remove(chatId);
            if (link.getChatIds().isEmpty()) {
                linksRepository.remove(link);
            }
        }
        return link;
    }

    @Scheduled(fixedDelay = 10, timeUnit = TimeUnit.SECONDS)
    public void checkUpdates() {
        var links = linksRepository.findAll();
        for (var link : links) {
            var url = link.getUrl();
            var apiService = apiServices.stream().filter(s -> url.contains(s.getBaseUrl())).findAny().orElseThrow();

            var updatedAt = apiService.getLastUpdate(link);

            if (updatedAt != null && updatedAt.isAfter(link.getLastUpdate())) {
                botClient.update(LinkUpdate.builder().id(link.getId()).url(link.getUrl())
                        .description("Что-то изменилось!").tgChatIds(link.getChatIds()).build());
                link.setLastUpdate(updatedAt);
            }
        }
    }
}
