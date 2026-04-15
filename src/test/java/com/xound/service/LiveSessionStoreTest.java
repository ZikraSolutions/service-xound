// src/test/java/com/xound/service/LiveSessionStoreTest.java
package com.xound.service;

import com.xound.model.live.LiveState;
import com.xound.model.live.LiveStateType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class LiveSessionStoreTest {

    private LiveSessionStore store;

    @BeforeEach
    void setUp() {
        store = new LiveSessionStore();
    }

    @Test
    void save_and_get_returns_state() {
        LiveState state = new LiveState(LiveStateType.LIVE_STATE, 1L, 10L, 2, 5, true);
        store.save(1L, state);
        Optional<LiveState> result = store.get(1L);
        assertTrue(result.isPresent());
        assertEquals(2, result.get().getSongIndex());
        assertEquals(5, result.get().getLineIndex());
    }

    @Test
    void get_missing_bandId_returns_empty() {
        Optional<LiveState> result = store.get(99L);
        assertFalse(result.isPresent());
    }

    @Test
    void remove_deletes_state() {
        LiveState state = new LiveState(LiveStateType.LIVE_STATE, 1L, 10L, 0, 0, true);
        store.save(1L, state);
        store.remove(1L);
        assertFalse(store.get(1L).isPresent());
    }

    @Test
    void save_overwrites_existing_state() {
        store.save(1L, new LiveState(LiveStateType.LIVE_STATE, 1L, 10L, 0, 0, true));
        store.save(1L, new LiveState(LiveStateType.LIVE_STATE, 1L, 10L, 3, 7, false));
        Optional<LiveState> result = store.get(1L);
        assertTrue(result.isPresent());
        assertEquals(3, result.get().getSongIndex());
        assertEquals(7, result.get().getLineIndex());
    }
}
