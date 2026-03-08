package backend.academy.linktracker.bot.handler;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;

public class ListHandler implements CommandHandler {

    @Override
    public String getCommand() {
        return "/list";
    }

    @Override
    public String getDescription() {
        return "вывести список всех ссылок, отслеживаемых пользователем";
    }

    @Override
    public SendMessage handle(Update update) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handle'");
    }

}
