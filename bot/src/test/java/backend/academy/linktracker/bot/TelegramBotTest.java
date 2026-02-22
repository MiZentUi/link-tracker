package backend.academy.linktracker.bot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import backend.academy.linktracker.bot.properties.TelegramProperties;
import backend.academy.linktracker.bot.service.BotService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import java.util.List;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.wiremock.spring.EnableWireMock;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@EnableWireMock
class TelegramBotTest implements WithAssertions {

    @Autowired
    TelegramBot telegramBot;

    @Autowired
    TelegramProperties telegramProperties;

    @Autowired
    BotService botService;

    @Test
    void startRequest() {
        // Arrange
        var command = "/start";
        var chatId = 42L;
        var words = List.of("Добро пожаловать!", "/help");

        // Act
        var updateMock = mock(Update.class);
        var messageMock = mock(Message.class);
        var chatMock = mock(Chat.class);
        when(messageMock.text()).thenReturn(command);
        when(messageMock.chat()).thenReturn(chatMock);
        when(chatMock.id()).thenReturn(chatId);
        when(updateMock.message()).thenReturn(messageMock);
        var response = botService.processUpdate(updateMock);

        // Assert
        assertEquals(chatId, response.getChatId());
        assertThat(words.stream().allMatch(response.getText()::contains));
    }

    @Test
    void helpRequest() {
        // Arrange
        var command = "/help";
        var chatId = 42L;
        var words = List.of("/start", "приветственное сообщение");

        // Act
        var updateMock = mock(Update.class);
        var messageMock = mock(Message.class);
        var chatMock = mock(Chat.class);
        when(messageMock.text()).thenReturn(command);
        when(messageMock.chat()).thenReturn(chatMock);
        when(chatMock.id()).thenReturn(chatId);
        when(updateMock.message()).thenReturn(messageMock);
        var response = botService.processUpdate(updateMock);

        // Assert
        assertEquals(chatId, response.getChatId());
        assertThat(words.stream().allMatch(response.getText()::contains));
    }

    @Test
    void unknownRequest() {
        // Arrange
        var command = "/unknown";
        var chatId = 42L;
        var words = List.of("Неизвестная команда", "/help");

        // Act
        var updateMock = mock(Update.class);
        var messageMock = mock(Message.class);
        var chatMock = mock(Chat.class);
        when(messageMock.text()).thenReturn(command);
        when(messageMock.chat()).thenReturn(chatMock);
        when(chatMock.id()).thenReturn(chatId);
        when(updateMock.message()).thenReturn(messageMock);
        var response = botService.processUpdate(updateMock);

        // Assert
        assertEquals(chatId, response.getChatId());
        assertThat(words.stream().allMatch(response.getText()::contains));
    }
}
