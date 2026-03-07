package com.xound.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Band {
    private Long id;
    private String name;
    private Long adminUserId;
    private String inviteCode;
    private LocalDateTime createdAt;
}
