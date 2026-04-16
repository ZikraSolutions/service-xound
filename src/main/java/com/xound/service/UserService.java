package com.xound.service;

import com.xound.dto.AuthResponse;
import com.xound.dto.UserLoginRequest;
import com.xound.dto.UserRegisterRequest;
import com.xound.dto.UserResponse;
import com.xound.exception.BadRequestException;
import com.xound.exception.ConflictException;
import com.xound.exception.NotFoundException;
import com.xound.exception.UnauthorizedException;
import com.xound.model.User;
import com.xound.repository.RoleRepository;
import com.xound.repository.UserRepository;
import com.xound.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional
    public AuthResponse register(UserRegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new ConflictException("El username ya esta registrado");
        }

        Long musicianRoleId = roleRepository.findByName("MUSICIAN")
                .orElseThrow(() -> new NotFoundException("Rol MUSICIAN no encontrado"))
                .getId();

        User user = new User();
        user.setName(request.getName());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoleId(musicianRoleId);

        userRepository.save(user);

        User saved = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadRequestException("Error al registrar usuario"));

        String token = jwtUtil.generateToken(saved.getId(), saved.getUsername(), saved.getRoleName());
        return new AuthResponse(token, UserResponse.from(saved));
    }

    @Transactional
    public User changeRole(Long userId, String roleName) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        Long roleId = roleRepository.findByName(roleName)
                .orElseThrow(() -> new BadRequestException("Rol no valido: " + roleName))
                .getId();

        userRepository.updateRole(userId, roleId);

        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Error al obtener usuario actualizado"));
    }

    @Transactional(readOnly = true)
    public AuthResponse login(UserLoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Credenciales invalidas"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Credenciales invalidas");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRoleName());
        return new AuthResponse(token, UserResponse.from(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        userRepository.deleteById(id);
    }
}
