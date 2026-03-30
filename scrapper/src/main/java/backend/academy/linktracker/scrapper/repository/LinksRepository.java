package backend.academy.linktracker.scrapper.repository;

import backend.academy.linktracker.scrapper.model.Link;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface LinksRepository {
    void save(Link link);

    void remove(Link link);

    List<Link> findAll();

    List<Link> findAllByChatId(Long chatId);

    Link findByUrl(String link);
}
