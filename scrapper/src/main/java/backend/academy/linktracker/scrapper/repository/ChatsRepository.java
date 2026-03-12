package backend.academy.linktracker.scrapper.repository;

import org.springframework.stereotype.Repository;

@Repository
public interface ChatsRepository {
    void save(Long chatId);

    void remove(Long chatId);

    boolean exists(Long chatId);
}
