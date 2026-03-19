package com.xound.controller;

import com.xound.model.AdminInvite;
import com.xound.model.Band;
import com.xound.model.User;
import com.xound.repository.AdminInviteRepository;
import com.xound.repository.BandRepository;
import com.xound.repository.RoleRepository;
import com.xound.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
public class SuperAdminController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AdminInviteRepository adminInviteRepository;
    private final BandRepository bandRepository;

    public SuperAdminController(UserRepository userRepository, RoleRepository roleRepository,
                                AdminInviteRepository adminInviteRepository, BandRepository bandRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.adminInviteRepository = adminInviteRepository;
        this.bandRepository = bandRepository;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, Authentication auth) {
        Long myId = (Long) auth.getCredentials();
        if (myId.equals(id)) {
            return ResponseEntity.badRequest().body(Map.of("error", "No puedes eliminar tu propia cuenta"));
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Usuario eliminado"));
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<?> changeRole(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            String roleName = body.get("roleName");
            if ("SUPER_ADMIN".equals(roleName)) {
                return ResponseEntity.badRequest().body(Map.of("error", "No se puede asignar SUPER_ADMIN"));
            }
            Long roleId = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("Rol no válido"))
                    .getId();
            userRepository.updateRole(id, roleId);
            User updated = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/generate-admin-code")
    public ResponseEntity<?> generateAdminCode() {
        String code = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        adminInviteRepository.save(code);
        return ResponseEntity.ok(Map.of("code", code));
    }

    @GetMapping("/admin-codes")
    public ResponseEntity<List<AdminInvite>> getAdminCodes() {
        return ResponseEntity.ok(adminInviteRepository.findAll());
    }

    @PostMapping("/use-admin-code")
    public ResponseEntity<?> useAdminCode(@RequestBody Map<String, String> body, Authentication auth) {
        try {
            String code = body.get("code");
            if (code == null || code.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Código requerido"));
            }
            AdminInvite invite = adminInviteRepository.findByCode(code.trim().toUpperCase())
                    .orElseThrow(() -> new RuntimeException("Código inválido o ya usado"));

            Long userId = (Long) auth.getCredentials();

            // Verificar que no sea ya admin
            User currentUser = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            if ("ADMIN".equals(currentUser.getRoleName()) || "SUPER_ADMIN".equals(currentUser.getRoleName())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Ya eres administrador"));
            }

            Long adminRoleId = roleRepository.findByName("ADMIN")
                    .orElseThrow(() -> new RuntimeException("Rol ADMIN no encontrado"))
                    .getId();

            userRepository.updateRole(userId, adminRoleId);
            adminInviteRepository.markUsed(invite.getId(), userId);

            // Remove user from any band they were a member of (admins manage their own bands)
            bandRepository.findByMemberUserId(userId).ifPresent(band ->
                    bandRepository.removeMember(band.getId(), userId));

            User updated = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Error"));

            return ResponseEntity.ok(Map.of("message", "Ahora eres administrador", "user", updated));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
