package backend.academy.linktracker.bot.handler;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;

public interface CommandHandler {
    String getCommand();

    String getDescription();

    SendMessage handle(Update update);

    boolean isDone();
}
