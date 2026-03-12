package backend.academy.linktracker.scrapper.service;

import org.springframework.stereotype.Service;

import backend.academy.linktracker.scrapper.exception.ChatAlreadyExistsException;
import backend.academy.linktracker.scrapper.exception.ChatNotFoundException;
import backend.academy.linktracker.scrapper.repository.ChatsRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ChatsService {
    private ChatsRepository repository;

    public void addId(Long id) {
        if (repository.exists(id)) {
            throw new ChatAlreadyExistsException("Chat id=" + id + " already exists!");
        }
        repository.save(id);
    }

    public void removeId(Long id) {
        if (!repository.exists(id)) {
            throw new ChatNotFoundException("Chat id=" + id + " id not found!");
        }
        repository.remove(id);
    }
}
