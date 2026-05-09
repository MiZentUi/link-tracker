package backend.academy.linktracker.scrapper.repository.orm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import backend.academy.linktracker.scrapper.model.Tag;
import backend.academy.linktracker.scrapper.repository.TagsRepository;

@Component
public interface JpaTagsRepository extends TagsRepository, JpaRepository<Tag, Long> {
}
