package com.xound.service;

import com.xound.dto.EventRequest;
import com.xound.exception.NotFoundException;
import com.xound.model.Band;
import com.xound.model.Event;
import com.xound.repository.BandRepository;
import com.xound.repository.EventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(readOnly = true)
    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Event> findAllByUserId(Long userId) {
        return eventRepository.findAllByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Event> findPublished() {
        return eventRepository.findPublished();
    }

    @Transactional(readOnly = true)
    public List<Event> findPublishedForMusician(Long musicianUserId) {
        Optional<Band> band = bandRepository.findByMemberUserId(musicianUserId);
        if (band.isPresent()) {
            return eventRepository.findPublishedByBandAdmin(band.get().getAdminUserId());
        }
        return List.of();
    }

    @Transactional(readOnly = true)
    public Event findById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Evento no encontrado"));
    }

    @Transactional(readOnly = true)
    public Event findByShareCode(String code) {
        return eventRepository.findByShareCode(code)
                .orElseThrow(() -> new NotFoundException("Evento no encontrado o no publicado"));
    }

    @Transactional
    public void save(EventRequest request, Long userId) {
        Event event = new Event();
        event.setTitle(request.getTitle());
        event.setEventDate(request.getEventDate());
        event.setVenue(request.getVenue());
        event.setUserId(userId);
        event.setShareCode(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        eventRepository.save(event);
    }

    @Transactional
    public void update(Long id, EventRequest request) {
        Event existing = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Evento no encontrado"));
        existing.setTitle(request.getTitle());
        existing.setEventDate(request.getEventDate());
        existing.setVenue(request.getVenue());
        eventRepository.update(existing);
    }

    @Transactional
    public void publish(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Evento no encontrado"));
        eventRepository.publish(id, !event.getPublished());
    }

    @Transactional
    public void delete(Long id) {
        eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Evento no encontrado"));
        eventRepository.changeStatus(id, false);
    }
}
