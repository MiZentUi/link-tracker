package backend.academy.linktracker.scrapper.repository.sql.mapper;

import backend.academy.linktracker.scrapper.model.Chat;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class ChatMapper implements RowMapper<Chat> {

    @Override
    public Chat mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Chat.builder().id(rs.getLong("id")).build();
    }
}
