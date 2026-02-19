package com.xound.service;

import com.xound.model.Song;
import com.xound.repository.SongRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SongService {

    private final SongRepository songRepository;

    public SongService(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    public List<Song> findAll() {
        return songRepository.findAll();
    }

    public Song findById(Long id) {
        return songRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Canción no encontrada"));
    }

    public List<Song> searchByTitle(String title) {
        return songRepository.searchByTitle(title);
    }

    public void save(Song song) {
        songRepository.save(song);
    }

    public void update(Long id, Song song) {
        songRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Canción no encontrada"));
        song.setId(id);
        songRepository.update(song);
    }

    public void delete(Long id) {
        songRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Canción no encontrada"));

        if (songRepository.isInActiveSetlist(id)) {
            throw new RuntimeException("No se puede eliminar: la canción está en un setlist activo");
        }

        songRepository.changeStatus(id, false);
    }
}
