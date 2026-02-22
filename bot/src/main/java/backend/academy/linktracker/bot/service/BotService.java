package backend.academy.linktracker.bot.service;

import backend.academy.linktracker.bot.handler.CommandHandler;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import jakarta.annotation.PostConstruct;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class BotService {
    private final TelegramBot bot;

    @Qualifier("handlersByCommand")
    private final Map<String, CommandHandler> handlers;

    @PostConstruct
    public void init() {
        log.atInfo().addKeyValue("handlers", handlers).log("Available handlers");

        setCommands();

        bot.setUpdatesListener(updates -> {
            updates.forEach(u -> {
                var message = processUpdate(u);
                if (message != null) {
                    var response = bot.execute(message);

                    if (!response.isOk()) {
                        log.atError().addKeyValue("response", response).log("Update is failed");
                    }
                } else {
                    log.error("Message is null");
                }
            });
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    public SendMessage processUpdate(Update update) {
        var message = update.message();

        if (message != null && message.text() != null) {
            var text = message.text();
            long chatId = message.chat().id();

            if (text.startsWith("/")) {
                var handler = handlers.get(text);

                if (handler != null) {
                    return handler.handle(update);
                }
                return new SendMessage(
                        chatId, "Неизвестная команда. Воспользуйтесь /help, чтобы посмотреть список доступных команд.");
            }
            return new SendMessage(
                    chatId,
                    "Действие \"" + text
                            + "\" не предусмотрено. Воспользуйтесь /help, чтобы посмотреть список доступных команд.");
        }
        return null;
    }

    private void setCommands() {
        var botCommands = handlers.values().stream()
                .map(h -> new BotCommand(h.getCommand(), h.getDescription()))
                .toArray(BotCommand[]::new);
        var response = bot.execute(new SetMyCommands(botCommands));
        if (!response.isOk()) {
            log.atError().addKeyValue("response", response).log("Set commands failed");
        }
    }
}
