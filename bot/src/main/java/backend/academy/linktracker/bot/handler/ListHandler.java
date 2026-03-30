package backend.academy.linktracker.bot.handler;

import backend.academy.linktracker.bot.client.ScrapperClient;
import backend.academy.linktracker.bot.exception.ApiErrorException;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ListHandler implements CommandHandler {
    private final ScrapperClient scrapperClient;

    @Override
    public String getCommand() {
        return "/list";
    }

    @Override
    public String getDescription() {
        return "вывести список всех ссылок, отслеживаемых пользователем";
    }

    @Override
    public SendMessage handle(Update update) {
        if (update != null && update.message() != null) {
            long chatId = update.message().chat().id();
            try {
                var links =
                        scrapperClient.getLinks(update.message().chat().id()).getLinks();
                log.atInfo()
                        .addKeyValue("chat_id", chatId)
                        .addKeyValue("links", links)
                        .log("links size: {}", links.size());
                var pattern = Pattern.compile("tag=(?<tag>[A-Za-z0-9]+)");
                var matcher = pattern.matcher(update.message().text());
                if (matcher.find()) {
                    var tag = matcher.group("tag");
                    log.atInfo().addKeyValue("tag", tag).log("tag={}", tag);
                    links = links.stream()
                            .filter(l -> l.getTags().contains(tag))
                            .toList();
                }
                if (links.isEmpty()) {
                    return new SendMessage(chatId, "Ссылки не найдены");
                }
                var response = new StringBuilder("Ссылки: \n");
                links.forEach(l -> {
                    response.append(" - ").append(l.getUrl());
                    var tags = l.getTags();
                    if (!tags.isEmpty()) {
                        response.append(" - теги: [")
                                .append(String.join(",", tags))
                                .append("]");
                    }
                    response.append("\n");
                });
                return new SendMessage(chatId, response.toString());
            } catch (ApiErrorException e) {
                var status = e.getStatusCode();
                if (status == HttpStatus.NOT_FOUND) {
                    return new SendMessage(chatId, "Чат не зарегестрирован. Введите /start для регистрации");
                }
            }
        }
        return null;
    }

    @Override
    public boolean isDone() {
        return true;
    }
}
