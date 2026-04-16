package com.xound.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventRequest {

    @NotBlank(message = "El titulo del evento es obligatorio")
    @Size(max = 200, message = "El titulo no puede exceder 200 caracteres")
    private String title;

    @NotNull(message = "La fecha del evento es obligatoria")
    private LocalDateTime eventDate;

    @NotBlank(message = "El lugar del evento es obligatorio")
    @Size(max = 200, message = "El lugar no puede exceder 200 caracteres")
    private String venue;
}
