package backend.academy.linktracker.scrapper.service;

import backend.academy.linktracker.scrapper.exception.ChatAlreadyExistsException;
import backend.academy.linktracker.scrapper.exception.ChatNotFoundException;
import backend.academy.linktracker.scrapper.repository.ChatsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatsService {
    private final ChatsRepository repository;

    public void addId(Long id) {
        if (repository.exists(id)) {
            throw new ChatAlreadyExistsException("Chat id=" + id + " already exists!");
        }
        log.atInfo().addKeyValue("chat_id", id.toString()).log("adding chat");
        repository.save(id);
    }

    public void removeId(Long id) {
        if (!repository.exists(id)) {
            throw new ChatNotFoundException("Chat id=" + id + " id not found!");
        }
        log.atInfo().addKeyValue("chat_id", id.toString()).log("removing chat");
        repository.remove(id);
    }
}
