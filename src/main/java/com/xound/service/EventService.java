package com.xound.service;

import com.xound.model.Band;
import com.xound.model.Event;
import com.xound.repository.BandRepository;
import com.xound.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final BandRepository bandRepository;

    public EventService(EventRepository eventRepository, BandRepository bandRepository) {
        this.eventRepository = eventRepository;
        this.bandRepository = bandRepository;
    }

    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    public List<Event> findAllByUserId(Long userId) {
        return eventRepository.findAllByUserId(userId);
    }

    public List<Event> findPublished() {
        return eventRepository.findPublished();
    }

    public List<Event> findPublishedForMusician(Long musicianUserId) {
        Optional<Band> band = bandRepository.findByMemberUserId(musicianUserId);
        if (band.isPresent()) {
            return eventRepository.findPublishedByBandAdmin(band.get().getAdminUserId());
        }
        return List.of();
    }

    public Event findById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));
    }

    public Event findByShareCode(String code) {
        return eventRepository.findByShareCode(code)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado o no publicado"));
    }

    public void save(Event event) {
        // Generar código de compartir único
        event.setShareCode(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        eventRepository.save(event);
    }

    public void update(Long id, Event event) {
        eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));
        event.setId(id);
        eventRepository.update(event);
    }

    public void publish(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));
        eventRepository.publish(id, !event.getPublished());
    }

    public void delete(Long id) {
        eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));
        eventRepository.changeStatus(id, false);
    }
}
