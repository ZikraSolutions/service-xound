package com.xound.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "band_members")
public class BandMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "band_id", insertable = false, updatable = false)
    private Long bandId;

    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Muchos miembros pertenecen a una banda (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "band_id")
    @JsonIgnore
    private Band band;

    // Cada registro de miembro apunta a un usuario (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    // Datos del JOIN con JdbcTemplate
    @Transient
    private String userName;
    @Transient
    private String userUsername;
    @Transient
    private String roleName;
}
