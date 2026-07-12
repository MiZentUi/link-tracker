package backend.academy.linktracker.scrapper.repository.sql.mapper;

import backend.academy.linktracker.scrapper.exception.LinkException;
import backend.academy.linktracker.scrapper.model.Chat;
import backend.academy.linktracker.scrapper.model.Link;
import backend.academy.linktracker.scrapper.model.Tag;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

public class LinksExtractor implements ResultSetExtractor<List<Link>> {

    @Override
    public List<Link> extractData(ResultSet rs) throws SQLException, DataAccessException {
        var links = new HashMap<Long, Link>();

        while (rs.next()) {
            var linkId = rs.getLong("id");
            var link = links.computeIfAbsent(linkId, id -> {
                try {
                    return Link.builder()
                            .id(id)
                            .url(rs.getString("url"))
                            .lastUpdate(
                                    rs.getTimestamp("last_update").toInstant().atOffset(ZoneOffset.UTC))
                            .chats(new HashSet<>())
                            .tags(new HashSet<>())
                            .build();
                } catch (SQLException e) {
                    throw new LinkException(e);
                }
            });

            link.getChats().add(Chat.builder().id(rs.getLong("chat_id")).build());

            link.getTags()
                    .add(Tag.builder()
                            .id(rs.getLong("tag_id"))
                            .name(rs.getString("tag_name"))
                            .build());
        }

        return new ArrayList<>(links.values());
    }
}
