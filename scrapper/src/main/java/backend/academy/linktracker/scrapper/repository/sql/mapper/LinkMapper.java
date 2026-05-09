package backend.academy.linktracker.scrapper.repository.sql.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import backend.academy.linktracker.scrapper.model.Link;

public class LinkMapper implements RowMapper<Link> {

    @Override
    public Link mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Link.builder()
                .id(rs.getLong("id"))
                .url(rs.getString("url"))
                .lastUpdate(rs.getTimestamp("last_update").toLocalDateTime())
                .build();
    }
}
