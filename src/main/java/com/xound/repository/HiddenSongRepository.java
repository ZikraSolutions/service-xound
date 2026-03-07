package com.xound.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class HiddenSongRepository {

    private final JdbcTemplate jdbcTemplate;

    public HiddenSongRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Long> findHiddenSongIdsByUserId(Long userId) {
        return jdbcTemplate.queryForList(
                "SELECT song_id FROM hidden_songs WHERE user_id = ?", Long.class, userId);
    }

    public int add(Long songId, Long userId) {
        return jdbcTemplate.update(
                "INSERT INTO hidden_songs (song_id, user_id) VALUES (?, ?) ON CONFLICT DO NOTHING",
                songId, userId);
    }

    public int remove(Long songId, Long userId) {
        return jdbcTemplate.update(
                "DELETE FROM hidden_songs WHERE song_id = ? AND user_id = ?",
                songId, userId);
    }

    public boolean isHidden(Long songId, Long userId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM hidden_songs WHERE song_id = ? AND user_id = ?",
                Integer.class, songId, userId);
        return count != null && count > 0;
    }
}
