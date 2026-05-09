package backend.academy.linktracker.scrapper.repository;

import org.springframework.stereotype.Repository;

import backend.academy.linktracker.scrapper.model.Tag;

@Repository
public interface TagsRepository {
    Tag save(Tag tag);

    void delete(Tag tag);

    void deleteById(Long id);
}
