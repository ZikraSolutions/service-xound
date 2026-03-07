package com.xound.service;

import com.xound.repository.HiddenSongRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HiddenSongService {

    private final HiddenSongRepository hiddenSongRepository;

    public HiddenSongService(HiddenSongRepository hiddenSongRepository) {
        this.hiddenSongRepository = hiddenSongRepository;
    }

    public List<Long> getHiddenSongIds(Long userId) {
        return hiddenSongRepository.findHiddenSongIdsByUserId(userId);
    }

    public void hide(Long songId, Long userId) {
        hiddenSongRepository.add(songId, userId);
    }
}
