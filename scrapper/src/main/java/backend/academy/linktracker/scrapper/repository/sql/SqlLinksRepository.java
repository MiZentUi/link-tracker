package backend.academy.linktracker.scrapper.repository.sql;

import backend.academy.linktracker.scrapper.model.Link;
import backend.academy.linktracker.scrapper.repository.LinksRepository;
import backend.academy.linktracker.scrapper.repository.sql.mapper.LinksExtractor;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SqlLinksRepository implements LinksRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Link save(Link link) {
        link.setId(jdbcTemplate.queryForObject(
                "INSERT INTO links (url, last_update) VALUES (?, ?) ON CONFLICT (url) DO UPDATE SET last_update = EXCLUDED.last_update RETURNING id",
                Long.class,
                link.getUrl(),
                link.getLastUpdate()));
        jdbcTemplate.update("DELETE FROM links_chats WHERE link_id = ?", link.getId());
        if (link.getChats() != null) {
            for (var chat : link.getChats()) {
                jdbcTemplate.update(
                        "INSERT INTO links_chats VALUES (?, ?) ON CONFLICT DO NOTHING", link.getId(), chat.getId());
            }
        }
        return link;
    }

    @Override
    public void delete(Link link) {
        jdbcTemplate.update("DELETE FROM links_chats WHERE link_id = ?", link.getId());
        jdbcTemplate.update("DELETE FROM links WHERE id = ?", link.getId());
    }

    @Override
    public List<Link> findAll() {
        return jdbcTemplate.query(
                "SELECT l.id, l.url, l.last_update, ls.chat_id, t.id as tag_id, t.name as tag_name FROM links l JOIN links_chats lc ON l.id = lc.link_id JOIN tags t ON l.id = t.id",
                new LinksExtractor());
    }

    @Override
    public Slice<Link> findAll(Pageable pageable) {
        var links = jdbcTemplate.query(
                "SELECT l.id, l.url, l.last_update, ls.chat_id, t.id as tag_id, t.name as tag_name FROM links l JOIN links_chats lc ON l.id = lc.link_id JOIN tags t ON l.id = t.id WHERE id > ? ORDER BY id ASC LIMIT ?",
                new LinksExtractor(),
                pageable.getOffset(),
                pageable.getPageSize());
        var hasNext = jdbcTemplate.queryForObject(
                "SELECT EXISTS(SELECT 1 FROM links WHERE id > ? LIMIT 1)",
                Boolean.class,
                pageable.next().getOffset());
        return new SliceImpl<>(links, pageable, hasNext != null && hasNext);
    }

    @Override
    public Link findByUrl(String url) {
        return jdbcTemplate
                .query(
                        "SELECT l.id, l.url, l.last_update, ls.chat_id, t.id as tag_id, t.name as tag_name FROM (SELECT * FROM links LIMIT 1) l JOIN links_chats lc ON l.id = lc.link_id JOIN tags t ON l.id = t.id",
                        new LinksExtractor(),
                        url)
                .stream()
                .findAny()
                .orElse(null);
    }

    @Override
    public Optional<Link> findById(Long id) {
        return jdbcTemplate
                .query(
                        "SELECT l.id, l.url, l.last_update, ls.chat_id, t.id as tag_id, t.name as tag_name FROM links l JOIN links_chats lc ON l.id = lc.link_id JOIN tags t ON l.id = t.id WHERE l.id = ?",
                        new LinksExtractor(),
                        id)
                .stream()
                .findAny();
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update("DELETE FROM links");
    }
}
