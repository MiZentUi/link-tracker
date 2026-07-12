package backend.academy.linktracker.bot;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import backend.academy.linktracker.bot.client.ScrapperClient;
import backend.academy.linktracker.bot.dto.AddLinkRequest;
import backend.academy.linktracker.bot.exception.ApiClientErrorException;
import backend.academy.linktracker.bot.service.ChatsService;
import backend.academy.linktracker.bot.service.LinksService;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import java.util.List;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.util.StopWatch;
import org.wiremock.spring.EnableWireMock;

@SpringBootTest(properties = {"resilience4j.retry.configs.default.waitDuration=15ms"})
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@EnableWireMock
class Resilience4jTests {

    @Autowired
    ChatsService chatsService;

    @Autowired
    LinksService linksService;

    @MockitoSpyBean
    ScrapperClient client;

    @Autowired
    CacheManager cacheManager;

    @AfterEach
    void clear() {
        WireMock.reset();
        Mockito.clearInvocations(client);
        cacheManager.getCacheNames().forEach(name -> cacheManager.getCache(name).clear());
    }

    @Test
    void testChatCreationTimeout() {
        stubFor(post(urlMatching("/scrapper/tg-chat/\\d+"))
                .willReturn(aResponse().withStatus(200).withFixedDelay(200)));

        var exception = assertThrows(CompletionException.class, () -> {
            chatsService.createChat(1L).join();
        });
        assertInstanceOf(TimeoutException.class, exception.getCause());
    }

    @Test
    void testLinkTrackTimeout() {
        stubFor(post(urlMatching("/scrapper/links"))
                .willReturn(aResponse().withStatus(200).withFixedDelay(200)));

        var exception = assertThrows(CompletionException.class, () -> {
            linksService
                    .track(
                            1L,
                            AddLinkRequest.builder()
                                    .link("http://example.com")
                                    .tags(List.of())
                                    .build())
                    .join();
        });
        assertInstanceOf(TimeoutException.class, exception.getCause());
    }

    @Test
    void testChatCreationRetry() {
        stubFor(post(urlMatching("/scrapper/tg-chat/\\d+"))
                .inScenario("retry-chat")
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(aResponse().withStatus(500).withBody("""
                                {
                                    "description": "desc",
                                    "code": "CODE"
                                }
                                """))
                .willSetStateTo("second"));

        stubFor(post(urlMatching("/scrapper/tg-chat/\\d+"))
                .inScenario("retry-chat")
                .whenScenarioStateIs("second")
                .willReturn(aResponse().withStatus(500).withBody("""
                                {
                                    "description": "desc",
                                    "code": "CODE"
                                }
                                """))
                .willSetStateTo("third"));

        stubFor(post(urlMatching("/scrapper/tg-chat/\\d+"))
                .inScenario("retry-chat")
                .whenScenarioStateIs("third")
                .willReturn(aResponse().withStatus(200)));

        chatsService.createChat(1L).join();

        verify(client, times(3)).createChat(anyLong());
    }

    @Test
    void testGetLinksRetry() {
        stubFor(get(urlMatching("/scrapper/links"))
                .inScenario("retry-links")
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(aResponse().withStatus(500).withBody("""
                                {
                                    "description": "desc",
                                    "code": "CODE"
                                }
                                """))
                .willSetStateTo("second"));

        stubFor(get(urlMatching("/scrapper/links"))
                .inScenario("retry-links")
                .whenScenarioStateIs("second")
                .willReturn(aResponse().withStatus(500).withBody("""
                                {
                                    "description": "desc",
                                    "code": "CODE"
                                }
                                """))
                .willSetStateTo("third"));

        stubFor(get(urlMatching("/scrapper/links"))
                .inScenario("retry-links")
                .whenScenarioStateIs("third")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                    "links": [
                                        {
                                            "id": 1,
                                            "url": "http://example.com",
                                            "tags": []
                                        }
                                    ],
                                    "size": 1
                                }
                                """)));

        var links = linksService.getLinks(1L).join();

        verify(client, times(3)).getLinks(anyLong());

        assertEquals(1, links.size());
        assertEquals("http://example.com", links.get(0).getUrl());
    }

    @Test
    void testGetLinksNoRetry() {
        stubFor(get(urlMatching("/scrapper/links"))
                .willReturn(aResponse().withStatus(400).withBody("""
                                {
                                    "description": "desc",
                                    "code": "CODE"
                                }
                                """)));

        var exception = assertThrows(CompletionException.class, () -> {
            linksService.getLinks(1L).join();
        });
        assertInstanceOf(ApiClientErrorException.class, exception.getCause());

        verify(client, times(1)).getLinks(anyLong());
    }

    @Test
    void testGetLinksRetryInterval() {
        stubFor(get(urlMatching("/scrapper/links"))
                .inScenario("retry-interval")
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(aResponse().withStatus(500).withBody("""
                                {
                                    "description": "desc",
                                    "code": "CODE"
                                }
                                """))
                .willSetStateTo("second"));

        stubFor(get(urlMatching("/scrapper/links"))
                .inScenario("retry-interval")
                .whenScenarioStateIs("second")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                    "links": [
                                        {
                                            "id": 1,
                                            "url": "http://example.com",
                                            "tags": []
                                        }
                                    ],
                                    "size": 1
                                }
                                """)));

        var watch = new StopWatch();

        watch.start();
        var links = linksService.getLinks(1L).join();
        watch.stop();

        verify(client, times(2)).getLinks(anyLong());

        assertTrue(watch.getTotalTimeMillis() >= 15);

        assertEquals(1, links.size());
        assertEquals("http://example.com", links.get(0).getUrl());
    }
}
