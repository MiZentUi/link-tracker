package backend.academy.linktracker.bot.handler;

import backend.academy.linktracker.bot.client.ScrapperClient;
import backend.academy.linktracker.bot.exception.ApiErrorException;
import backend.academy.linktracker.bot.model.Session;
import backend.academy.linktracker.bot.state.SessionState;
import backend.academy.linktracker.bot.state.StateFactory;
import com.pengrad.telegrambot.model.Update;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartHandler implements CommandHandler {
    private final StateFactory stateFactory;
    private final ScrapperClient scrapperClient;

    @Override
    public String getCommand() {
        return "/start";
    }

    @Override
    public String getDescription() {
        return "приветственное сообщение";
    }

    @Override
    public SessionState getState(Session session) {
        return stateFactory.getDefaultState(session);
    }

    @Override
    public String handle(Update update) {
        long chatId = update.message().chat().id();
        log.atInfo().addKeyValue("chat_id", chatId).log("start command");
        try {
            scrapperClient.createChat(chatId);
        } catch (ApiErrorException e) {
            log.atInfo().addKeyValue("exception", e.getMessage()).log("start api error");
        }
        return "Добро пожаловать! Используйте /help, чтобы посмотреть доступные команды.";
    }
}
