package backend.academy.linktracker.bot.handler;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class HelpHandler implements CommandHandler {
    private List<CommandHandler> handlers;

    @Override
    public String getCommand() {
        return "/help";
    }

    @Override
    public String getDescription() {
        return "описание команд";
    }

    @Override
    public SendMessage handle(Update update) {
        var text = handlers.stream()
                .map(h -> h.getCommand() + " - " + h.getDescription() + "\n")
                .collect(Collectors.joining());
        return new SendMessage((long) update.message().chat().id(), text);
    }
}
