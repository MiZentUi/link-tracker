package backend.academy.linktracker.bot.state;

import backend.academy.linktracker.bot.handler.CommandHandler;
import backend.academy.linktracker.bot.handler.UntrackHandler;
import com.pengrad.telegrambot.model.Update;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Scope("prototype")
public class UntrackState extends SessionState {
    private enum State {
        READY,
        GET_LINK
    }

    private final UntrackHandler untrackHandler;

    private State state = State.READY;

    public UntrackState(
            @Qualifier("handlersByCommand") Map<String, CommandHandler> handlers,
            StateFactory stateFactory,
            UntrackHandler untrackHandler) {
        super(handlers, stateFactory);

        this.untrackHandler = untrackHandler;
    }

    @Override
    public String handleUpdate(Update update) {
        if (state != State.READY) {
            var cancellation = checkCancellation(update);
            if (cancellation != null) {
                return cancellation;
            }
        }

        log.atInfo().addKeyValue("state", state.toString()).log("current state");

        String message = null;

        switch (state) {
            case READY -> {
                message = untrackHandler.handle(update);
                state = State.GET_LINK;
            }
            case GET_LINK -> {
                message = untrackHandler.handleLink(update);
                session.setState(stateFactory.getDefaultState(session));
            }
        }

        return message;
    }
}
