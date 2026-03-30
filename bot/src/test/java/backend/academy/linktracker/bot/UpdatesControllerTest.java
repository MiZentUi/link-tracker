package backend.academy.linktracker.bot;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.academy.linktracker.bot.controller.UpdatesController;
import backend.academy.linktracker.bot.service.BotService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UpdatesController.class)
class UpdatesControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    BotService botService;

    @Test
    void updates_withValidBody_returnsOk() throws Exception {
        var body = """
                {
                  "id": 1,
                  "url": "https://example.com",
                  "description": "Test update",
                  "tgChatIds": [123, 456]
                }
                """;

        mockMvc.perform(post("/updates").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk());

        verify(botService, times(1)).sendLinkUpdate(ArgumentMatchers.any());
    }

    @Test
    void updates_withNotValidBody_returnsBadRequest() throws Exception {
        var body = """
                {
                  "id": 1,
                  "url": "https://example.com",
                  "description": 123,
                  "tgChatIds": 123
                }
                """;

        mockMvc.perform(post("/updates").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }
}
