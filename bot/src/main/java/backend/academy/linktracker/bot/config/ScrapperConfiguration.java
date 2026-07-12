package backend.academy.linktracker.bot.config;

import backend.academy.linktracker.bot.client.ScrapperClient;
import backend.academy.linktracker.bot.dto.ApiErrorResponse;
import backend.academy.linktracker.bot.exception.ApiClientErrorException;
import backend.academy.linktracker.bot.exception.ApiServerErrorException;
import backend.academy.linktracker.bot.properties.ScrapperProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ScrapperConfiguration {
    private final ScrapperProperties properties;

    @Bean
    ScrapperClient scrapperClient(RestClient.Builder restClientBuilder) {
        var client = restClientBuilder
                .baseUrl(properties.getBaseUrl())
                .defaultStatusHandler(HttpStatusCode::is4xxClientError, (_, response) -> {
                    var apiResponse = new ObjectMapper().readValue(response.getBody(), ApiErrorResponse.class);
                    log.atInfo()
                            .addKeyValue("code", apiResponse.getCode())
                            .addKeyValue("description", apiResponse.getDescription())
                            .addKeyValue("exception_message", apiResponse.getExceptionMessage())
                            .log("ApiErrorResponse");
                    throw new ApiClientErrorException(apiResponse, response.getStatusCode());
                })
                .defaultStatusHandler(HttpStatusCode::is5xxServerError, (_, response) -> {
                    var apiResponse = new ObjectMapper().readValue(response.getBody(), ApiErrorResponse.class);
                    log.atInfo()
                            .addKeyValue("code", apiResponse.getCode())
                            .addKeyValue("description", apiResponse.getDescription())
                            .addKeyValue("exception_message", apiResponse.getExceptionMessage())
                            .log("ApiErrorResponse");
                    throw new ApiServerErrorException(apiResponse, response.getStatusCode());
                })
                .build();
        return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(client))
                .build()
                .createClient(ScrapperClient.class);
    }
}
