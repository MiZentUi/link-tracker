package backend.academy.linktracker.scrapper.repository;

import backend.academy.linktracker.scrapper.model.Link;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

@Repository
public interface LinksRepository {
    Link save(Link link);

    List<Link> findAll();

    Optional<Link> findById(Long id);

    Link findByUrl(String url);

    void delete(Link link);
}
