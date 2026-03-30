package backend.academy.linktracker.bot.config;

import backend.academy.linktracker.bot.handler.CommandHandler;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class HandlerConfiguration {
    private final List<CommandHandler> handlers;

    @Bean
    Map<String, CommandHandler> handlersByCommand() {
        return handlers.stream().collect(Collectors.toMap(CommandHandler::getCommand, h -> h));
    }
}
