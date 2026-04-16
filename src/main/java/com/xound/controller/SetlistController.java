package com.xound.controller;

import com.xound.dto.SetlistAddSongRequest;
import com.xound.dto.SetlistReorderItem;
import com.xound.model.SetlistSong;
import com.xound.service.SetlistService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events/{eventId}/setlist")
@Validated
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
    public ResponseEntity<Map<String, String>> addSong(@PathVariable Long eventId,
                                                        @Valid @RequestBody SetlistAddSongRequest request) {
        setlistService.addSong(eventId, request.getSongId());
        return ResponseEntity.ok(Map.of("message", "Cancion agregada al setlist"));
    }

    @DeleteMapping("/{songId}")
    public ResponseEntity<Map<String, String>> removeSong(@PathVariable Long eventId,
                                                           @PathVariable Long songId) {
        setlistService.removeSong(eventId, songId);
        return ResponseEntity.ok(Map.of("message", "Cancion removida del setlist"));
    }

    @PutMapping("/reorder")
    public ResponseEntity<Map<String, String>> reorder(@PathVariable Long eventId,
                                                        @RequestBody @NotEmpty(message = "El nuevo orden no puede estar vacio")
                                                        List<@Valid SetlistReorderItem> newOrder) {
        setlistService.reorder(eventId, newOrder);
        return ResponseEntity.ok(Map.of("message", "Setlist reordenado exitosamente"));
    }
}
