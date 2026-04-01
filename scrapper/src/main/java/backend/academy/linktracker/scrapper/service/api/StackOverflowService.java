package backend.academy.linktracker.scrapper.service.api;

import backend.academy.linktracker.scrapper.client.StackOverflowClient;
import backend.academy.linktracker.scrapper.model.Link;
import backend.academy.linktracker.scrapper.properties.StackoverflowProperties;
import java.time.Instant;
import java.time.LocalDateTime;
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
    public LocalDateTime getLastUpdate(Link link) {
        var idPattern = Pattern.compile("https*:\\/\\/(?<site>[A-Za-z0-9]+)\\.com\\/questions\\/(?<id>\\d+)\\/.+");
        var matcher = idPattern.matcher(link.getUrl());
        if (matcher.find()) {
            var questionId = Integer.parseInt(matcher.group("id"));
            var site = matcher.group("site");
            log.atInfo()
                    .addKeyValue("site", site)
                    .addKeyValue("question_id", questionId)
                    .log();
            var response = client.questions(questionId, site, properties.getKey(), properties.getAccessToken());
            log.atInfo()
                    .addKeyValue("quota_remaining", response.getQuotaRemaining())
                    .log();
            return LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(response.getLastActivityDate()),
                    TimeZone.getDefault().toZoneId());
        }
        return null;
    }
}
