package com.xound.repository;

import com.xound.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<User> rowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setName(rs.getString("name"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setRoleId(rs.getLong("role_id"));
        user.setStatus(rs.getBoolean("status"));
        java.sql.Timestamp createdAt = rs.getTimestamp("created_at");
        user.setCreatedAt(createdAt != null ? createdAt.toLocalDateTime() : null);
        try {
            user.setRoleName(rs.getString("role_name"));
        } catch (Exception e) {
            // no hay columna role_name en esta query
        }
        return user;
    };

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<User> findAll() {
        String sql = "SELECT u.*, r.name AS role_name FROM users u " +
                     "JOIN roles r ON u.role_id = r.id WHERE u.status = true";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public Optional<User> findById(Long id) {
        String sql = "SELECT u.*, r.name AS role_name FROM users u " +
                     "JOIN roles r ON u.role_id = r.id WHERE u.id = ? AND u.status = true";
        return jdbcTemplate.query(sql, rowMapper, id).stream().findFirst();
    }

    public Optional<User> findByUsername(String username) {
        String sql = "SELECT u.*, r.name AS role_name FROM users u " +
                     "JOIN roles r ON u.role_id = r.id WHERE u.username = ? AND u.status = true";
        return jdbcTemplate.query(sql, rowMapper, username).stream().findFirst();
    }

    public int save(User user) {
        String sql = "INSERT INTO users (name, username, password, role_id) VALUES (?, ?, ?, ?)";
        return jdbcTemplate.update(sql, user.getName(), user.getUsername(), user.getPassword(), user.getRoleId());
    }

    public int update(User user) {
        String sql = "UPDATE users SET username = ?, role_id = ? WHERE id = ?";
        return jdbcTemplate.update(sql, user.getUsername(), user.getRoleId(), user.getId());
    }

    public int changeStatus(Long id, boolean status) {
        return jdbcTemplate.update("UPDATE users SET status = ? WHERE id = ?", status, id);
    }

    public int updateRole(Long userId, Long roleId) {
        return jdbcTemplate.update("UPDATE users SET role_id = ? WHERE id = ?", roleId, userId);
    }

    public void deleteById(Long id) {
        // Delete hidden songs
        jdbcTemplate.update("DELETE FROM hidden_songs WHERE user_id = ?", id);
        // Delete favorites
        jdbcTemplate.update("DELETE FROM favorites WHERE user_id = ?", id);
        // Delete setlist songs for user's events
        jdbcTemplate.update("DELETE FROM setlist_songs WHERE event_id IN (SELECT id FROM events WHERE user_id = ?)", id);
        // Delete events
        jdbcTemplate.update("DELETE FROM events WHERE user_id = ?", id);
        // Delete setlist songs referencing user's songs
        jdbcTemplate.update("DELETE FROM setlist_songs WHERE song_id IN (SELECT id FROM songs WHERE user_id = ?)", id);
        // Delete songs
        jdbcTemplate.update("DELETE FROM songs WHERE user_id = ?", id);
        // Remove from band members
        jdbcTemplate.update("DELETE FROM band_members WHERE user_id = ?", id);
        // Delete bands owned by this user
        jdbcTemplate.update("DELETE FROM band_members WHERE band_id IN (SELECT id FROM bands WHERE admin_user_id = ?)", id);
        jdbcTemplate.update("DELETE FROM bands WHERE admin_user_id = ?", id);
        // Clear admin invite references
        jdbcTemplate.update("UPDATE admin_invites SET used_by_user_id = NULL WHERE used_by_user_id = ?", id);
        // Delete user
        jdbcTemplate.update("DELETE FROM users WHERE id = ?", id);
    }
}
