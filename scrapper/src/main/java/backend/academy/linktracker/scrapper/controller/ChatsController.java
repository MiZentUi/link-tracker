package backend.academy.linktracker.scrapper.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import backend.academy.linktracker.scrapper.service.ChatsService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/tg-chat")
@AllArgsConstructor
public class ChatsController {
    private final ChatsService service;

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void addChatId(@NotNull @PositiveOrZero @PathVariable Long id) {
        service.addId(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteChatId(@NotNull @PositiveOrZero @PathVariable Long id) {
        service.removeId(id);
    }
}
