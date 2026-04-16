package com.xound.dto;

import com.xound.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Respuesta segura de usuario: nunca expone el password al cliente.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String name;
    private String username;
    private Long roleId;
    private String roleName;
    private Boolean status;
    private LocalDateTime createdAt;

    public static UserResponse from(User user) {
        if (user == null) return null;
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getUsername(),
                user.getRoleId(),
                user.getRoleName(),
                user.getStatus(),
                user.getCreatedAt()
        );
    }
}
