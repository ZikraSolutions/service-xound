package com.xound.service;

import com.xound.dto.SetlistReorderItem;
import com.xound.model.SetlistSong;
import com.xound.repository.SetlistSongRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetlistService {

    private final SetlistSongRepository setlistSongRepository;

    public SetlistService(SetlistSongRepository setlistSongRepository) {
        this.setlistSongRepository = setlistSongRepository;
    }

    @Transactional(readOnly = true)
    public List<SetlistSong> getSetlist(Long eventId) {
        return setlistSongRepository.findByEventId(eventId);
    }

    @Transactional
    public void addSong(Long eventId, Long songId) {
        int nextPosition = setlistSongRepository.getNextPosition(eventId);
        setlistSongRepository.addSong(eventId, songId, nextPosition);
    }

    @Transactional
    public void removeSong(Long eventId, Long songId) {
        setlistSongRepository.removeSong(eventId, songId);
    }

    @Transactional
    public void reorder(Long eventId, List<SetlistReorderItem> newOrder) {
        for (SetlistReorderItem item : newOrder) {
            setlistSongRepository.updatePosition(eventId, item.getSongId(), item.getPosition());
        }
    }
}
