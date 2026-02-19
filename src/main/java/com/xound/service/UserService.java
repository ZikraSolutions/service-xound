package com.xound.service;

import com.xound.model.User;
import com.xound.repository.RoleRepository;
import com.xound.repository.UserRepository;
import com.xound.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Map<String, Object> register(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("El email ya está registrado");
        }

        if (user.getRoleId() == null) {
            // Por defecto asignar rol MUSICIAN
            roleRepository.findByName("MUSICIAN")
                    .ifPresent(role -> user.setRoleId(role.getId()));
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        User saved = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new RuntimeException("Error al registrar usuario"));

        String token = jwtUtil.generateToken(saved.getId(), saved.getEmail(), saved.getRoleName());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", saved);
        return response;
    }

    public Map<String, Object> login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRoleName());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", user);
        return response;
    }
}
