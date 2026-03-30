package backend.academy.linktracker.scrapper.dao;

import backend.academy.linktracker.scrapper.model.Link;
import backend.academy.linktracker.scrapper.repository.LinksRepository;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class LinksDao implements LinksRepository {
    private Long idCounter;
    private Map<Long, Link> links;

    @PostConstruct
    public void init() {
        links = new ConcurrentHashMap<>();
        idCounter = 1L;
    }

    @Override
    public void save(Link link) {
        var currentId = idCounter++;
        link.setId(currentId);
        links.put(currentId, link);
    }

    @Override
    public List<Link> findAll() {
        return links.values().stream().toList();
    }

    @Override
    public List<Link> findAllByChatId(Long chatId) {
        return links.values().stream()
                .filter(l -> l.getChatIds().contains(chatId))
                .toList();
    }

    @Override
    public Link findByUrl(String url) {
        return links.values().stream()
                .filter(l -> l.getUrl().equals(url))
                .findAny()
                .orElse(null);
    }

    @Override
    public void remove(Link link) {
        links.remove(link.getId());
    }
}
