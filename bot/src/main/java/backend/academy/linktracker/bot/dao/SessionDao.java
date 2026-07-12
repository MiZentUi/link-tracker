package backend.academy.linktracker.bot.dao;

import backend.academy.linktracker.bot.model.Session;
import backend.academy.linktracker.bot.repository.SessionRepository;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Component;

@Component
public class SessionDao implements SessionRepository {
    private final AtomicLong idCounter;
    private final Map<Long, Session> sessions;

    public SessionDao() {
        sessions = new ConcurrentHashMap<>();
        idCounter = new AtomicLong(1);
    }

    @Override
    public void save(Session session) {
        var currentId = idCounter.getAndIncrement();
        session.setId(currentId);
        sessions.put(currentId, session);
    }

    @Override
    public Optional<Session> findByChatId(Long chatId) {
        return sessions.values().stream()
                .filter(s -> s.getChatId().equals(chatId))
                .findAny();
    }
}
