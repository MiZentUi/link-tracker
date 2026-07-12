package backend.academy.linktracker.scrapper.repository;

import backend.academy.linktracker.scrapper.model.Chat;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatsRepository {
    Chat save(Chat chat);

    Optional<Chat> findById(Long id);

    void deleteById(Long id);

    boolean existsById(Long id);

    void deleteAll();
}
