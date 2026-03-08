package backend.academy.linktracker.bot.controller;

import java.util.Arrays;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

import backend.academy.linktracker.bot.dto.ApiErrorResponse;
import backend.academy.linktracker.bot.dto.LinkUpdate;
import backend.academy.linktracker.bot.service.BotService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@RestController
@AllArgsConstructor
public class UpdatesController {
    private BotService botService;

    @PostMapping("/updates")
    @ResponseStatus(HttpStatus.OK)
    public void updates(@Valid @RequestBody LinkUpdate update) {
        botService.sendLinkUpdate(update);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse wrongRequestArguments(MethodArgumentNotValidException exception) {
        return ApiErrorResponse.builder()
                .description("Wrong request arguments")
                .code(HttpStatus.BAD_REQUEST.toString())
                .exceptionName(exception.getClass().getCanonicalName())
                .exceptionMessage(exception.getMessage())
                .stacktrace(Arrays.stream(exception.getStackTrace()).map(StackTraceElement::toString).toList())
                .build();
    }
}
