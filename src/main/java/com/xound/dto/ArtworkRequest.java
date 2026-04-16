package com.xound.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtworkRequest {

    @NotBlank(message = "La URL de la caratula es obligatoria")
    private String artworkUrl;
}
