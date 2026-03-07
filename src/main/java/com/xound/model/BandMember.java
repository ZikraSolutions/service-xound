package com.xound.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BandMember {
    private Long id;
    private Long bandId;
    private Long userId;
    private LocalDateTime createdAt;

    // JOIN data
    private String userName;
    private String userUsername;
    private String roleName;
}
