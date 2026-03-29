package backend.academy.linktracker.bot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.pengrad.telegrambot.TelegramBot;
import backend.academy.linktracker.bot.properties.TelegramProperties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class TelegramConfiguration {

    @Bean
    TelegramBot telegramBot(TelegramProperties properties) {
        var builder = new TelegramBot.Builder(properties.getToken())
                .apiUrl(properties.getUrl())
                .updateListenerSleep(properties.getUpdateListenerSleep().toMillis());

        if (properties.isDebug()) {
            builder.debug();
        }

        return builder.build();
    }
}
