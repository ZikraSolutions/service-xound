package com.xound.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetlistSong {
    private Long id;
    private Long eventId;
    private Long songId;
    private Integer position;

    // Datos de la canción (para cuando se hace JOIN)
    private String songTitle;
    private String songArtist;
    private String songTone;
    private String songContent;
    private String songNotes;
    private Integer songBpm;
    private String songTimeSignature;
}
