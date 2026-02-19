package com.xound.repository;

import com.xound.model.Song;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SongRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Song> rowMapper = (rs, rowNum) -> new Song(
            rs.getLong("id"),
            rs.getString("title"),
            rs.getString("artist"),
            rs.getString("tone"),
            rs.getString("content"),
            rs.getString("notes"),
            rs.getLong("user_id"),
            rs.getBoolean("status"),
            rs.getTimestamp("created_at").toLocalDateTime()
    );

    public SongRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Song> findAll() {
        return jdbcTemplate.query("SELECT * FROM songs WHERE status = true ORDER BY title", rowMapper);
    }

    public Optional<Song> findById(Long id) {
        return jdbcTemplate.query("SELECT * FROM songs WHERE id = ? AND status = true", rowMapper, id)
                .stream().findFirst();
    }

    public List<Song> searchByTitle(String title) {
        String sql = "SELECT * FROM songs WHERE LOWER(title) LIKE LOWER(?) AND status = true ORDER BY title";
        return jdbcTemplate.query(sql, rowMapper, "%" + title + "%");
    }

    public int save(Song song) {
        String sql = "INSERT INTO songs (title, artist, tone, content, notes, user_id) VALUES (?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                song.getTitle(), song.getArtist(), song.getTone(),
                song.getContent(), song.getNotes(), song.getUserId());
    }

    public int update(Song song) {
        String sql = "UPDATE songs SET title = ?, artist = ?, tone = ?, content = ?, notes = ? WHERE id = ?";
        return jdbcTemplate.update(sql,
                song.getTitle(), song.getArtist(), song.getTone(),
                song.getContent(), song.getNotes(), song.getId());
    }

    public int changeStatus(Long id, boolean status) {
        return jdbcTemplate.update("UPDATE songs SET status = ? WHERE id = ?", status, id);
    }

    public boolean isInActiveSetlist(Long songId) {
        String sql = "SELECT COUNT(*) FROM setlist_songs ss " +
                     "JOIN events e ON ss.event_id = e.id " +
                     "WHERE ss.song_id = ? AND e.status = true AND e.published = true";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, songId);
        return count != null && count > 0;
    }
}
