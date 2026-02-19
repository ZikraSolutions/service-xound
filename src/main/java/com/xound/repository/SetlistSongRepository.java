package com.xound.repository;

import com.xound.model.SetlistSong;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SetlistSongRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<SetlistSong> rowMapper = (rs, rowNum) -> new SetlistSong(
            rs.getLong("id"),
            rs.getLong("event_id"),
            rs.getLong("song_id"),
            rs.getInt("position"),
            rs.getString("song_title"),
            rs.getString("song_artist"),
            rs.getString("song_tone"),
            rs.getString("song_content"),
            rs.getString("song_notes")
    );

    public SetlistSongRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<SetlistSong> findByEventId(Long eventId) {
        String sql = "SELECT ss.id, ss.event_id, ss.song_id, ss.position, " +
                     "s.title AS song_title, s.artist AS song_artist, s.tone AS song_tone, " +
                     "s.content AS song_content, s.notes AS song_notes " +
                     "FROM setlist_songs ss " +
                     "JOIN songs s ON ss.song_id = s.id " +
                     "WHERE ss.event_id = ? AND s.status = true " +
                     "ORDER BY ss.position";
        return jdbcTemplate.query(sql, rowMapper, eventId);
    }

    public int addSong(Long eventId, Long songId, int position) {
        String sql = "INSERT INTO setlist_songs (event_id, song_id, position) VALUES (?, ?, ?)";
        return jdbcTemplate.update(sql, eventId, songId, position);
    }

    public int removeSong(Long eventId, Long songId) {
        return jdbcTemplate.update(
                "DELETE FROM setlist_songs WHERE event_id = ? AND song_id = ?", eventId, songId);
    }

    public int getNextPosition(Long eventId) {
        Integer max = jdbcTemplate.queryForObject(
                "SELECT COALESCE(MAX(position), 0) FROM setlist_songs WHERE event_id = ?",
                Integer.class, eventId);
        return (max != null ? max : 0) + 1;
    }

    public int updatePosition(Long eventId, Long songId, int newPosition) {
        return jdbcTemplate.update(
                "UPDATE setlist_songs SET position = ? WHERE event_id = ? AND song_id = ?",
                newPosition, eventId, songId);
    }

    public int deleteAllByEventId(Long eventId) {
        return jdbcTemplate.update("DELETE FROM setlist_songs WHERE event_id = ?", eventId);
    }
}
