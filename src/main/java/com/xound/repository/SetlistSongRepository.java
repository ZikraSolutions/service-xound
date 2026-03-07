package com.xound.repository;

import com.xound.model.SetlistSong;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SetlistSongRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<SetlistSong> rowMapper = (rs, rowNum) -> {
        SetlistSong ss = new SetlistSong();
        ss.setId(rs.getLong("id"));
        ss.setEventId(rs.getLong("event_id"));
        ss.setSongId(rs.getLong("song_id"));
        ss.setPosition(rs.getInt("position"));
        ss.setSongTitle(rs.getString("song_title"));
        ss.setSongArtist(rs.getString("song_artist"));
        ss.setSongTone(rs.getString("song_tone"));
        ss.setSongContent(rs.getString("song_content"));
        ss.setSongNotes(rs.getString("song_notes"));
        int bpm = rs.getInt("song_bpm");
        ss.setSongBpm(rs.wasNull() ? null : bpm);
        ss.setSongTimeSignature(rs.getString("song_time_signature"));
        return ss;
    };

    public SetlistSongRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<SetlistSong> findByEventId(Long eventId) {
        String sql = "SELECT ss.id, ss.event_id, ss.song_id, ss.position, " +
                     "s.title AS song_title, s.artist AS song_artist, s.tone AS song_tone, " +
                     "s.content AS song_content, s.notes AS song_notes, " +
                     "s.bpm AS song_bpm, s.time_signature AS song_time_signature " +
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
