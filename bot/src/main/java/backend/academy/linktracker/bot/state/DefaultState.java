package backend.academy.linktracker.bot.state;

import backend.academy.linktracker.bot.handler.CommandHandler;
import com.pengrad.telegrambot.model.Update;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Scope("prototype")
public class DefaultState extends SessionState {

    public DefaultState(
            @Qualifier("handlersByCommand") Map<String, CommandHandler> handlers, StateFactory stateFactory) {
        super(handlers, stateFactory);
    }

    @Override
    public String handleUpdate(Update update) {
        var message = update.message();

        if (message != null && message.text() != null) {
            var text = message.text();
            var commandString = text.split("\s+")[0];
            long chatId = message.chat().id();
            log.atInfo().addKeyValue("chat_id", chatId).log("message chat id");

            var handler = handlers.get(commandString);

            if (handler != null) {
                var state = handler.getState(session);
                if (state instanceof DefaultState) {
                    return handler.handle(update);
                }
                session.setState(state);
                return state.handleUpdate(update);
            } else {
                return text.startsWith("/")
                        ? "Неизвестная команда. Воспользуйтесь /help, чтобы посмотреть список доступных команд."
                        : "Действие \"" + text
                                + "\" не предусмотрено. Воспользуйтесь /help, чтобы посмотреть список доступных команд.";
            }
        }
        return null;
    }
}
