// src/main/java/com/xound/service/LiveSessionStore.java
package com.xound.service;

import com.xound.model.live.LiveState;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LiveSessionStore {

    private final ConcurrentHashMap<Long, LiveState> sessions = new ConcurrentHashMap<>();

    public void save(Long bandId, LiveState state) {
        sessions.put(bandId, state);
    }

    public Optional<LiveState> get(Long bandId) {
        return Optional.ofNullable(sessions.get(bandId));
    }

    public void remove(Long bandId) {
        sessions.remove(bandId);
    }
}
