package backend.academy.linktracker.scrapper.repository.orm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import backend.academy.linktracker.scrapper.model.Link;
import backend.academy.linktracker.scrapper.repository.LinksRepository;

@Component
public interface JpaLinksRepository extends LinksRepository, JpaRepository<Link, Long> {
}
