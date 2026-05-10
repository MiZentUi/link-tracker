package backend.academy.linktracker.scrapper.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import backend.academy.linktracker.scrapper.model.Tag;

@Repository
public interface TagsRepository {
    Tag save(Tag tag);

    Optional<Tag> findById(Long id);

    List<Tag> findAll();

    void delete(Tag tag);

    void deleteById(Long id);

    void deleteAll();
}
