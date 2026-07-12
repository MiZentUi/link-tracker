package backend.academy.linktracker.bot.state;

import backend.academy.linktracker.bot.model.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StateFactory {

    @Lookup
    protected DefaultState defaultState() {
        return null;
    }

    @Lookup
    protected TrackState trackState() {
        return null;
    }

    @Lookup
    protected UntrackState untrackState() {
        return null;
    }

    public DefaultState getDefaultState(Session session) {
        var state = defaultState();
        state.setSession(session);
        return state;
    }

    public TrackState getTrackState(Session session) {
        var state = trackState();
        state.setSession(session);
        return state;
    }

    public UntrackState getUntrackState(Session session) {
        var state = untrackState();
        state.setSession(session);
        return state;
    }
}
