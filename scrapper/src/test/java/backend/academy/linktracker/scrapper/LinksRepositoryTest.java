package backend.academy.linktracker.scrapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import backend.academy.linktracker.scrapper.model.Link;
import backend.academy.linktracker.scrapper.repository.LinksRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class LinksRepositoryTest {

    @Autowired
    PostgreSQLContainer postgres;

    @Autowired
    LinksRepository linksRepository;

    @AfterEach
    void clean() {
        linksRepository.deleteAll();
    }

    @Test
    void saveThenFindAndDelete() {
        var link = Link.builder().url("https://example.com").build();

        linksRepository.save(link);

        assertNotNull(link.getId());

        var addedLink = linksRepository.findById(link.getId()).orElse(null);

        assertNotNull(addedLink);
        assertEquals(link.getId(), addedLink.getId());
        assertEquals(link.getUrl(), addedLink.getUrl());

        linksRepository.delete(link);
        var deletedLink = linksRepository.findById(link.getId());

        assertFalse(deletedLink.isPresent());
    }

    @Test
    void findAllEmpty() {
        assertTrue(linksRepository.findAll().isEmpty());
    }

    @Test
    void findByUrl() {
        var link = Link.builder().url("https://example.com").build();

        linksRepository.save(link);

        assertNotNull(link.getId());

        var addedLink = linksRepository.findByUrl(link.getUrl());

        assertNotNull(addedLink);
        assertEquals(link.getId(), addedLink.getId());
        assertEquals(link.getUrl(), addedLink.getUrl());
    }
}
