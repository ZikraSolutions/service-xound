package com.xound.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetlistAddSongRequest {

    @NotNull(message = "El id de la cancion es obligatorio")
    private Long songId;
}
