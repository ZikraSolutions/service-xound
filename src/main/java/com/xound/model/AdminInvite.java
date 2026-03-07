package com.xound.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminInvite {
    private Long id;
    private String code;
    private boolean used;
    private Long usedByUserId;
    private LocalDateTime createdAt;
}
