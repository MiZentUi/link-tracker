package backend.academy.linktracker.bot.handler;

import backend.academy.linktracker.bot.client.ScrapperClient;
import backend.academy.linktracker.bot.dto.RemoveLinkRequest;
import backend.academy.linktracker.bot.exception.ApiErrorException;
import backend.academy.linktracker.bot.model.Session;
import backend.academy.linktracker.bot.state.SessionState;
import backend.academy.linktracker.bot.state.StateFactory;
import com.pengrad.telegrambot.model.Update;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UntrackHandler implements CommandHandler {
    private final StateFactory stateFactory;
    private final ScrapperClient scrapperClient;

    @Override
    public String getCommand() {
        return "/untrack";
    }

    @Override
    public String getDescription() {
        return "прекратить отслеживание ссылки";
    }

    @Override
    public SessionState getState(Session session) {
        return stateFactory.getUntrackState(session);
    }

    @Override
    public String handle(Update update) {
        return "Введите ссылку для прекращения отслеживания";
    }

    public String handleLink(Update update) {
        long chatId = update.message().chat().id();
        var text = update.message().text();
        var linkRequest = new RemoveLinkRequest(text);
        try {
            scrapperClient.deleteLink(chatId, linkRequest);
        } catch (ApiErrorException e) {
            var status = e.getStatusCode();
            return switch (status) {
                case HttpStatus.CONFLICT -> "Ссылка уже отслеживается";
                case HttpStatus.NOT_FOUND -> "Чат не зарегестрирован. Введите /start для регистрации";
                default -> e.getMessage();
            };
        }
        log.atInfo().addKeyValue("link", linkRequest.getLink()).log("link removed");
        return "Ссылка теперь не отслеживается";
    }
}
