package backend.academy.linktracker.scrapper.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import backend.academy.linktracker.scrapper.repository.ChatsRepository;
import jakarta.annotation.PostConstruct;

@Component
public class ChatsDao implements ChatsRepository {
    private List<Long> chatIds;

    @PostConstruct
    private void init() {
        chatIds = new ArrayList<>();
    }

    @Override
    public void save(Long chatId) {
        chatIds.add(chatId);
    }

    @Override
    public void remove(Long chatId) {
        chatIds.remove(chatId);
    }

    @Override
    public boolean exists(Long chatId) {
        return chatIds.stream().anyMatch(i -> i.equals(chatId));
    }
}
