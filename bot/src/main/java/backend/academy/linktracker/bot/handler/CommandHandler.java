package backend.academy.linktracker.bot.handler;

import backend.academy.linktracker.bot.model.Session;
import backend.academy.linktracker.bot.state.SessionState;
import com.pengrad.telegrambot.model.Update;

public interface CommandHandler {
    String getCommand();

    String getDescription();

    SessionState getState(Session session);

    String handle(Update update);
}
