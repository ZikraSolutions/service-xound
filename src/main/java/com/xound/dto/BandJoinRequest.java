package com.xound.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BandJoinRequest {

    @NotBlank(message = "El codigo de invitacion es obligatorio")
    private String inviteCode;
}
