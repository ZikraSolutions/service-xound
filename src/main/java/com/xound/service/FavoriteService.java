package com.xound.service;

import com.xound.repository.FavoriteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;

    public FavoriteService(FavoriteRepository favoriteRepository) {
        this.favoriteRepository = favoriteRepository;
    }

    public List<Long> getFavoriteSongIds(Long userId) {
        return favoriteRepository.findSongIdsByUserId(userId);
    }

    public boolean toggle(Long songId, Long userId) {
        if (favoriteRepository.exists(songId, userId)) {
            favoriteRepository.remove(songId, userId);
            return false;
        } else {
            favoriteRepository.add(songId, userId);
            return true;
        }
    }
}
