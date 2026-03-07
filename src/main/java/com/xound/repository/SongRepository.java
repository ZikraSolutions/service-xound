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

    private final RowMapper<Song> rowMapper = (rs, rowNum) -> {
        Song song = new Song();
        song.setId(rs.getLong("id"));
        song.setTitle(rs.getString("title"));
        song.setArtist(rs.getString("artist"));
        song.setTone(rs.getString("tone"));
        song.setContent(rs.getString("content"));
        song.setLyrics(rs.getString("lyrics"));
        song.setNotes(rs.getString("notes"));
        int bpm = rs.getInt("bpm");
        song.setBpm(rs.wasNull() ? null : bpm);
        song.setTimeSignature(rs.getString("time_signature"));
        song.setUserId(rs.getLong("user_id"));
        song.setStatus(rs.getBoolean("status"));
        song.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return song;
    };

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

    public Optional<Song> findByTitleAndArtist(String title, String artist) {
        String sql = "SELECT * FROM songs WHERE LOWER(title) = LOWER(?) AND LOWER(artist) = LOWER(?) AND status = true";
        return jdbcTemplate.query(sql, rowMapper, title, artist).stream().findFirst();
    }

    public int save(Song song) {
        String sql = "INSERT INTO songs (title, artist, tone, content, lyrics, notes, bpm, time_signature, user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                song.getTitle(), song.getArtist(), song.getTone(),
                song.getContent(), song.getLyrics(), song.getNotes(),
                song.getBpm(), song.getTimeSignature(), song.getUserId());
    }

    public int update(Song song) {
        String sql = "UPDATE songs SET title = ?, artist = ?, tone = ?, content = ?, lyrics = ?, notes = ?, bpm = ?, time_signature = ? WHERE id = ?";
        return jdbcTemplate.update(sql,
                song.getTitle(), song.getArtist(), song.getTone(),
                song.getContent(), song.getLyrics(), song.getNotes(),
                song.getBpm(), song.getTimeSignature(), song.getId());
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
