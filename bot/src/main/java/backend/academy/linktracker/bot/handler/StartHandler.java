package backend.academy.linktracker.bot.handler;

import backend.academy.linktracker.bot.client.ScrapperClient;
import backend.academy.linktracker.bot.exception.ApiErrorException;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartHandler implements CommandHandler {
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
    public SendMessage handle(Update update) {
        long chatId = update.message().chat().id();
        log.info("Start command from {}", chatId);
        try {
            scrapperClient.createChat(chatId);
        } catch (ApiErrorException e) {
            log.atInfo().addKeyValue("exception", e).log("Start api error: ", e.getMessage());
        }
        return new SendMessage(chatId, "Добро пожаловать! Используйте /help, чтобы посмотреть доступные команды.");
    }

    @Override
    public boolean isDone() {
        return true;
    }
}
