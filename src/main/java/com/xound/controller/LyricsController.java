package com.xound.controller;

import com.xound.service.LyricsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/lyrics")
@Tag(name = "Letras de canciones", description = "Busca letras de canciones usando lyrics.ovh")
public class LyricsController {

    private final LyricsService lyricsService;

    public LyricsController(LyricsService lyricsService) {
        this.lyricsService = lyricsService;
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar letra de una cancion",
               description = "Busca la letra por artista y titulo usando la API de lyrics.ovh")
    public ResponseEntity<?> search(
            @Parameter(description = "Nombre del artista") @RequestParam String artist,
            @Parameter(description = "Titulo de la cancion") @RequestParam String title) {

        if (artist == null || artist.isBlank() || title == null || title.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Los parametros 'artist' y 'title' son requeridos"));
        }

        String lyrics = lyricsService.searchLyrics(artist, title);

        if (lyrics == null) {
            return ResponseEntity.ok(Map.of(
                    "found", false,
                    "message", "No se encontro la letra para '" + title + "' de " + artist
            ));
        }

        return ResponseEntity.ok(Map.of(
                "found", true,
                "artist", artist.trim(),
                "title", title.trim(),
                "lyrics", lyrics
        ));
    }
}
