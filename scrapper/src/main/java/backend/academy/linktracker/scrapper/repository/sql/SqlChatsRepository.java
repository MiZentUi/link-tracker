package backend.academy.linktracker.scrapper.repository.sql;

import backend.academy.linktracker.scrapper.model.Chat;
import backend.academy.linktracker.scrapper.repository.ChatsRepository;
import backend.academy.linktracker.scrapper.repository.LinksRepository;
import backend.academy.linktracker.scrapper.repository.sql.mapper.ChatMapper;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SqlChatsRepository implements ChatsRepository {
    private final JdbcTemplate jdbcTemplate;
    private final LinksRepository linksRepository;

    @Override
    public Chat save(Chat chat) {
        jdbcTemplate.update("INSERT INTO chats VALUES (?)", chat.getId());
        if (chat.getLinks() != null) {
            for (var link : chat.getLinks()) {
                jdbcTemplate.update("INSERT INTO links_chats VALUES (?, ?)", link.getId(), chat.getId());
            }
        }
        return chat;
    }

    @Override
    public Optional<Chat> findById(Long id) {
        var chat = jdbcTemplate.query("SELECT * FROM chats WHERE id = ?", new ChatMapper(), id).stream()
                .findAny()
                .orElse(null);
        if (chat != null) {
            chat.setLinks(jdbcTemplate
                    .queryForList(
                            "SELECT link_id FROM chats JOIN links_chats ON chats.id = links_chats.chat_id JOIN links ON links.id = link_id WHERE chats.id = ?",
                            Long.class,
                            id)
                    .stream()
                    .map(linksRepository::findById)
                    .map(Optional::get)
                    .collect(Collectors.toSet()));
        }
        return Optional.of(chat);
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM chats WHERE id = ?", id);
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update("DELETE FROM chats");
    }

    @Override
    public boolean existsById(Long id) {
        var exists = jdbcTemplate.queryForObject(
                "SELECT EXISTS(SELECT 1 FROM chats WHERE id = ? LIMIT 1)", Boolean.class, id);
        return exists != null && exists;
    }
}
