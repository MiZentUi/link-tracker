package backend.academy.linktracker.scrapper.service;

import backend.academy.linktracker.scrapper.dto.LinkResponse;
import backend.academy.linktracker.scrapper.dto.ListLinksResponse;
import backend.academy.linktracker.scrapper.exception.ChatNotFoundException;
import backend.academy.linktracker.scrapper.exception.LinkAlreadyExistsException;
import backend.academy.linktracker.scrapper.exception.LinkNotFoundException;
import backend.academy.linktracker.scrapper.model.Link;
import backend.academy.linktracker.scrapper.model.Tag;
import backend.academy.linktracker.scrapper.repository.ChatsRepository;
import backend.academy.linktracker.scrapper.repository.LinksRepository;
import backend.academy.linktracker.scrapper.repository.TagsRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;

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
    private final TagsRepository tagsRepository;

    public ListLinksResponse getListResponse(Long chatId) {
        var chat = chatsRepository.findById(chatId).orElseThrow(
                () -> new ChatNotFoundException(String.format("Chat id=%d not exists in repository", chatId)));
        var links = chat.getLinks().stream()
                .map(l -> new LinkResponse(l.getId(), l.getUrl(), l.getTags().stream().map(Tag::getName).toList()))
                .toList();
        log.atInfo()
                .addKeyValue("links", links.stream().map(LinkResponse::getUrl).reduce((a, b) -> a + " " + b + " "))
                .log("links size: {}", links.size());
        return new ListLinksResponse(links, links.size());
    }

    @Transactional
    public LinkResponse addLink(@NotNull String url, @NotNull List<String> tags, @NotNull Long chatId) {
        var link = linksRepository.findByUrl(url);
        var chat = chatsRepository.findById(chatId).orElseThrow(
                () -> new ChatNotFoundException(String.format("Chat id=%d not exists in repository", chatId)));
        log.atInfo()
                .addKeyValue("url", url)
                .addKeyValue("tags", tags)
                .addKeyValue("chatId", chatId)
                .log("try add link");
        if (link == null) {
            link = Link.builder()
                    .url(url)
                    .chats(new HashSet<>())
                    .build();
            linksRepository.save(link);
        } else {
            if (link.getChats().stream().anyMatch(c -> c.getId().equals(chat.getId()))) {
                throw new LinkAlreadyExistsException("Link is already being tracked on chat (id=" + chatId + ")");
            }
        }
        link.getChats().add(chat);
        for (var tag : tags) {
            tagsRepository.save(Tag.builder()
                    .chat(chat)
                    .link(link)
                    .name(tag)
                    .build());
        }
        linksRepository.save(link);
        if (link.getTags() == null) {
            return new LinkResponse(link.getId(), link.getUrl(), tags);
        }
        return new LinkResponse(link.getId(), link.getUrl(), link.getTags().stream().map(Tag::getName).toList());
    }

    @Transactional
    public LinkResponse removeLink(String url, Long chatId) {
        var link = linksRepository.findByUrl(url);
        log.atInfo().addKeyValue("url", url).addKeyValue("chatId", chatId).log("try remove link");
        if (link == null) {
            throw new LinkNotFoundException(String.format("Link with url=%s not exists in repository", url));
        } else {
            link.getChats().removeIf(c -> c.getId().equals(chatsRepository.findById(chatId).orElseThrow(
                    () -> new ChatNotFoundException(String.format("Chat id=%d not exists in repository", chatId)))
                    .getId()));
            if (link.getChats().isEmpty()) {
                for (var tag : link.getTags()) {
                    tagsRepository.delete(tag);
                }
                linksRepository.delete(link);
            } else {
                linksRepository.save(link);
            }
        }
        return new LinkResponse(link.getId(), link.getUrl(), link.getTags().stream().map(Tag::getName).toList());
    }
}
