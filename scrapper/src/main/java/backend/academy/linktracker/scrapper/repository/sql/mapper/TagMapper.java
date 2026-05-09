package backend.academy.linktracker.scrapper.repository.sql.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import backend.academy.linktracker.scrapper.model.Tag;

public class TagMapper implements RowMapper<Tag> {

    @Override
    public Tag mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Tag.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .build();
    }
}
