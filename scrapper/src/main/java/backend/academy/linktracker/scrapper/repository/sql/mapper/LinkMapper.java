package backend.academy.linktracker.scrapper.repository.sql.mapper;

import backend.academy.linktracker.scrapper.model.Link;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneOffset;
import org.springframework.jdbc.core.RowMapper;

public class LinkMapper implements RowMapper<Link> {

    @Override
    public Link mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Link.builder()
                .id(rs.getLong("id"))
                .url(rs.getString("url"))
                .lastUpdate(rs.getTimestamp("last_update").toInstant().atOffset(ZoneOffset.UTC))
                .build();
    }
}
