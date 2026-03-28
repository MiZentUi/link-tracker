package backend.academy.linktracker.scrapper.service.api;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import backend.academy.linktracker.scrapper.client.StackOverflowClient;
import backend.academy.linktracker.scrapper.model.Link;
import backend.academy.linktracker.scrapper.properties.StackoverflowProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class StackOverflowService implements ApiService {
    private StackOverflowClient client;
    private StackoverflowProperties properties;

    public String getBaseUrl() {
        return "https://stackoverflow.com";
    }

    @Override
    public LocalDateTime getLastUpdate(Link link) {
        var idPattern = Pattern.compile("https*:\\/\\/stackoverflow\\.com\\/questions\\/(?<id>\\d+)\\/.+");
        var matcher = idPattern.matcher(link.getUrl());
        if (matcher.find()) {
            var response = client.questions(Integer.parseInt(matcher.group("id")), "stackoverflow",
                    properties.getKey(), properties.getAccessToken());
            log.info("stackoverflow quota remaining: " + response.getQuotaRemaining());
            return LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(response.getLastActivityDate()),
                    TimeZone.getDefault().toZoneId());
        }
        return null;
    }
}
