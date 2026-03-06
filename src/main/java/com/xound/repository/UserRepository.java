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
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setRoleId(rs.getLong("role_id"));
        user.setStatus(rs.getBoolean("status"));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        // roleName se llena si hay JOIN
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

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT u.*, r.name AS role_name FROM users u " +
                     "JOIN roles r ON u.role_id = r.id WHERE u.email = ? AND u.status = true";
        return jdbcTemplate.query(sql, rowMapper, email).stream().findFirst();
    }

    public int save(User user) {
        String sql = "INSERT INTO users (name, email, password, role_id) VALUES (?, ?, ?, ?)";
        return jdbcTemplate.update(sql, user.getName(), user.getEmail(), user.getPassword(), user.getRoleId());
    }

    public int update(User user) {
        String sql = "UPDATE users SET name = ?, email = ?, role_id = ? WHERE id = ?";
        return jdbcTemplate.update(sql, user.getName(), user.getEmail(), user.getRoleId(), user.getId());
    }

    public int changeStatus(Long id, boolean status) {
        return jdbcTemplate.update("UPDATE users SET status = ? WHERE id = ?", status, id);
    }

    public int updateRole(Long userId, Long roleId) {
        return jdbcTemplate.update("UPDATE users SET role_id = ? WHERE id = ?", roleId, userId);
    }
}
