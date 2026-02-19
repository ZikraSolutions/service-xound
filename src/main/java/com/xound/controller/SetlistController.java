package com.xound.controller;

import com.xound.model.SetlistSong;
import com.xound.service.SetlistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events/{eventId}/setlist")
public class SetlistController {

    private final SetlistService setlistService;

    public SetlistController(SetlistService setlistService) {
        this.setlistService = setlistService;
    }

    @GetMapping
    public ResponseEntity<List<SetlistSong>> getSetlist(@PathVariable Long eventId) {
        return ResponseEntity.ok(setlistService.getSetlist(eventId));
    }

    @PostMapping
    public ResponseEntity<?> addSong(@PathVariable Long eventId, @RequestBody Map<String, Long> body) {
        try {
            setlistService.addSong(eventId, body.get("songId"));
            return ResponseEntity.ok(Map.of("message", "Canción agregada al setlist"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{songId}")
    public ResponseEntity<?> removeSong(@PathVariable Long eventId, @PathVariable Long songId) {
        setlistService.removeSong(eventId, songId);
        return ResponseEntity.ok(Map.of("message", "Canción removida del setlist"));
    }

    @PutMapping("/reorder")
    public ResponseEntity<?> reorder(@PathVariable Long eventId,
                                     @RequestBody List<Map<String, Object>> newOrder) {
        try {
            setlistService.reorder(eventId, newOrder);
            return ResponseEntity.ok(Map.of("message", "Setlist reordenado exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
