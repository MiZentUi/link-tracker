package backend.academy.linktracker.scrapper.repository.orm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import backend.academy.linktracker.scrapper.model.Chat;
import backend.academy.linktracker.scrapper.repository.ChatsRepository;

@Component
public interface JpaChatsRepository extends ChatsRepository, JpaRepository<Chat, Long> {
}
