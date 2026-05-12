package backend.academy.linktracker.scrapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.wiremock.spring.EnableWireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import backend.academy.linktracker.scrapper.model.Link;
import backend.academy.linktracker.scrapper.service.api.GitHubService;

@SpringBootTest
@EnableWireMock
@Import(TestcontainersConfiguration.class)
class GitHubServiceTest {

    @Autowired
    private GitHubService service;

    @Test
    void newIssues() throws URISyntaxException {
        var link = Link.builder()
                .url("https://github.com/MiZentUi/link-tracker")
                .build();

        var response = """
                [
                    {
                        "title": "title1",
                        "body": "body1",
                        "created_at": "2026-05-03T00:08:58Z",
                        "user": {
                            "login": "user"
                        },
                        "pull_request": {}
                    },
                    {
                        "title": "title2",
                        "body": "body2",
                        "created_at": "2026-05-02T00:08:58Z",
                        "user": {
                            "login": "user"
                        }
                    }
                ]
                """;

        stubFor(get(urlPathEqualTo("/repos/MiZentUi/link-tracker/issues")).willReturn(okJson(response)));

        var descriptions = service.getChangesDescriptions(link,
                OffsetDateTime.of(2026, 5, 1, 0, 0, 0, 0, ZoneOffset.UTC));

        assertEquals(2, descriptions.size());

        var desc1 = descriptions.get(0);
        assertTrue(desc1.contains("Pull Request"));
        assertTrue(desc1.contains("title1"));
        assertTrue(desc1.contains("body1"));
        assertTrue(desc1.contains("user"));

        var desc2 = descriptions.get(1);
        assertTrue(desc2.contains("Issue"));
        assertTrue(desc2.contains("title2"));
        assertTrue(desc2.contains("body2"));
        assertTrue(desc2.contains("user"));
    }

    @Test
    void longBody() throws URISyntaxException {
        var link = Link.builder()
                .url("https://github.com/MiZentUi/link-tracker")
                .build();

        var response = """
                [
                    {
                        "title": "title1",
                        "body": "body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1",
                        "created_at": "2026-05-03T00:08:58Z",
                        "user": {
                            "login": "user"
                        },
                        "pull_request": {}
                    }
                ]
                """;

        stubFor(get(urlPathEqualTo("/repos/MiZentUi/link-tracker/issues")).willReturn(okJson(response)));

        var descriptions = service.getChangesDescriptions(link,
                OffsetDateTime.of(2026, 5, 1, 0, 0, 0, 0, ZoneOffset.UTC));

        assertEquals(1, descriptions.size());

        var desc = descriptions.getFirst();
        assertTrue(desc.contains("Pull Request"));
        assertTrue(desc.contains("title1"));
        assertTrue(desc.contains(
                "body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1..."));
        assertTrue(desc.contains("user"));
    }
}
