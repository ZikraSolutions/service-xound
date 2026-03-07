package com.xound.controller;

import com.xound.service.FavoriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @GetMapping
    public ResponseEntity<List<Long>> getFavorites(Authentication auth) {
        Long userId = (Long) auth.getCredentials();
        return ResponseEntity.ok(favoriteService.getFavoriteSongIds(userId));
    }

    @PostMapping("/{songId}")
    public ResponseEntity<?> toggle(@PathVariable Long songId, Authentication auth) {
        Long userId = (Long) auth.getCredentials();
        boolean isFavorite = favoriteService.toggle(songId, userId);
        return ResponseEntity.ok(Map.of("favorite", isFavorite));
    }
}
