package backend.academy.linktracker.scrapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import backend.academy.linktracker.scrapper.model.Chat;
import backend.academy.linktracker.scrapper.model.Link;
import backend.academy.linktracker.scrapper.model.Tag;
import backend.academy.linktracker.scrapper.repository.ChatsRepository;
import backend.academy.linktracker.scrapper.repository.LinksRepository;
import backend.academy.linktracker.scrapper.repository.TagsRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class TagsRepositoryTest {

    @Autowired
    PostgreSQLContainer postgres;

    @Autowired
    ChatsRepository chatsRepository;

    @Autowired
    LinksRepository linksRepository;

    @Autowired
    TagsRepository tagsRepository;

    @AfterEach
    void clean() {
        chatsRepository.deleteAll();
        linksRepository.deleteAll();
        tagsRepository.deleteAll();
    }

    @Test
    void saveThenFindAndDelete() {
        var chat = chatsRepository.save(Chat.builder().id(42L).build());
        var link = linksRepository.save(Link.builder().url("https://url.url").build());

        var tag = Tag.builder().chat(chat).link(link).name("tag").build();

        tagsRepository.save(tag);

        assertNotNull(tag.getId());

        var addedTag = tagsRepository.findById(tag.getId()).orElse(null);

        assertNotNull(addedTag);
        assertEquals(tag.getId(), addedTag.getId());
        assertEquals(tag.getName(), addedTag.getName());

        tagsRepository.delete(tag);
        var deletedLink = tagsRepository.findById(tag.getId());

        assertFalse(deletedLink.isPresent());
    }

    @Test
    void saveThenFindAndDeleteById() {
        var chat = chatsRepository.save(Chat.builder().id(42L).build());
        var link = linksRepository.save(Link.builder().url("https://url.url").build());

        var tag = Tag.builder().chat(chat).link(link).name("tag").build();

        tagsRepository.save(tag);

        assertNotNull(tag.getId());

        var addedTag = tagsRepository.findById(tag.getId()).orElse(null);

        assertNotNull(addedTag);
        assertEquals(tag.getId(), addedTag.getId());
        assertEquals(tag.getName(), addedTag.getName());

        tagsRepository.deleteById(tag.getId());
        var deletedLink = tagsRepository.findById(tag.getId());

        assertFalse(deletedLink.isPresent());
    }

    @Test
    void findAllEmpty() {
        assertTrue(tagsRepository.findAll().isEmpty());
    }
}
