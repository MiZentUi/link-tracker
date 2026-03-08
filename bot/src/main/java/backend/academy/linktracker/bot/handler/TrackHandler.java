package backend.academy.linktracker.bot.handler;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;

public class TrackHandler implements CommandHandler {

    @Override
    public String getCommand() {
        return "/track";
    }

    @Override
    public String getDescription() {
        return "начать отслеживание ссылки";
    }

    @Override
    public SendMessage handle(Update update) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handle'");
    }

}
