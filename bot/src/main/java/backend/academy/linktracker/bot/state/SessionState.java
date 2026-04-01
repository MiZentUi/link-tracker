package backend.academy.linktracker.bot.state;

import backend.academy.linktracker.bot.handler.CommandHandler;
import backend.academy.linktracker.bot.model.Session;
import com.pengrad.telegrambot.model.Update;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class SessionState {
    protected final Map<String, CommandHandler> handlers;
    protected final StateFactory stateFactory;
    protected Session session;

    protected String checkCancellation(Update update) {
        var text = update.message().text();
        var commandString = text.split("\s+")[0];

        var handler = handlers.get(commandString);
        if (handler != null) {
            var state = handler.getState(session);
            session.setState(state);
            return state.handleUpdate(update);
        }

        return null;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public abstract String handleUpdate(Update update);
}
