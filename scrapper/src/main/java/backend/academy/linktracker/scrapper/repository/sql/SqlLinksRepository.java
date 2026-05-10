package backend.academy.linktracker.scrapper.repository.sql;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import backend.academy.linktracker.scrapper.model.Chat;
import backend.academy.linktracker.scrapper.model.Link;
import backend.academy.linktracker.scrapper.model.Tag;
import backend.academy.linktracker.scrapper.repository.LinksRepository;
import backend.academy.linktracker.scrapper.repository.sql.mapper.ChatMapper;
import backend.academy.linktracker.scrapper.repository.sql.mapper.LinkMapper;
import backend.academy.linktracker.scrapper.repository.sql.mapper.TagMapper;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SqlLinksRepository implements LinksRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Link save(Link link) {
        link.setId(jdbcTemplate.queryForObject(
                "INSERT INTO links (url, last_update) VALUES (?, ?) ON CONFLICT (url) DO UPDATE SET last_update = EXCLUDED.last_update RETURNING id",
                Long.class,
                link.getUrl(), link.getLastUpdate()));
        jdbcTemplate.update("DELETE FROM links_chats WHERE link_id = ?", link.getId());
        if (link.getChats() != null) {
            for (var chat : link.getChats()) {
                jdbcTemplate.update("INSERT INTO links_chats VALUES (?, ?) ON CONFLICT DO NOTHING", link.getId(),
                        chat.getId());
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
        var links = jdbcTemplate.query("SELECT * FROM links", new LinkMapper());
        for (var link : links) {
            link.setChats(getChats(link.getId()));
            link.setTags(getTags(link.getId()));
        }
        return links;
    }

    @Override
    public Link findByUrl(String url) {
        var link = jdbcTemplate.query("SELECT * FROM links WHERE url = ? LIMIT 1", new LinkMapper(), url).stream()
                .findAny()
                .orElse(null);
        if (link != null) {
            link.setChats(getChats(link.getId()));
            link.setTags(getTags(link.getId()));
        }
        return link;
    }

    @Override
    public Optional<Link> findById(Long id) {
        var link = jdbcTemplate.query("SELECT * FROM links WHERE id = ?", new BeanPropertyRowMapper<>(Link.class), id)
                .stream().findAny();
        if (link.isPresent()) {
            var l = link.get();
            l.setChats(getChats(l.getId()));
            l.setTags(getTags(l.getId()));
        }
        return link;
    }

    private Set<Chat> getChats(Long id) {
        return jdbcTemplate.query(
                "SELECT chats.id FROM links JOIN links_chats ON links.id = links_chats.link_id JOIN chats ON chats.id = chat_id WHERE links.id = ?",
                new ChatMapper(), id).stream().collect(Collectors.toSet());
    }

    private Set<Tag> getTags(Long id) {
        return jdbcTemplate.query(
                "SELECT tags.id, tags.name FROM links JOIN tags ON links.id = tags.link_id WHERE links.id = ?",
                new TagMapper(), id).stream().collect(Collectors.toSet());
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update("DELETE FROM links");
    }
}
