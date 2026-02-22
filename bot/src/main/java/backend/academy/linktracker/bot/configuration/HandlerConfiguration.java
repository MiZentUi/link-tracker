package backend.academy.linktracker.bot.configuration;

import backend.academy.linktracker.bot.handler.CommandHandler;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class HandlerConfiguration {
    private List<CommandHandler> handlers;

    @Bean
    public Map<String, CommandHandler> handlersByCommand() {
        return handlers.stream().collect(Collectors.toMap(CommandHandler::getCommand, h -> h));
    }
}
