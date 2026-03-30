package backend.academy.linktracker.bot.service;

import backend.academy.linktracker.bot.dto.LinkUpdate;
import backend.academy.linktracker.bot.handler.CommandHandler;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import jakarta.annotation.PostConstruct;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BotService {
    private final TelegramBot bot;

    @Qualifier("handlersByCommand")
    private final Map<String, CommandHandler> handlers;

    private CommandHandler currentHandler;

    @PostConstruct
    public void init() {
        log.atInfo().addKeyValue("handlers", handlers).log("available handlers");

        setCommands();

        bot.setUpdatesListener(updates -> {
            updates.forEach(u -> {
                var message = processUpdate(u);
                if (message != null) {
                    var response = bot.execute(message);

                    if (!response.isOk()) {
                        log.atError().addKeyValue("response", message.getText()).log("update is failed");
                    }
                } else {
                    log.error("message is null");
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
            log.atInfo().addKeyValue("chat_id", chatId).log("message chat id");

            if (currentHandler != null && currentHandler.isDone()) {
                currentHandler = null;
            }

            var handler = handlers.get(text.split("\s+")[0]);

            if (handler != null) {
                currentHandler = handler;
            }

            if (currentHandler != null) {
                log.atInfo()
                        .addKeyValue("current_handler", currentHandler.getCommand())
                        .log();

                return currentHandler.handle(update);
            } else {
                return new SendMessage(
                        chatId,
                        text.startsWith("/")
                                ? "Неизвестная команда. Воспользуйтесь /help, чтобы посмотреть список доступных команд."
                                : "Действие \"" + text
                                        + "\" не предусмотрено. Воспользуйтесь /help, чтобы посмотреть список доступных команд.");
            }
        }
        return null;
    }

    private void setCommands() {
        var botCommands = handlers.values().stream()
                .map(h -> new BotCommand(h.getCommand(), h.getDescription()))
                .toArray(BotCommand[]::new);
        var response = bot.execute(new SetMyCommands(botCommands));
        if (!response.isOk()) {
            log.atError().addKeyValue("response", response).log("set commands failed");
        }
    }

    public void sendLinkUpdate(LinkUpdate update) {
        update.getTgChatIds().forEach(id -> {
            var updateFormat = "<b>Обновление по ссылке: %s</b>%n<b>Описание:</b>%n<blockquote>%s</blockquote>";
            var message = new SendMessage(
                            (long) id, String.format(updateFormat, update.getUrl(), update.getDescription()))
                    .parseMode(ParseMode.HTML);
            bot.execute(message);
        });
    }
}
