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

    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9]+$";

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
        if (user.getUsername() == null || !user.getUsername().matches(USERNAME_PATTERN)) {
            throw new RuntimeException("El username solo puede contener letras y numeros, sin espacios ni caracteres especiales");
        }

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("El username ya esta registrado");
        }

        // Siempre asignar MUSICIAN al registrarse
        roleRepository.findByName("MUSICIAN")
                .ifPresent(role -> user.setRoleId(role.getId()));

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        User saved = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new RuntimeException("Error al registrar usuario"));

        String token = jwtUtil.generateToken(saved.getId(), saved.getUsername(), saved.getRoleName());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", saved);
        return response;
    }

    public User changeRole(Long userId, String roleName) {
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Long roleId = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Rol no valido: " + roleName))
                .getId();

        userRepository.updateRole(userId, roleId);

        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Error al obtener usuario actualizado"));
    }

    public Map<String, Object> login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Credenciales invalidas"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Credenciales invalidas");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRoleName());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", user);
        return response;
    }
}
