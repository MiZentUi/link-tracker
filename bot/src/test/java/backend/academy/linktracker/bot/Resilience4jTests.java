package backend.academy.linktracker.bot;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.wiremock.spring.EnableWireMock;

import backend.academy.linktracker.bot.dto.AddLinkRequest;
import backend.academy.linktracker.bot.service.ChatsService;
import backend.academy.linktracker.bot.service.LinksService;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@EnableWireMock
class Resilience4jTests {

    @Autowired
    ChatsService chatsService;

    @Autowired
    LinksService linksService;

    @Test
    void testChatCreationTimeout() {
        stubFor(post(urlMatching("/scrapper/tg-chat/\\d+"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withFixedDelay(1000)));

        var exception = assertThrows(CompletionException.class, () -> {
            chatsService.createChat(1L).join();
        });
        assertInstanceOf(TimeoutException.class, exception.getCause());
    }

    @Test
    void testLinkCreationTimeout() {
        stubFor(post(urlMatching("/scrapper/links"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withFixedDelay(1000)));

        var exception = assertThrows(CompletionException.class, () -> {
            linksService.track(1L, AddLinkRequest.builder()
                    .link("http://example.com")
                    .tags(List.of())
                    .build()).join();
        });
        assertInstanceOf(TimeoutException.class, exception.getCause());
    }
}
