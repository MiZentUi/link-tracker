package backend.academy.linktracker.scrapper.service;

import org.springframework.stereotype.Service;

import backend.academy.linktracker.scrapper.exception.ChatAlreadyExistsException;
import backend.academy.linktracker.scrapper.exception.ChatNotFoundException;
import backend.academy.linktracker.scrapper.repository.ChatsRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class ChatsService {
    private ChatsRepository repository;

    public void addId(Long id) {
        if (repository.exists(id)) {
            throw new ChatAlreadyExistsException("Chat id=" + id + " already exists!");
        }
        log.info("adding chat with id {}", id);
        repository.save(id);
    }

    public void removeId(Long id) {
        if (!repository.exists(id)) {
            throw new ChatNotFoundException("Chat id=" + id + " id not found!");
        }
        log.info("removing chat with id {}", id);
        repository.remove(id);
    }
}
