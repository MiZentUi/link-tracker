package backend.academy.linktracker.scrapper.controller;

import org.springframework.web.bind.annotation.RestController;

import backend.academy.linktracker.scrapper.dto.AddLinkRequest;
import backend.academy.linktracker.scrapper.dto.LinkResponse;
import backend.academy.linktracker.scrapper.dto.ListLinksResponse;
import backend.academy.linktracker.scrapper.dto.RemoveLinkRequest;
import backend.academy.linktracker.scrapper.service.LinksService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;

import java.util.ArrayList;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/links")
@AllArgsConstructor
public class LinksController {
    private LinksService linksService;

    @GetMapping
    public ListLinksResponse getLinks(@NotNull @PositiveOrZero @RequestHeader("Tg-Chat-Id") Long chatId) {
        return linksService.getListResponse(chatId);
    }

    @PostMapping
    public LinkResponse addLink(@NotNull @PositiveOrZero @RequestHeader("Tg-Chat-Id") Long chatId,
            @RequestBody AddLinkRequest request) {
        var link = linksService.addLink(request.getLink(), request.getTags(), chatId);
        return new LinkResponse(link.getId(), link.getUrl(), new ArrayList<>(link.getTags()));
    }

    @DeleteMapping
    public LinkResponse removeLink(@NotNull @PositiveOrZero @RequestHeader("Tg-Chat-Id") Long chatId,
            @RequestBody RemoveLinkRequest request) {
        var link = linksService.removeLink(request.getLink(), chatId);
        return new LinkResponse(link.getId(), link.getUrl(), new ArrayList<>(link.getTags()));
    }
}
