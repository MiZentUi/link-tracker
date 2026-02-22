package backend.academy.linktracker.bot.handler;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;

@Component
public class StartHandler implements CommandHandler {
    @Override
    public String getCommand() {
        return "/start";
    }

    @Override
    public String getDescription() {
        return "приветственное сообщение";
    }

    @Override
    public SendMessage handle(Update update) {
        return new SendMessage(
                (long) update.message().chat().id(),
                "Добро пожаловать! Используйте /help, чтобы посмотреть доступные команды.");
    }
}
