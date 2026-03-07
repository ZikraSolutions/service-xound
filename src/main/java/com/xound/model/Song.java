package com.xound.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Song {
    private Long id;
    private String title;
    private String artist;
    private String tone;
    private String content;
    private String lyrics;
    private String notes;
    private Integer bpm;
    private String timeSignature;
    private Long userId;
    private Boolean status;
    private LocalDateTime createdAt;
}
