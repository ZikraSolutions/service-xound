package com.xound.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChordSearchResult {
    private String title;
    private String artist;
    private String url;
    private String chords; // null en búsqueda; poblado al hacer fetch
}
