package com.xound.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetlistReorderItem {

    @NotNull(message = "El id de la cancion es obligatorio")
    private Long songId;

    @NotNull(message = "La posicion es obligatoria")
    private Integer position;
}
