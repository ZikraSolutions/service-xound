// src/main/java/com/xound/controller/LiveSessionRestController.java
package com.xound.controller;

import com.xound.model.live.LiveState;
import com.xound.service.LiveSessionStore;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/live")
public class LiveSessionRestController {

    private final LiveSessionStore store;

    public LiveSessionRestController(LiveSessionStore store) {
        this.store = store;
    }

    @GetMapping("/{bandId}")
    public ResponseEntity<LiveState> getLiveSession(@PathVariable Long bandId) {
        return store.get(bandId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
}
