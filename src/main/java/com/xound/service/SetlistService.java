package com.xound.service;

import com.xound.model.SetlistSong;
import com.xound.repository.SetlistSongRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SetlistService {

    private final SetlistSongRepository setlistSongRepository;

    public SetlistService(SetlistSongRepository setlistSongRepository) {
        this.setlistSongRepository = setlistSongRepository;
    }

    public List<SetlistSong> getSetlist(Long eventId) {
        return setlistSongRepository.findByEventId(eventId);
    }

    public void addSong(Long eventId, Long songId) {
        int nextPosition = setlistSongRepository.getNextPosition(eventId);
        setlistSongRepository.addSong(eventId, songId, nextPosition);
    }

    public void removeSong(Long eventId, Long songId) {
        setlistSongRepository.removeSong(eventId, songId);
    }

    public void reorder(Long eventId, List<Map<String, Object>> newOrder) {
        for (Map<String, Object> item : newOrder) {
            Long songId = Long.valueOf(item.get("songId").toString());
            Integer position = Integer.valueOf(item.get("position").toString());
            setlistSongRepository.updatePosition(eventId, songId, position);
        }
    }
}
