package com.xound.repository;

import com.xound.model.AdminInvite;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class AdminInviteRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<AdminInvite> rowMapper = (rs, rowNum) -> {
        AdminInvite invite = new AdminInvite();
        invite.setId(rs.getLong("id"));
        invite.setCode(rs.getString("code"));
        invite.setUsed(rs.getBoolean("used"));
        Long usedBy = rs.getLong("used_by_user_id");
        invite.setUsedByUserId(rs.wasNull() ? null : usedBy);
        invite.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return invite;
    };

    public AdminInviteRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int save(String code) {
        return jdbcTemplate.update("INSERT INTO admin_invites (code) VALUES (?)", code);
    }

    public Optional<AdminInvite> findByCode(String code) {
        return jdbcTemplate.query("SELECT * FROM admin_invites WHERE code = ? AND used = false", rowMapper, code)
                .stream().findFirst();
    }

    public int markUsed(Long id, Long userId) {
        return jdbcTemplate.update("UPDATE admin_invites SET used = true, used_by_user_id = ? WHERE id = ?", userId, id);
    }

    public List<AdminInvite> findAll() {
        return jdbcTemplate.query("SELECT * FROM admin_invites ORDER BY created_at DESC", rowMapper);
    }
}
