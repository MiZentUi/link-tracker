package backend.academy.linktracker.bot.model;

import backend.academy.linktracker.bot.state.SessionState;
import backend.academy.linktracker.bot.state.StateFactory;
import com.pengrad.telegrambot.model.Update;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
public class Session {

    private Long id;

    @NotNull
    private final Long chatId;

    private SessionState state;

    public Session(Long chatId, StateFactory stateFactory) {
        this.chatId = chatId;
        state = stateFactory.getDefaultState(this);
    }

    public void changeState(SessionState state) {
        this.state = state;
    }

    public String handleUpdate(Update update) {
        return state.handleUpdate(update);
    }
}
