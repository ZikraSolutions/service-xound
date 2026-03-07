package com.xound.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Favorite {
    private Long id;
    private Long songId;
    private Long userId;
    private LocalDateTime createdAt;
}
