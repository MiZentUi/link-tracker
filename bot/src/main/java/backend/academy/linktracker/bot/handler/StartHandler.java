package backend.academy.linktracker.bot.handler;

import backend.academy.linktracker.bot.exception.ApiErrorException;
import backend.academy.linktracker.bot.model.Session;
import backend.academy.linktracker.bot.service.ChatsService;
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
    private final ChatsService chatService;

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
        chatService.createChat(chatId);
        return "Добро пожаловать! Используйте /help, чтобы посмотреть доступные команды.";
    }
}
