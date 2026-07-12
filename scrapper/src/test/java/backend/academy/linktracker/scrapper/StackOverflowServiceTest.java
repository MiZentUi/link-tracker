package backend.academy.linktracker.scrapper;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import backend.academy.linktracker.scrapper.model.Link;
import backend.academy.linktracker.scrapper.service.api.StackOverflowService;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.wiremock.spring.EnableWireMock;

@SpringBootTest
@EnableWireMock
@Import(TestcontainersConfiguration.class)
class StackOverflowServiceTest {

    @Autowired
    private StackOverflowService service;

    @Test
    void newUpdates() {
        var link = Link.builder()
                .url("https://stackoverflow.com/questions/123/java-lib-or-app-to-convert-csv-to-xml-file")
                .build();

        var questionsResponse = """
                {
                    "items": [
                        {"title": "title"}
                    ]
                }
                """;

        var answersResponse = """
                {
                    "items": [
                        {
                            "owner": {
                                "display_name": "saint_groceon"
                            },
                            "creation_date": 1217608303,
                            "body": "body1"
                        }
                    ]
                }
                """;

        var commentsResponse = """
                {
                    "items": [
                        {
                            "owner": {
                                "display_name": "user"
                            },
                            "creation_date": 1217608303,
                            "body": "body2"
                        }
                    ]
                }
                """;

        stubFor(get(urlPathEqualTo("/questions/123")).willReturn(okJson(questionsResponse)));
        stubFor(get(urlPathEqualTo("/questions/123/answers")).willReturn(okJson(answersResponse)));
        stubFor(get(urlPathEqualTo("/questions/123/comments")).willReturn(okJson(commentsResponse)));

        var descriptions =
                service.getChangesDescriptions(link, OffsetDateTime.of(2008, 8, 1, 0, 0, 0, 0, ZoneOffset.UTC));

        assertEquals(2, descriptions.size());

        var desc1 = descriptions.get(0);
        assertTrue(desc1.contains("title"));
        assertTrue(desc1.contains("body1"));
        assertTrue(desc1.contains("saint_groceon"));

        var desc2 = descriptions.get(1);
        assertTrue(desc2.contains("title"));
        assertTrue(desc2.contains("body2"));
        assertTrue(desc2.contains("user"));
    }

    @Test
    void longBody() {
        var link = Link.builder()
                .url("https://stackoverflow.com/questions/123/java-lib-or-app-to-convert-csv-to-xml-file")
                .build();

        var questionsResponse = """
                {
                    "items": [
                        {"title": "title"}
                    ],
                    "quota_remaining": 1000
                }
                """;

        var answersResponse = """
                {
                    "items": [
                        {
                            "owner": {
                                "display_name": "saint_groceon"
                            },
                            "creation_date": 1217608303,
                            "body": "body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1"
                        }
                    ],
                    "quota_remaining": 1000
                }
                """;

        var commentsResponse = """
                {
                    "items": [],
                    "quota_remaining": 1000
                }
                """;

        stubFor(get(urlPathEqualTo("/questions/123")).willReturn(okJson(questionsResponse)));
        stubFor(get(urlPathEqualTo("/questions/123/answers")).willReturn(okJson(answersResponse)));
        stubFor(get(urlPathEqualTo("/questions/123/comments")).willReturn(okJson(commentsResponse)));

        var descriptions =
                service.getChangesDescriptions(link, OffsetDateTime.of(2008, 8, 1, 0, 0, 0, 0, ZoneOffset.UTC));

        assertEquals(1, descriptions.size());

        var desc = descriptions.getFirst();
        assertTrue(desc.contains("title"));
        assertTrue(desc.contains("saint_groceon"));
        assertTrue(
                desc.contains(
                        "body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1body1..."));
    }
}
