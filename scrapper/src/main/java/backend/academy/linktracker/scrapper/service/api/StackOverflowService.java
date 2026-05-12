package backend.academy.linktracker.scrapper.service.api;

import backend.academy.linktracker.scrapper.client.StackOverflowClient;
import backend.academy.linktracker.scrapper.model.Link;
import backend.academy.linktracker.scrapper.properties.StackoverflowProperties;

import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StackOverflowService implements ScrapingApiService {
    private final StackOverflowClient client;
    private final StackoverflowProperties properties;

    public String getBaseUrl() {
        return "https://stackoverflow.com";
    }

    @Override
    public LocalDateTime getLastUpdate(Link link) throws URISyntaxException {
        var idPattern = Pattern
                .compile("https*:\\/\\/(?<site>[A-Za-z0-9]+)\\.com\\/questions\\/(?<id>\\d+)\\/.+");
        var matcher = idPattern.matcher(link.getUrl());
        if (matcher.find()) {
            var questionId = Integer.parseInt(matcher.group("id"));
            var site = matcher.group("site");
            log.atInfo()
                    .addKeyValue("site", site)
                    .addKeyValue("question_id", questionId)
                    .log();
            var response = client.questions(questionId, site, properties.getKey(),
                    properties.getAccessToken());
            log.atInfo()
                    .addKeyValue("quota_remaining", response.getQuotaRemaining())
                    .log();
            return LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(response.getItems().getFirst().getLastActivityDate()),
                    TimeZone.getDefault().toZoneId());
        }
        return null;
    }

    @Override
    public List<String> getChangesDescriptions(Link link, OffsetDateTime since) throws URISyntaxException {
        var idPattern = Pattern
                .compile("https*:\\/\\/(?<site>[A-Za-z0-9]+)\\.com\\/questions\\/(?<id>\\d+)\\/.+");
        var matcher = idPattern.matcher(link.getUrl());
        var descriptions = new ArrayList<String>();
        if (matcher.find()) {
            var questionId = Integer.parseInt(matcher.group("id"));
            var site = matcher.group("site");
            log.atInfo()
                    .addKeyValue("site", site)
                    .addKeyValue("question_id", questionId)
                    .log();
            var title = client.questions(questionId, site, properties.getKey(), properties.getAccessToken())
                    .getItems()
                    .getFirst().getTitle();

            var timestamp = Timestamp.from(since.toInstant());
            var params = properties.getParams();
            var answers = client.questionsAnswers(questionId, timestamp, site, params.sort(),
                    params.order(),
                    params.filter(),
                    properties.getKey(), properties.getAccessToken());
            var comments = client.questionsComments(questionId, timestamp, site, "activity", "ask",
                    "!nNPvSN_ZTx",
                    properties.getKey(), properties.getAccessToken());

            for (var answer : answers.getItems()) {
                var description = new StringBuilder();
                description.append("Вопрос: ").append(title).append("\n");
                description.append("Пользователь: ").append(answer.getOwnerName()).append("\n");
                description.append("Время создания: ")
                        .append(LocalDateTime.ofInstant(
                                Instant.ofEpochMilli(answer.getCreationDate()),
                                TimeZone.getDefault().toZoneId())
                                .format(DateTimeFormatter.BASIC_ISO_DATE))
                        .append("\n");
                var body = answer.getBody();
                var maxPreviewLen = 200;
                description.append("Превью: ")
                        .append(body.substring(0, Math.min(maxPreviewLen, body.length()) - 1))
                        .append(body.length() > maxPreviewLen ? "..." : "");
                descriptions.add(description.toString());
            }

            for (var comment : comments.getItems()) {
                var description = new StringBuilder();
                description.append("Вопрос: ").append(title).append("\n");
                description.append("Пользователь: ").append(comment.getOwnerName()).append("\n");
                description.append("Время создания: ")
                        .append(LocalDateTime.ofInstant(
                                Instant.ofEpochMilli(comment.getCreationDate()),
                                TimeZone.getDefault().toZoneId())
                                .format(DateTimeFormatter.BASIC_ISO_DATE))
                        .append("\n");
                var body = comment.getBody();
                var maxPreviewLen = 200;
                description.append("Превью: ")
                        .append(body.substring(0, Math.min(maxPreviewLen, body.length()) - 1))
                        .append(body.length() > maxPreviewLen ? "..." : "");
                descriptions.add(description.toString());
            }
        }
        return descriptions;
    }
}
