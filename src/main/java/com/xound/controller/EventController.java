package com.xound.controller;

import com.xound.model.Event;
import com.xound.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public ResponseEntity<List<Event>> findAll() {
        return ResponseEntity.ok(eventService.findAll());
    }

    @GetMapping("/published")
    public ResponseEntity<List<Event>> findPublished() {
        return ResponseEntity.ok(eventService.findPublished());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(eventService.findById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/share/{code}")
    public ResponseEntity<?> findByShareCode(@PathVariable String code) {
        try {
            return ResponseEntity.ok(eventService.findByShareCode(code));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody Event event, Authentication auth) {
        try {
            Long userId = (Long) auth.getCredentials();
            event.setUserId(userId);
            eventService.save(event);
            return ResponseEntity.ok(Map.of("message", "Evento creado exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Event event) {
        try {
            eventService.update(id, event);
            return ResponseEntity.ok(Map.of("message", "Evento actualizado exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/publish")
    public ResponseEntity<?> publish(@PathVariable Long id) {
        try {
            eventService.publish(id);
            return ResponseEntity.ok(Map.of("message", "Estado de publicación actualizado"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            eventService.delete(id);
            return ResponseEntity.ok(Map.of("message", "Evento eliminado exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
