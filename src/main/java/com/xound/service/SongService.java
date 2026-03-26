package com.xound.service;

import com.xound.model.Band;
import com.xound.model.Song;
import com.xound.repository.BandRepository;
import com.xound.repository.HiddenSongRepository;
import com.xound.repository.SongRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class SongService {

    private final SongRepository songRepository;
    private final HiddenSongRepository hiddenSongRepository;
    private final BandRepository bandRepository;

    public SongService(SongRepository songRepository, HiddenSongRepository hiddenSongRepository, BandRepository bandRepository) {
        this.songRepository = songRepository;
        this.hiddenSongRepository = hiddenSongRepository;
        this.bandRepository = bandRepository;
    }

    public List<Song> findAll() {
        return songRepository.findAll();
    }

    public List<Song> findAllByUserId(Long userId) {
        return songRepository.findByUserId(userId);
    }

    public List<Song> findByBand(Long userId) {
        // First check if user is an admin with their own band
        Optional<Band> adminBand = bandRepository.findByAdminUserId(userId);
        if (adminBand.isPresent()) {
            return songRepository.findByUserId(userId);
        }
        // Otherwise find the band as a member and get the admin's songs
        Optional<Band> memberBand = bandRepository.findByMemberUserId(userId);
        if (memberBand.isPresent()) {
            return songRepository.findByUserId(memberBand.get().getAdminUserId());
        }
        return Collections.emptyList();
    }

    public Song findById(Long id) {
        return songRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Canción no encontrada"));
    }

    public List<Song> searchByTitle(String title) {
        return songRepository.searchByTitle(title);
    }

    public List<Song> searchByTitleAndUserId(String title, Long userId) {
        return songRepository.searchByTitleAndUserId(title, userId);
    }

    public Song save(Song song) {
        // Verificar si ya existe una canción con el mismo título y artista para este usuario
        Optional<Song> existing = songRepository.findByTitleAndArtistAndUserId(
                song.getTitle(), song.getArtist() != null ? song.getArtist() : "", song.getUserId());

        if (existing.isPresent()) {
            Song found = existing.get();
            // Si el usuario la tiene oculta, des-ocultarla
            if (hiddenSongRepository.isHidden(found.getId(), song.getUserId())) {
                hiddenSongRepository.remove(found.getId(), song.getUserId());
                return found;
            }
            throw new RuntimeException("Esta canción ya se encuentra en tu biblioteca");
        }

        songRepository.save(song);
        return song;
    }

    public void update(Long id, Song song) {
        songRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Canción no encontrada"));
        song.setId(id);
        songRepository.update(song);
    }

    public void updateArtworkUrl(Long id, String artworkUrl) {
        songRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Canción no encontrada"));
        songRepository.updateArtworkUrl(id, artworkUrl);
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
