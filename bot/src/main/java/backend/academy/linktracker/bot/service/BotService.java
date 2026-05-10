package backend.academy.linktracker.bot.service;

import backend.academy.linktracker.bot.dto.LinkUpdate;
import backend.academy.linktracker.bot.handler.CommandHandler;
import backend.academy.linktracker.bot.model.Session;
import backend.academy.linktracker.bot.repository.SessionRepository;
import backend.academy.linktracker.bot.state.StateFactory;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.BotCommand;
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
    private final StateFactory stateFactory;
    private final TelegramBot bot;
    private final SessionRepository sessionRepository;

    @Qualifier("handlersByCommand")
    private final Map<String, CommandHandler> handlers;

    @PostConstruct
    public void init() {
        log.atInfo().addKeyValue("handlers", handlers).log("available handlers");

        setCommands();

        bot.setUpdatesListener(updates -> {
            updates.forEach(update -> {
                long chatId = update.message().chat().id();
                var session = sessionRepository.findByChatId(chatId).orElse(null);

                if (session == null) {
                    session = new Session(chatId, stateFactory);
                    sessionRepository.save(session);
                }

                var message = session.handleUpdate(update);

                if (message != null) {
                    var response = bot.execute(new SendMessage(chatId, message));

                    if (!response.isOk()) {
                        log.atError().addKeyValue("response", message).log("update is failed");
                    }
                } else {
                    log.error("message is null");
                }
            });
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
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
