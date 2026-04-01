package backend.academy.linktracker.bot.repository;

import backend.academy.linktracker.bot.model.Session;
import java.util.Optional;

public interface SessionRepository {
    void save(Session session);

    Optional<Session> findByChatId(Long chat_id);
}
