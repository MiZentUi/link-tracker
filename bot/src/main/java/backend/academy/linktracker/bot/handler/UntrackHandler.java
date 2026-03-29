package backend.academy.linktracker.bot.handler;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;

import backend.academy.linktracker.bot.client.ScrapperClient;
import backend.academy.linktracker.bot.dto.RemoveLinkRequest;
import backend.academy.linktracker.bot.exception.ApiErrorException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UntrackHandler implements CommandHandler {
    enum State {
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
            return new SendMessage(chatId, "Отмена...");
        }

        switch (state) {
            case UNKNOWN:
                state = State.GET_LINK;
                return new SendMessage(chatId, "Введите ссылку для прекращения отслеживания");
            case GET_LINK:
                state = State.UNKNOWN;
                try {
                    scrapperClient.deleteLink(chatId, new RemoveLinkRequest(text));
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
                return new SendMessage(chatId, "Ссылка теперь не отслеживается");
        }

        return null;
    }

    @Override
    public boolean isDone() {
        return state == State.UNKNOWN;
    }
}
