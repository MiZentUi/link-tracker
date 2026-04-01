package backend.academy.linktracker.bot.state;

import backend.academy.linktracker.bot.dto.AddLinkRequest;
import backend.academy.linktracker.bot.handler.CommandHandler;
import backend.academy.linktracker.bot.handler.TrackHandler;
import com.pengrad.telegrambot.model.Update;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Scope("prototype")
public class TrackState extends SessionState {
    private enum State {
        READY,
        GET_LINK,
        GET_TAGS
    }

    private final TrackHandler trackHandler;

    private AddLinkRequest linkRequest;
    private State state = State.READY;

    public TrackState(
            @Qualifier("handlersByCommand") Map<String, CommandHandler> handlers,
            StateFactory stateFactory,
            TrackHandler trackHandler) {
        super(handlers, stateFactory);

        this.trackHandler = trackHandler;
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
                linkRequest = new AddLinkRequest();
                message = trackHandler.handle(update);
                state = State.GET_LINK;
            }
            case GET_LINK -> {
                message = trackHandler.handleLink(update, linkRequest);
                state = State.GET_TAGS;
            }
            case GET_TAGS -> {
                message = trackHandler.handleTags(update, linkRequest);
                state = State.READY;
                session.setState(stateFactory.getDefaultState(session));
            }
        }

        return message;
    }
}
