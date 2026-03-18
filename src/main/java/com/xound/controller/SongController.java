package com.xound.controller;

import com.xound.model.Song;
import com.xound.service.SongService;
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
    public ResponseEntity<?> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(songService.findById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Song>> search(@RequestParam String title, Authentication auth) {
        Long userId = (Long) auth.getCredentials();
        return ResponseEntity.ok(songService.searchByTitleAndUserId(title, userId));
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody Song song, Authentication auth) {
        try {
            Long userId = (Long) auth.getCredentials();
            song.setUserId(userId);
            Song result = songService.save(song);
            // Si la canción ya tenía ID, fue restaurada (des-ocultada)
            if (result.getId() != null && result.getId() > 0 && song.getId() == null) {
                return ResponseEntity.ok(Map.of("message", "La canción ya existía y fue restaurada"));
            }
            return ResponseEntity.ok(Map.of("message", "Canción creada exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Song song) {
        try {
            songService.update(id, song);
            return ResponseEntity.ok(Map.of("message", "Canción actualizada exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            songService.delete(id);
            return ResponseEntity.ok(Map.of("message", "Canción eliminada exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
