package backend.academy.linktracker.bot.handler;

import backend.academy.linktracker.bot.model.Session;
import backend.academy.linktracker.bot.state.SessionState;
import backend.academy.linktracker.bot.state.StateFactory;
import com.pengrad.telegrambot.model.Update;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HelpHandler implements CommandHandler {
    private final StateFactory stateFactory;
    private final List<CommandHandler> handlers;

    @Override
    public String getCommand() {
        return "/help";
    }

    @Override
    public String getDescription() {
        return "описание команд";
    }

    @Override
    public SessionState getState(Session session) {
        return stateFactory.getDefaultState(session);
    }

    @Override
    public String handle(Update update) {
        return handlers.stream()
                .map(h -> h.getCommand() + " - " + h.getDescription() + "\n")
                .collect(Collectors.joining());
    }
}
