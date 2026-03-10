package com.xound.repository;

import com.xound.model.Band;
import com.xound.model.BandMember;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class BandRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Band> bandMapper = (rs, rowNum) -> {
        Band band = new Band();
        band.setId(rs.getLong("id"));
        band.setName(rs.getString("name"));
        band.setAdminUserId(rs.getLong("admin_user_id"));
        band.setInviteCode(rs.getString("invite_code"));
        java.sql.Timestamp createdAt = rs.getTimestamp("created_at");
        band.setCreatedAt(createdAt != null ? createdAt.toLocalDateTime() : null);
        return band;
    };

    private final RowMapper<BandMember> memberMapper = (rs, rowNum) -> {
        BandMember m = new BandMember();
        m.setId(rs.getLong("id"));
        m.setBandId(rs.getLong("band_id"));
        m.setUserId(rs.getLong("user_id"));
        java.sql.Timestamp memberCreatedAt = rs.getTimestamp("created_at");
        m.setCreatedAt(memberCreatedAt != null ? memberCreatedAt.toLocalDateTime() : null);
        m.setUserName(rs.getString("user_name"));
        m.setUserUsername(rs.getString("user_username"));
        m.setRoleName(rs.getString("role_name"));
        return m;
    };

    public BandRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Band> findByAdminUserId(Long adminUserId) {
        return jdbcTemplate.query("SELECT * FROM bands WHERE admin_user_id = ?", bandMapper, adminUserId)
                .stream().findFirst();
    }

    public Optional<Band> findByInviteCode(String inviteCode) {
        return jdbcTemplate.query("SELECT * FROM bands WHERE invite_code = ?", bandMapper, inviteCode)
                .stream().findFirst();
    }

    public int save(Band band) {
        return jdbcTemplate.update(
                "INSERT INTO bands (name, admin_user_id, invite_code) VALUES (?, ?, ?)",
                band.getName(), band.getAdminUserId(), band.getInviteCode());
    }

    public List<BandMember> findMembers(Long bandId) {
        String sql = "SELECT bm.id, bm.band_id, bm.user_id, bm.created_at, " +
                     "u.name AS user_name, u.username AS user_username, r.name AS role_name " +
                     "FROM band_members bm " +
                     "JOIN users u ON bm.user_id = u.id " +
                     "JOIN roles r ON u.role_id = r.id " +
                     "WHERE bm.band_id = ? AND u.status = true " +
                     "ORDER BY bm.created_at";
        return jdbcTemplate.query(sql, memberMapper, bandId);
    }

    public int addMember(Long bandId, Long userId) {
        return jdbcTemplate.update(
                "INSERT INTO band_members (band_id, user_id) VALUES (?, ?) ON CONFLICT DO NOTHING",
                bandId, userId);
    }

    public int removeMember(Long bandId, Long userId) {
        return jdbcTemplate.update(
                "DELETE FROM band_members WHERE band_id = ? AND user_id = ?",
                bandId, userId);
    }

    public boolean isMember(Long bandId, Long userId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM band_members WHERE band_id = ? AND user_id = ?",
                Integer.class, bandId, userId);
        return count != null && count > 0;
    }

    public int updateInviteCode(Long bandId, String newCode) {
        return jdbcTemplate.update("UPDATE bands SET invite_code = ? WHERE id = ?", newCode, bandId);
    }

    public Optional<Band> findByMemberUserId(Long userId) {
        String sql = "SELECT b.* FROM bands b JOIN band_members bm ON b.id = bm.band_id WHERE bm.user_id = ?";
        return jdbcTemplate.query(sql, bandMapper, userId).stream().findFirst();
    }
}
