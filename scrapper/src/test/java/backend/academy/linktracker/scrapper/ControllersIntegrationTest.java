package backend.academy.linktracker.scrapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.academy.linktracker.scrapper.repository.ChatsRepository;
import backend.academy.linktracker.scrapper.repository.LinksRepository;
import backend.academy.linktracker.scrapper.repository.TagsRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Import(TestcontainersConfiguration.class)
class ControllersIntegrationTest {

    @Autowired
    MockMvc mockMvc;

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
    void addAndGetLink() throws Exception {
        mockMvc.perform(post("/tg-chat/1")).andExpect(status().isOk());

        var addLinkBody = """
                {
                    "link": "https://example.com",
                    "tags": ["test"]
                }
                """;
        mockMvc.perform(post("/links")
                        .header("Tg-Chat-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addLinkBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.url").value("https://example.com"))
                .andExpect(jsonPath("$.tags[0]").value("test"));

        mockMvc.perform(get("/links").header("Tg-Chat-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(1))
                .andExpect(jsonPath("$.links[0].id").exists())
                .andExpect(jsonPath("$.links[0].url").value("https://example.com"))
                .andExpect(jsonPath("$.links[0].tags[0]").value("test"));
    }

    @Test
    void addAndRemoveLink() throws Exception {
        mockMvc.perform(post("/tg-chat/1")).andExpect(status().isOk());

        var addLinkBody = """
                {
                    "link": "https://example.com",
                    "tags": ["test"]
                }
                """;
        mockMvc.perform(post("/links")
                        .header("Tg-Chat-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addLinkBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.url").value("https://example.com"))
                .andExpect(jsonPath("$.tags[0]").value("test"));

        var deleteLinkBody = """
                {
                    "link": "https://example.com"
                }
                """;
        mockMvc.perform(delete("/links")
                        .header("Tg-Chat-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(deleteLinkBody))
                .andExpect(status().isOk());

        mockMvc.perform(get("/links").header("Tg-Chat-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(0));
    }

    @Test
    void removeFromNotExistingChat() throws Exception {
        mockMvc.perform(post("/tg-chat/1")).andExpect(status().isOk());

        var addLinkBody = """
                {
                    "link": "https://example.com",
                    "tags": ["test"]
                }
                """;
        mockMvc.perform(post("/links")
                        .header("Tg-Chat-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addLinkBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.url").value("https://example.com"))
                .andExpect(jsonPath("$.tags[0]").value("test"));

        mockMvc.perform(get("/links").header("Tg-Chat-Id", 999)).andExpect(status().isNotFound());

        mockMvc.perform(get("/links").header("Tg-Chat-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(1))
                .andExpect(jsonPath("$.links[0].id").exists())
                .andExpect(jsonPath("$.links[0].url").value("https://example.com"))
                .andExpect(jsonPath("$.links[0].tags[0]").value("test"));
    }

    @Test
    void addLinkForNotExistingChat() throws Exception {
        mockMvc.perform(post("/tg-chat/1")).andExpect(status().isOk());

        var addLinkBody = """
                {
                    "link": "https://example.com",
                    "tags": ["test"]
                }
                """;
        mockMvc.perform(post("/links")
                        .header("Tg-Chat-Id", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addLinkBody))
                .andExpect(status().isNotFound());
    }

    @Test
    void removedChat() throws Exception {
        mockMvc.perform(post("/tg-chat/1")).andExpect(status().isOk());

        mockMvc.perform(delete("/tg-chat/1")).andExpect(status().isOk());

        var addLinkBody = """
                {
                    "link": "https://example.com",
                    "tags": ["test"]
                }
                """;
        mockMvc.perform(post("/links")
                        .header("Tg-Chat-Id", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addLinkBody))
                .andExpect(status().isNotFound());
    }

    @Test
    void removeNotExistingChat() throws Exception {
        mockMvc.perform(delete("/tg-chat/1")).andExpect(status().isNotFound());
    }
}
