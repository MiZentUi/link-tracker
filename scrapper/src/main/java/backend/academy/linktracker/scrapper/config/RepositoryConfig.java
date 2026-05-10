package backend.academy.linktracker.scrapper.config;

import backend.academy.linktracker.scrapper.properties.RepositoryProperties;
import backend.academy.linktracker.scrapper.repository.ChatsRepository;
import backend.academy.linktracker.scrapper.repository.LinksRepository;
import backend.academy.linktracker.scrapper.repository.TagsRepository;
import backend.academy.linktracker.scrapper.repository.orm.JpaChatsRepository;
import backend.academy.linktracker.scrapper.repository.orm.JpaLinksRepository;
import backend.academy.linktracker.scrapper.repository.orm.JpaTagsRepository;
import backend.academy.linktracker.scrapper.repository.sql.SqlChatsRepository;
import backend.academy.linktracker.scrapper.repository.sql.SqlLinksRepository;
import backend.academy.linktracker.scrapper.repository.sql.SqlTagsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@RequiredArgsConstructor
public class RepositoryConfig {
    private final RepositoryProperties properties;

    @Bean
    @Primary
    ChatsRepository chatsRepository(JpaChatsRepository jpaChatsRepository, SqlChatsRepository sqlChatsRepository) {
        return switch (properties.getAccessType().toLowerCase()) {
            case "orm" -> jpaChatsRepository;
            case "sql" -> sqlChatsRepository;
            default -> jpaChatsRepository;
        };
    }

    @Bean
    @Primary
    LinksRepository linksRepository(JpaLinksRepository jpaLinksRepository, SqlLinksRepository sqlLinksRepository) {
        return switch (properties.getAccessType().toLowerCase()) {
            case "orm" -> jpaLinksRepository;
            case "sql" -> sqlLinksRepository;
            default -> jpaLinksRepository;
        };
    }

    @Bean
    @Primary
    TagsRepository tagsRepository(JpaTagsRepository jpaTagsRepository, SqlTagsRepository sqlTagsRepository) {
        return switch (properties.getAccessType().toLowerCase()) {
            case "orm" -> jpaTagsRepository;
            case "sql" -> sqlTagsRepository;
            default -> jpaTagsRepository;
        };
    }
}
