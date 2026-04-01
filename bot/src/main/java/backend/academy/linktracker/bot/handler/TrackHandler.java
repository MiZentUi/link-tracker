package backend.academy.linktracker.bot.handler;

import backend.academy.linktracker.bot.client.ScrapperClient;
import backend.academy.linktracker.bot.dto.AddLinkRequest;
import backend.academy.linktracker.bot.exception.ApiErrorException;
import backend.academy.linktracker.bot.model.Session;
import backend.academy.linktracker.bot.state.SessionState;
import backend.academy.linktracker.bot.state.StateFactory;
import com.pengrad.telegrambot.model.Update;
import java.util.ArrayList;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrackHandler implements CommandHandler {
    private final StateFactory stateFactory;
    private final ScrapperClient scrapperClient;

    @Override
    public String getCommand() {
        return "/track";
    }

    @Override
    public String getDescription() {
        return "начать отслеживание ссылки";
    }

    @Override
    public SessionState getState(Session session) {
        return stateFactory.getTrackState(session);
    }

    @Override
    public String handle(Update update) {
        return "Введите ссылку для отслеживания";
    }

    public String handleLink(Update update, AddLinkRequest linkRequest) {
        linkRequest.setLink(update.message().text());
        return "Введите теги (необязательно, \".\" для пропуска): ";
    }

    public String handleTags(Update update, AddLinkRequest linkRequest) {
        var chatId = update.message().chat().id();
        var text = update.message().text();
        var tags = new ArrayList<String>();
        if (!text.equals(".")) {
            tags.addAll(Arrays.asList(text.split(",+")));
        }
        log.atInfo()
                .addKeyValue(
                        "tags",
                        tags.stream().reduce((a, b) -> a + " " + b + " ").orElse(null))
                .log();
        linkRequest.setTags(tags);
        try {
            scrapperClient.createLink(chatId, linkRequest);
        } catch (ApiErrorException e) {
            var status = e.getStatusCode();
            return switch (status) {
                case HttpStatus.CONFLICT -> "Ссылка уже отслеживается";
                case HttpStatus.NOT_FOUND -> "Чат не зарегестрирован. Введите /start для регистрации";
                default -> e.getMessage();
            };
        }
        log.atInfo().addKeyValue("link", linkRequest.getLink()).log("link added");
        return "Ссылка добавлена";
    }
}
