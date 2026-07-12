package backend.academy.linktracker.scrapper.repository.sql;

import backend.academy.linktracker.scrapper.model.Tag;
import backend.academy.linktracker.scrapper.repository.TagsRepository;
import backend.academy.linktracker.scrapper.repository.sql.mapper.TagMapper;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SqlTagsRepository implements TagsRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Tag save(Tag tag) {
        tag.setId(jdbcTemplate.queryForObject(
                "INSERT INTO tags (chat_id, link_id, name) VALUES (?, ?, ?) RETURNING id",
                Long.class,
                tag.getChat().getId(),
                tag.getLink().getId(),
                tag.getName()));
        return tag;
    }

    @Override
    public List<Tag> findAll() {
        return jdbcTemplate.query("SELECT * FROM tags", new TagMapper());
    }

    @Override
    public Optional<Tag> findById(Long id) {
        return jdbcTemplate.query("SELECT * FROM tags WHERE id = ?", new TagMapper(), id).stream()
                .findAny();
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM tags WHERE id = ?", id);
    }

    @Override
    public void delete(Tag tag) {
        jdbcTemplate.update("DELETE FROM tags WHERE id = ?", tag.getId());
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update("DELETE FROM tags");
    }
}
