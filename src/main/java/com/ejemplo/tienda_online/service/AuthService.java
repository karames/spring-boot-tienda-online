package com.ejemplo.tienda_online.service;

import com.ejemplo.tienda_online.dto.JwtResponse;
import com.ejemplo.tienda_online.dto.LoginRequest;
import com.ejemplo.tienda_online.dto.RegisterRequest;
import com.ejemplo.tienda_online.exception.BusinessException;
import com.ejemplo.tienda_online.model.Usuario;
import com.ejemplo.tienda_online.repository.UsuarioRepository;
import com.ejemplo.tienda_online.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;

/**
 * Servicio de autenticación que maneja el registro y login de usuarios
 * Proporciona funcionalidades seguras para la gestión de autenticación
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final String USERNAME_REGEX = "^[a-zA-Z0-9._-]{3,20}$";
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";

    /**
     * Registra un nuevo usuario en el sistema
     * @param request datos del usuario a registrar
     * @return usuario creado
     * @throws BusinessException si los datos son inválidos o el usuario ya existe
     */
    public Usuario register(RegisterRequest request) {
        log.info("Iniciando registro de usuario: {}", request.getUsername());

        validateRegisterRequest(request);
        checkUserExistence(request);

        Usuario usuario = buildNewUser(request);
        Usuario savedUser = usuarioRepository.save(usuario);

        log.info("Usuario registrado exitosamente: {}", savedUser.getUsername());
        return savedUser;
    }

    /**
     * Autentica un usuario y genera un token JWT
     * @param request credenciales del usuario
     * @return respuesta con token JWT y información del usuario
     * @throws BadCredentialsException si las credenciales son inválidas
     */
    public JwtResponse login(LoginRequest request) {
        log.info("Iniciando login para usuario: {}", request.getUsername());

        validateLoginRequest(request);

        Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));

        authenticateUser(request);

        validateUserRoles(usuario);

        String role = usuario.getRoles().get(0).name();
        String token = jwtUtil.generateToken(usuario.getUsername(), role);

        log.info("Login exitoso para usuario: {} con rol: {}", usuario.getUsername(), role);

        return JwtResponse.builder()
                .token(token)
                .username(usuario.getUsername())
                .role(role)
                .build();
    }

    private void validateRegisterRequest(RegisterRequest request) {
        if (!StringUtils.hasText(request.getUsername())) {
            throw new BusinessException("El nombre de usuario es obligatorio");
        }

        if (!request.getUsername().matches(USERNAME_REGEX)) {
            throw new BusinessException("El nombre de usuario debe tener entre 3-20 caracteres alfanuméricos");
        }

        if (!StringUtils.hasText(request.getEmail())) {
            throw new BusinessException("El email es obligatorio");
        }

        if (!request.getEmail().matches(EMAIL_REGEX)) {
            throw new BusinessException("El formato del email es inválido");
        }

        if (!StringUtils.hasText(request.getPassword()) || request.getPassword().length() < MIN_PASSWORD_LENGTH) {
            throw new BusinessException("La contraseña debe tener al menos " + MIN_PASSWORD_LENGTH + " caracteres");
        }
    }

    private void checkUserExistence(RegisterRequest request) {
        if (usuarioRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new BusinessException("El nombre de usuario ya está en uso");
        }

        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BusinessException("El email ya está registrado");
        }
    }

    private Usuario buildNewUser(RegisterRequest request) {
        return Usuario.builder()
                .username(request.getUsername().trim().toLowerCase())
                .email(request.getEmail().trim().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Collections.singletonList(Usuario.RolUsuario.CLIENTE))
                .build();
    }

    private void validateLoginRequest(LoginRequest request) {
        if (!StringUtils.hasText(request.getUsername()) || !StringUtils.hasText(request.getPassword())) {
            throw new BadCredentialsException("Usuario y contraseña son obligatorios");
        }
    }

    private void authenticateUser(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername().trim().toLowerCase(),
                            request.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (BadCredentialsException e) {
            log.warn("Intento de login fallido para usuario: {}", request.getUsername());
            throw new BadCredentialsException("Credenciales inválidas");
        }    }

    private void validateUserRoles(Usuario usuario) {
        if (usuario.getRoles() == null || usuario.getRoles().isEmpty()) {
            log.error("Usuario sin roles: {}", usuario.getUsername());
            throw new BusinessException("Usuario sin roles asignados");
        }
    }
}
