package backend.academy.linktracker.scrapper.service;

import backend.academy.linktracker.scrapper.dto.LinkResponse;
import backend.academy.linktracker.scrapper.dto.ListLinksResponse;
import backend.academy.linktracker.scrapper.exception.ChatNotFoundException;
import backend.academy.linktracker.scrapper.exception.LinkAlreadyExistsException;
import backend.academy.linktracker.scrapper.exception.LinkNotFoundException;
import backend.academy.linktracker.scrapper.model.Link;
import backend.academy.linktracker.scrapper.repository.ChatsRepository;
import backend.academy.linktracker.scrapper.repository.LinksRepository;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@RequiredArgsConstructor
@Validated
public class LinksService {
    private final LinksRepository linksRepository;
    private final ChatsRepository chatsRepository;

    public ListLinksResponse getListResponse(Long chatId) {
        if (!chatsRepository.exists(chatId)) {
            throw new ChatNotFoundException(String.format("Chat id=%d not exists in repository", chatId));
        }
        var links = linksRepository.findAllByChatId(chatId).stream()
                .map(l -> new LinkResponse(l.getId(), l.getUrl(), new ArrayList<>(l.getTags())))
                .toList();
        log.atInfo()
                .addKeyValue("links", links.stream().map(LinkResponse::getUrl).reduce((a, b) -> a + " " + b + " "))
                .log("links size: {}", links.size());
        return new ListLinksResponse(links, links.size());
    }

    public Link addLink(@NotNull String url, @NotNull List<String> tags, @NotNull Long chatId) {
        if (!chatsRepository.exists(chatId)) {
            throw new ChatNotFoundException(String.format("Chat id=%d not exists in repository", chatId));
        }
        var link = linksRepository.findByUrl(url);
        log.atInfo()
                .addKeyValue("url", url)
                .addKeyValue("tags", tags)
                .addKeyValue("chatId", chatId)
                .log("try add link");
        if (link == null) {
            link = Link.builder()
                    .url(url)
                    .tags(new HashSet<>(tags))
                    .chatIds(new ArrayList<>(List.of(chatId)))
                    .build();
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
        log.atInfo().addKeyValue("url", url).addKeyValue("chatId", chatId).log("try remove link");
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
}
