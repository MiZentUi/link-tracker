package backend.academy.linktracker.bot.handler;

import backend.academy.linktracker.bot.model.Session;
import backend.academy.linktracker.bot.state.SessionState;
import backend.academy.linktracker.bot.state.StateFactory;
import com.pengrad.telegrambot.model.Update;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CancelHandler implements CommandHandler {
    private final StateFactory stateFactory;

    @Override
    public String getCommand() {
        return "/cancel";
    }

    @Override
    public String getDescription() {
        return "отмена диалога команды";
    }

    @Override
    public SessionState getState(Session session) {
        return stateFactory.getDefaultState(session);
    }

    @Override
    public String handle(Update update) {
        return "Отмена действия";
    }
}
