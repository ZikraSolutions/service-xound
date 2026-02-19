package com.xound.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    private Long id;
    private String title;
    private LocalDateTime eventDate;
    private String venue;
    private Boolean published;
    private String shareCode;
    private Long userId;
    private Boolean status;
    private LocalDateTime createdAt;
}
