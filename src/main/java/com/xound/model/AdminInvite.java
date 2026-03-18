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
@Table(name = "admin_invites")
public class AdminInvite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private boolean used;

    @Column(name = "used_by_user_id", insertable = false, updatable = false)
    private Long usedByUserId;

    // El invite fue usado por un usuario (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "used_by_user_id")
    @JsonIgnore
    private User usedByUser;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
