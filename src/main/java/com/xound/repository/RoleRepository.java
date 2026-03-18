package com.xound.repository;

import com.xound.model.Role;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class RoleRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Role> rowMapper = (rs, rowNum) -> {
        Role role = new Role();
        role.setId(rs.getLong("id"));
        role.setName(rs.getString("name"));
        role.setStatus(rs.getBoolean("status"));
        return role;
    };

    public RoleRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Role> findAll() {
        return jdbcTemplate.query("SELECT * FROM roles WHERE status = true", rowMapper);
    }

    public Optional<Role> findById(Long id) {
        return jdbcTemplate.query("SELECT * FROM roles WHERE id = ? AND status = true", rowMapper, id)
                .stream().findFirst();
    }

    public Optional<Role> findByName(String name) {
        return jdbcTemplate.query("SELECT * FROM roles WHERE name = ? AND status = true", rowMapper, name)
                .stream().findFirst();
    }
}
