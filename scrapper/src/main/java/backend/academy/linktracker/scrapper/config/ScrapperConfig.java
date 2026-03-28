package backend.academy.linktracker.scrapper.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import backend.academy.linktracker.scrapper.client.BotClient;
import backend.academy.linktracker.scrapper.client.GitHubClient;
import backend.academy.linktracker.scrapper.client.StackOverflowClient;
import backend.academy.linktracker.scrapper.properties.BotProperties;
import backend.academy.linktracker.scrapper.properties.GithubProperties;
import backend.academy.linktracker.scrapper.properties.StackoverflowProperties;

@Configuration
public class ScrapperConfig {

    @Bean
    BotClient botClient(BotProperties properties) {
        var restClient = RestClient.builder().baseUrl(properties.getBaseUrl()).build();
        return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build()
                .createClient(BotClient.class);
    }

    @Bean
    GitHubClient githubClient(GithubProperties properties) {
        var restClient = RestClient.builder().baseUrl(properties.getBaseUrl())
                .defaultHeader("Authorization", "Bearer " + properties.getToken()).build();
        return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build()
                .createClient(GitHubClient.class);
    }

    @Bean
    StackOverflowClient stackOverflowClient(StackoverflowProperties properties) {
        var restClient = RestClient.builder().baseUrl(properties.getBaseUrl()).build();
        return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build()
                .createClient(StackOverflowClient.class);
    }
}
