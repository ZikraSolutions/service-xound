package com.xound.repository;

import com.xound.model.Event;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class EventRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Event> rowMapper = (rs, rowNum) -> new Event(
            rs.getLong("id"),
            rs.getString("title"),
            rs.getTimestamp("event_date").toLocalDateTime(),
            rs.getString("venue"),
            rs.getBoolean("published"),
            rs.getString("share_code"),
            rs.getLong("user_id"),
            rs.getBoolean("status"),
            rs.getTimestamp("created_at").toLocalDateTime()
    );

    public EventRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Event> findAll() {
        return jdbcTemplate.query(
                "SELECT * FROM events WHERE status = true ORDER BY event_date DESC", rowMapper);
    }

    public List<Event> findPublished() {
        return jdbcTemplate.query(
                "SELECT * FROM events WHERE published = true AND status = true ORDER BY event_date DESC",
                rowMapper);
    }

    public Optional<Event> findById(Long id) {
        return jdbcTemplate.query(
                "SELECT * FROM events WHERE id = ? AND status = true", rowMapper, id)
                .stream().findFirst();
    }

    public Optional<Event> findByShareCode(String code) {
        return jdbcTemplate.query(
                "SELECT * FROM events WHERE share_code = ? AND published = true AND status = true",
                rowMapper, code).stream().findFirst();
    }

    public int save(Event event) {
        String sql = "INSERT INTO events (title, event_date, venue, share_code, user_id) VALUES (?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                event.getTitle(), event.getEventDate(), event.getVenue(),
                event.getShareCode(), event.getUserId());
    }

    public int update(Event event) {
        String sql = "UPDATE events SET title = ?, event_date = ?, venue = ? WHERE id = ?";
        return jdbcTemplate.update(sql,
                event.getTitle(), event.getEventDate(), event.getVenue(), event.getId());
    }

    public int publish(Long id, boolean published) {
        return jdbcTemplate.update("UPDATE events SET published = ? WHERE id = ?", published, id);
    }

    public int changeStatus(Long id, boolean status) {
        return jdbcTemplate.update("UPDATE events SET status = ? WHERE id = ?", status, id);
    }
}
