package backend.academy.linktracker.scrapper.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import backend.academy.linktracker.scrapper.dto.LinkResponse;
import backend.academy.linktracker.scrapper.dto.ListLinksResponse;
import backend.academy.linktracker.scrapper.exception.ChatNotFoundException;
import backend.academy.linktracker.scrapper.exception.LinkAlreadyExistsException;
import backend.academy.linktracker.scrapper.exception.LinkNotFoundException;
import backend.academy.linktracker.scrapper.model.Link;
import backend.academy.linktracker.scrapper.repository.ChatsRepository;
import backend.academy.linktracker.scrapper.repository.LinksRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class LinksService {
    private LinksRepository linksRepository;
    private ChatsRepository chatsRepository;

    public ListLinksResponse getListResponse(Long chatId) {
        if (!chatsRepository.exists(chatId)) {
            throw new ChatNotFoundException(String.format("Chat id=%d not exists in repository", chatId));
        }
        var linkResponses = linksRepository.findAllByChatId(chatId).stream()
                .map(l -> new LinkResponse(l.getId(), l.getUrl(), new ArrayList<>(l.getTags()))).toList();
        return new ListLinksResponse(linkResponses, linkResponses.size());
    }

    public Link addLink(String url, List<String> tags, Long chatId) {
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
}
