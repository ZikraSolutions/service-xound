package com.xound.service;

import com.xound.model.Event;
import com.xound.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    public List<Event> findPublished() {
        return eventRepository.findPublished();
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
