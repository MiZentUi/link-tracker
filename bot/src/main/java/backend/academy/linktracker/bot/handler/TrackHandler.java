package backend.academy.linktracker.bot.handler;

import backend.academy.linktracker.bot.client.ScrapperClient;
import backend.academy.linktracker.bot.dto.AddLinkRequest;
import backend.academy.linktracker.bot.exception.ApiErrorException;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
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
    enum State {
        UNKNOWN,
        GET_LINK,
        GET_TAGS
    }

    private final ScrapperClient scrapperClient;
    private State state = State.UNKNOWN;
    private AddLinkRequest linkRequest;

    @Override
    public String getCommand() {
        return "/track";
    }

    @Override
    public String getDescription() {
        return "начать отслеживание ссылки";
    }

    @Override
    public SendMessage handle(Update update) {
        long chatId = update.message().chat().id();
        var text = update.message().text();

        if (text.startsWith("/cancel")) {
            state = State.UNKNOWN;
            log.info("track calcelation");
            return new SendMessage(chatId, "Отмена!");
        }

        log.info("current state: {}", state);

        switch (state) {
            case UNKNOWN:
                state = State.GET_LINK;
                linkRequest = new AddLinkRequest();
                return new SendMessage(chatId, "Введите ссылку для отслеживания");
            case GET_LINK:
                linkRequest.setLink(text);
                state = State.GET_TAGS;
                return new SendMessage(chatId, "Введите теги (необязательно, \".\" для пропуска): ");
            case GET_TAGS:
                var tags = new ArrayList<String>();
                if (!text.equals(".")) {
                    tags.addAll(Arrays.asList(text.split(",+")));
                }
                log.atInfo()
                        .addKeyValue("tags", tags)
                        .log(
                                "tags: {}",
                                tags.stream()
                                        .reduce((a, b) -> a + " " + b + " ")
                                        .orElse(null));
                linkRequest.setTags(tags);
                state = State.UNKNOWN;
                try {
                    scrapperClient.createLink(chatId, linkRequest);
                } catch (ApiErrorException e) {
                    var status = e.getStatusCode();
                    switch (status) {
                        case HttpStatus.CONFLICT:
                            return new SendMessage(chatId, "Ссылка уже отслеживается");
                        case HttpStatus.NOT_FOUND:
                            return new SendMessage(chatId, "Чат не зарегестрирован. Введите /start для регистрации");
                        default:
                            return new SendMessage(chatId, e.getMessage());
                    }
                }
                log.atInfo().addKeyValue("link", linkRequest).log("link added: {}", linkRequest.getLink());
                return new SendMessage(chatId, "Ссылка добавлена");
        }

        return null;
    }

    @Override
    public boolean isDone() {
        return state == State.UNKNOWN;
    }
}
