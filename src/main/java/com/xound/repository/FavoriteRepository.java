package com.xound.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FavoriteRepository {

    private final JdbcTemplate jdbcTemplate;

    public FavoriteRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Long> findSongIdsByUserId(Long userId) {
        return jdbcTemplate.queryForList(
                "SELECT song_id FROM favorites WHERE user_id = ?", Long.class, userId);
    }

    public boolean exists(Long songId, Long userId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM favorites WHERE song_id = ? AND user_id = ?",
                Integer.class, songId, userId);
        return count != null && count > 0;
    }

    public int add(Long songId, Long userId) {
        return jdbcTemplate.update(
                "INSERT INTO favorites (song_id, user_id) VALUES (?, ?) ON CONFLICT DO NOTHING",
                songId, userId);
    }

    public int remove(Long songId, Long userId) {
        return jdbcTemplate.update(
                "DELETE FROM favorites WHERE song_id = ? AND user_id = ?",
                songId, userId);
    }
}
