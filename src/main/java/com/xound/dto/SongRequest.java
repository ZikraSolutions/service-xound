package com.xound.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SongRequest {

    @NotBlank(message = "El titulo es obligatorio")
    @Size(max = 200, message = "El titulo no puede exceder 200 caracteres")
    private String title;

    @Size(max = 200, message = "El artista no puede exceder 200 caracteres")
    private String artist;

    @Size(max = 10, message = "La tonalidad no puede exceder 10 caracteres")
    private String tone;

    private String content;
    private String lyrics;
    private String notes;

    @Min(value = 0, message = "El BPM no puede ser negativo")
    private Integer bpm;

    @Size(max = 20, message = "La signatura de tiempo no puede exceder 20 caracteres")
    private String timeSignature;

    private String artworkUrl;
}
