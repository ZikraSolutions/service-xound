package com.xound.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "setlist_songs", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"event_id", "song_id"})
})
public class SetlistSong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", insertable = false, updatable = false)
    private Long eventId;

    @Column(name = "song_id", insertable = false, updatable = false)
    private Long songId;

    @Column(nullable = false)
    private Integer position;

    // Muchos setlist_songs pertenecen a un evento (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    @JsonIgnore
    private Event event;

    // Muchos setlist_songs apuntan a una canción (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "song_id")
    @JsonIgnore
    private Song song;

    // Datos de la canción (para cuando se hace JOIN con JdbcTemplate)
    @Transient
    private String songTitle;
    @Transient
    private String songArtist;
    @Transient
    private String songTone;
    @Transient
    private String songContent;
    @Transient
    private String songLyrics;
    @Transient
    private String songNotes;
    @Transient
    private Integer songBpm;
    @Transient
    private String songTimeSignature;
}
