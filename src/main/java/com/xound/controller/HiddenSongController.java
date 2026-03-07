package com.xound.controller;

import com.xound.service.HiddenSongService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hidden-songs")
public class HiddenSongController {

    private final HiddenSongService hiddenSongService;

    public HiddenSongController(HiddenSongService hiddenSongService) {
        this.hiddenSongService = hiddenSongService;
    }

    @GetMapping
    public ResponseEntity<List<Long>> getHiddenSongs(Authentication auth) {
        Long userId = (Long) auth.getCredentials();
        return ResponseEntity.ok(hiddenSongService.getHiddenSongIds(userId));
    }

    @PostMapping("/{songId}")
    public ResponseEntity<?> hide(@PathVariable Long songId, Authentication auth) {
        Long userId = (Long) auth.getCredentials();
        hiddenSongService.hide(songId, userId);
        return ResponseEntity.ok(Map.of("hidden", true));
    }
}
