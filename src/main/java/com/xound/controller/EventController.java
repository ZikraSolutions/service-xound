package com.xound.controller;

import com.xound.dto.EventRequest;
import com.xound.model.Event;
import com.xound.service.EventService;
import jakarta.validation.Valid;
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
    public ResponseEntity<List<Event>> findAll(Authentication auth) {
        Long userId = (Long) auth.getCredentials();
        return ResponseEntity.ok(eventService.findAllByUserId(userId));
    }

    @GetMapping("/published")
    public ResponseEntity<List<Event>> findPublished(Authentication auth) {
        Long userId = (Long) auth.getCredentials();
        return ResponseEntity.ok(eventService.findPublishedForMusician(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> findById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.findById(id));
    }

    @GetMapping("/share/{code}")
    public ResponseEntity<Event> findByShareCode(@PathVariable String code) {
        return ResponseEntity.ok(eventService.findByShareCode(code));
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> save(@Valid @RequestBody EventRequest request,
                                                     Authentication auth) {
        Long userId = (Long) auth.getCredentials();
        eventService.save(request, userId);
        return ResponseEntity.ok(Map.of("message", "Evento creado exitosamente"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> update(@PathVariable Long id,
                                                       @Valid @RequestBody EventRequest request) {
        eventService.update(id, request);
        return ResponseEntity.ok(Map.of("message", "Evento actualizado exitosamente"));
    }

    @PutMapping("/{id}/publish")
    public ResponseEntity<Map<String, String>> publish(@PathVariable Long id) {
        eventService.publish(id);
        return ResponseEntity.ok(Map.of("message", "Estado de publicacion actualizado"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        eventService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Evento eliminado exitosamente"));
    }
}
