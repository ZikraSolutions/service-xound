package com.xound.model.live;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LiveState {
    private LiveStateType type;
    private Long bandId;
    private Long eventId;
    private Integer songIndex;
    private Integer lineIndex;
    private Boolean isPlaying;
}
