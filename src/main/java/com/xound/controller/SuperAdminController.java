package com.xound.controller;

import com.xound.dto.AdminCodeRequest;
import com.xound.dto.ChangeRoleRequest;
import com.xound.dto.UserResponse;
import com.xound.exception.BadRequestException;
import com.xound.exception.ConflictException;
import com.xound.exception.NotFoundException;
import com.xound.model.AdminInvite;
import com.xound.model.User;
import com.xound.repository.AdminInviteRepository;
import com.xound.repository.BandRepository;
import com.xound.repository.RoleRepository;
import com.xound.repository.UserRepository;
import com.xound.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class SuperAdminController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AdminInviteRepository adminInviteRepository;
    private final BandRepository bandRepository;
    private final UserService userService;

    public SuperAdminController(UserRepository userRepository, RoleRepository roleRepository,
                                AdminInviteRepository adminInviteRepository, BandRepository bandRepository,
                                UserService userService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.adminInviteRepository = adminInviteRepository;
        this.bandRepository = bandRepository;
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userRepository.findAll().stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id, Authentication auth) {
        Long myId = (Long) auth.getCredentials();
        if (myId.equals(id)) {
            throw new BadRequestException("No puedes eliminar tu propia cuenta");
        }
        userService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "Usuario eliminado"));
    }

    @PutMapping("/users/{id}/role")
    @Transactional
    public ResponseEntity<UserResponse> changeRole(@PathVariable Long id,
                                                    @Valid @RequestBody ChangeRoleRequest request) {
        String roleName = request.getRoleName();
        if ("SUPER_ADMIN".equals(roleName)) {
            throw new BadRequestException("No se puede asignar SUPER_ADMIN");
        }
        Long roleId = roleRepository.findByName(roleName)
                .orElseThrow(() -> new BadRequestException("Rol no valido"))
                .getId();
        userRepository.updateRole(id, roleId);
        User updated = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        return ResponseEntity.ok(UserResponse.from(updated));
    }

    @PostMapping("/generate-admin-code")
    public ResponseEntity<Map<String, String>> generateAdminCode() {
        String code = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        adminInviteRepository.save(code);
        return ResponseEntity.ok(Map.of("code", code));
    }

    @GetMapping("/admin-codes")
    public ResponseEntity<List<AdminInvite>> getAdminCodes() {
        return ResponseEntity.ok(adminInviteRepository.findAll());
    }

    @PostMapping("/use-admin-code")
    @Transactional
    public ResponseEntity<Map<String, Object>> useAdminCode(@Valid @RequestBody AdminCodeRequest request,
                                                             Authentication auth) {
        String code = request.getCode().trim().toUpperCase();
        AdminInvite invite = adminInviteRepository.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Codigo invalido o ya usado"));

        Long userId = (Long) auth.getCredentials();

        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        if ("ADMIN".equals(currentUser.getRoleName()) || "SUPER_ADMIN".equals(currentUser.getRoleName())) {
            throw new ConflictException("Ya eres administrador");
        }

        Long adminRoleId = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new NotFoundException("Rol ADMIN no encontrado"))
                .getId();

        userRepository.updateRole(userId, adminRoleId);
        adminInviteRepository.markUsed(invite.getId(), userId);

        bandRepository.findByMemberUserId(userId).ifPresent(band ->
                bandRepository.removeMember(band.getId(), userId));

        User updated = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Error al obtener usuario actualizado"));

        return ResponseEntity.ok(Map.of(
                "message", "Ahora eres administrador",
                "user", UserResponse.from(updated)
        ));
    }
}
