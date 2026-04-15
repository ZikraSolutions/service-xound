// src/main/java/com/xound/controller/LiveSessionController.java
package com.xound.controller;

import com.xound.model.live.LiveState;
import com.xound.model.live.LiveStateType;
import com.xound.service.LiveSessionStore;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class LiveSessionController {

    private final SimpMessagingTemplate messagingTemplate;
    private final LiveSessionStore store;

    public LiveSessionController(SimpMessagingTemplate messagingTemplate, LiveSessionStore store) {
        this.messagingTemplate = messagingTemplate;
        this.store = store;
    }

    @MessageMapping("/band/{bandId}/live")
    public void handleLiveEvent(@DestinationVariable Long bandId, LiveState state) {
        state.setBandId(bandId);

        if (LiveStateType.LIVE_END.equals(state.getType())) {
            store.remove(bandId);
        } else {
            store.save(bandId, state);
        }

        messagingTemplate.convertAndSend("/topic/band/" + bandId + "/live", state);
    }
}
