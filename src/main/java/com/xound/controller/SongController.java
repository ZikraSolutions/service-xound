package com.xound.controller;

import com.xound.dto.ArtworkRequest;
import com.xound.dto.SongRequest;
import com.xound.model.Song;
import com.xound.service.SongService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/songs")
public class SongController {

    private final SongService songService;

    public SongController(SongService songService) {
        this.songService = songService;
    }

    @GetMapping
    public ResponseEntity<List<Song>> findAll(Authentication auth) {
        Long userId = (Long) auth.getCredentials();
        return ResponseEntity.ok(songService.findAllByUserId(userId));
    }

    @GetMapping("/band")
    public ResponseEntity<List<Song>> findByBand(Authentication auth) {
        Long userId = (Long) auth.getCredentials();
        return ResponseEntity.ok(songService.findByBand(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Song> findById(@PathVariable Long id) {
        return ResponseEntity.ok(songService.findById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Song>> search(@RequestParam String title, Authentication auth) {
        Long userId = (Long) auth.getCredentials();
        return ResponseEntity.ok(songService.searchByTitleAndUserId(title, userId));
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> save(@Valid @RequestBody SongRequest request,
                                                     Authentication auth) {
        Long userId = (Long) auth.getCredentials();
        Song result = songService.save(request, userId);
        if (result.getId() != null && result.getId() > 0) {
            return ResponseEntity.ok(Map.of("message", "La cancion ya existia y fue restaurada"));
        }
        return ResponseEntity.ok(Map.of("message", "Cancion creada exitosamente"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> update(@PathVariable Long id,
                                                       @Valid @RequestBody SongRequest request) {
        songService.update(id, request);
        return ResponseEntity.ok(Map.of("message", "Cancion actualizada exitosamente"));
    }

    @PatchMapping("/{id}/artwork")
    public ResponseEntity<Map<String, String>> updateArtwork(@PathVariable Long id,
                                                              @Valid @RequestBody ArtworkRequest request) {
        songService.updateArtworkUrl(id, request.getArtworkUrl());
        return ResponseEntity.ok(Map.of("message", "Caratula actualizada exitosamente"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        songService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Cancion eliminada exitosamente"));
    }
}
