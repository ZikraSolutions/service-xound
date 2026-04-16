package com.xound.service;

import com.xound.dto.SongRequest;
import com.xound.exception.ConflictException;
import com.xound.exception.NotFoundException;
import com.xound.model.Band;
import com.xound.model.Song;
import com.xound.repository.BandRepository;
import com.xound.repository.HiddenSongRepository;
import com.xound.repository.SongRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(readOnly = true)
    public List<Song> findAll() {
        return songRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Song> findAllByUserId(Long userId) {
        return songRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Song> findByBand(Long userId) {
        Optional<Band> adminBand = bandRepository.findByAdminUserId(userId);
        if (adminBand.isPresent()) {
            return songRepository.findByUserId(userId);
        }
        Optional<Band> memberBand = bandRepository.findByMemberUserId(userId);
        if (memberBand.isPresent()) {
            return songRepository.findByUserId(memberBand.get().getAdminUserId());
        }
        return Collections.emptyList();
    }

    @Transactional(readOnly = true)
    public Song findById(Long id) {
        return songRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cancion no encontrada"));
    }

    @Transactional(readOnly = true)
    public List<Song> searchByTitle(String title) {
        return songRepository.searchByTitle(title);
    }

    @Transactional(readOnly = true)
    public List<Song> searchByTitleAndUserId(String title, Long userId) {
        return songRepository.searchByTitleAndUserId(title, userId);
    }

    @Transactional
    public Song save(SongRequest request, Long userId) {
        Song song = new Song();
        song.setTitle(request.getTitle());
        song.setArtist(request.getArtist());
        song.setTone(request.getTone());
        song.setContent(request.getContent());
        song.setLyrics(request.getLyrics());
        song.setNotes(request.getNotes());
        song.setBpm(request.getBpm());
        song.setTimeSignature(request.getTimeSignature());
        song.setArtworkUrl(request.getArtworkUrl());
        song.setUserId(userId);

        Optional<Song> existing = songRepository.findByTitleAndArtistAndUserId(
                song.getTitle(), song.getArtist() != null ? song.getArtist() : "", userId);

        if (existing.isPresent()) {
            Song found = existing.get();
            if (hiddenSongRepository.isHidden(found.getId(), userId)) {
                hiddenSongRepository.remove(found.getId(), userId);
                return found;
            }
            throw new ConflictException("Esta cancion ya se encuentra en tu biblioteca");
        }

        songRepository.save(song);
        return song;
    }

    @Transactional
    public void update(Long id, SongRequest request) {
        Song existing = songRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cancion no encontrada"));
        existing.setTitle(request.getTitle());
        existing.setArtist(request.getArtist());
        existing.setTone(request.getTone());
        existing.setContent(request.getContent());
        existing.setLyrics(request.getLyrics());
        existing.setNotes(request.getNotes());
        existing.setBpm(request.getBpm());
        existing.setTimeSignature(request.getTimeSignature());
        if (request.getArtworkUrl() != null) {
            existing.setArtworkUrl(request.getArtworkUrl());
        }
        songRepository.update(existing);
    }

    @Transactional
    public void updateArtworkUrl(Long id, String artworkUrl) {
        songRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cancion no encontrada"));
        songRepository.updateArtworkUrl(id, artworkUrl);
    }

    @Transactional
    public void delete(Long id) {
        songRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cancion no encontrada"));

        if (songRepository.isInActiveSetlist(id)) {
            throw new ConflictException("No se puede eliminar: la cancion esta en un setlist activo");
        }

        songRepository.changeStatus(id, false);
    }
}
