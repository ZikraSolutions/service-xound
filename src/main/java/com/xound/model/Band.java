package com.xound.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bands")
public class Band {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "admin_user_id", insertable = false, updatable = false)
    private Long adminUserId;

    // Una banda tiene un administrador (N:1 — cada banda pertenece a un usuario)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_user_id")
    @JsonIgnore
    private User adminUser;

    @Column(name = "invite_code", unique = true)
    private String inviteCode;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Una banda tiene muchos miembros (1:N)
    @OneToMany(mappedBy = "band", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<BandMember> members;
}
