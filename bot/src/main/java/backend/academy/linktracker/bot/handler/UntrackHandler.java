package backend.academy.linktracker.bot.handler;

import backend.academy.linktracker.bot.client.ScrapperClient;
import backend.academy.linktracker.bot.dto.RemoveLinkRequest;
import backend.academy.linktracker.bot.exception.ApiErrorException;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UntrackHandler implements CommandHandler {
    private enum State {
        UNKNOWN,
        GET_LINK
    }

    private final ScrapperClient scrapperClient;
    private State state = State.UNKNOWN;

    @Override
    public String getCommand() {
        return "/untrack";
    }

    @Override
    public String getDescription() {
        return "прекратить отслеживание ссылки";
    }

    @Override
    public SendMessage handle(Update update) {
        long chatId = update.message().chat().id();
        var text = update.message().text();

        if (text.startsWith("/cancel")) {
            state = State.UNKNOWN;
            log.info("untrack calcelation");
            return new SendMessage(chatId, "Отмена");
        }

        log.atInfo().addKeyValue("state", state).log("current state");

        switch (state) {
            case UNKNOWN -> {
                state = State.GET_LINK;
                return new SendMessage(chatId, "Введите ссылку для прекращения отслеживания");
            }
            case GET_LINK -> {
                state = State.UNKNOWN;
                var linkRequest = new RemoveLinkRequest(text);
                try {
                    scrapperClient.deleteLink(chatId, linkRequest);
                } catch (ApiErrorException e) {
                    var status = e.getStatusCode();
                    return switch (status) {
                        case HttpStatus.CONFLICT -> new SendMessage(chatId, "Ссылка уже отслеживается");
                        case HttpStatus.NOT_FOUND ->
                            new SendMessage(chatId, "Чат не зарегестрирован. Введите /start для регистрации");
                        default -> new SendMessage(chatId, e.getMessage());
                    };
                }
                log.atInfo().addKeyValue("link", linkRequest.getLink()).log("link removed");
                return new SendMessage(chatId, "Ссылка теперь не отслеживается");
            }
        }

        return null;
    }

    @Override
    public boolean isDone() {
        return state == State.UNKNOWN;
    }
}
