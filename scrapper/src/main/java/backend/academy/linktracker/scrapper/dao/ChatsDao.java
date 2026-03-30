package backend.academy.linktracker.scrapper.dao;

import backend.academy.linktracker.scrapper.repository.ChatsRepository;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Component;

@Component
public class ChatsDao implements ChatsRepository {
    private final List<Long> chatIds;

    public ChatsDao() {
        chatIds = new CopyOnWriteArrayList<>();
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
