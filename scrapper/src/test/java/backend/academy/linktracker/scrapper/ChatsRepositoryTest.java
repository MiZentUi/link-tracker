package backend.academy.linktracker.scrapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import backend.academy.linktracker.scrapper.model.Chat;
import backend.academy.linktracker.scrapper.repository.ChatsRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class ChatsRepositoryTest {

    @Autowired
    PostgreSQLContainer postgres;

    @Autowired
    ChatsRepository chatsRepository;

    @AfterEach
    void clean() {
        chatsRepository.deleteAll();
    }

    @Test
    void saveThenFindAndDelete() {
        var chat = Chat.builder().id(42L).build();

        chatsRepository.save(chat);

        assertNotNull(chat.getId());

        var addedChat = chatsRepository.findById(chat.getId()).orElse(null);

        assertNotNull(addedChat);
        assertEquals(chat.getId(), addedChat.getId());

        chatsRepository.deleteById(chat.getId());
        var deletedChat = chatsRepository.findById(chat.getId());

        assertFalse(deletedChat.isPresent());
    }

    @Test
    void saveAndExists() {
        var chat = Chat.builder().id(42L).build();

        chatsRepository.save(chat);

        assertNotNull(chat.getId());
        assertTrue(chatsRepository.existsById(chat.getId()));
    }
}
