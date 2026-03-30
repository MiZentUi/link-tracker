package backend.academy.linktracker.bot.config;

import backend.academy.linktracker.bot.client.ScrapperClient;
import backend.academy.linktracker.bot.dto.ApiErrorResponse;
import backend.academy.linktracker.bot.exception.ApiErrorException;
import backend.academy.linktracker.bot.properties.ScrapperProperties;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class ScrapperConfiguration {
    private ScrapperProperties properties;

    @Bean
    ScrapperClient scrapperClient(RestClient.Builder restClientBuilder) {
        var client = restClientBuilder
                .baseUrl(properties.getBaseUrl())
                .defaultStatusHandler(HttpStatusCode::isError, (_, response) -> {
                    var apiResponse = new ObjectMapper().readValue(response.getBody(), ApiErrorResponse.class);
                    log.info(
                            "ApiErrorResponse: {} - {} - {}",
                            apiResponse.getCode(),
                            apiResponse.getDescription(),
                            apiResponse.getExceptionMessage());
                    throw new ApiErrorException(apiResponse, response.getStatusCode());
                })
                .build();
        return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(client))
                .build()
                .createClient(ScrapperClient.class);
    }
}
