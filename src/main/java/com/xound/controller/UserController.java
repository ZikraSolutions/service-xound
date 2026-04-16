package com.xound.controller;

import com.xound.dto.AuthResponse;
import com.xound.dto.ChangeRoleRequest;
import com.xound.dto.UserLoginRequest;
import com.xound.dto.UserRegisterRequest;
import com.xound.dto.UserResponse;
import com.xound.model.User;
import com.xound.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody UserRegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody UserLoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> findAll() {
        List<UserResponse> users = userService.findAll().stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<UserResponse> changeRole(@PathVariable Long id,
                                                    @Valid @RequestBody ChangeRoleRequest request) {
        User updated = userService.changeRole(id, request.getRoleName());
        return ResponseEntity.ok(UserResponse.from(updated));
    }
}
