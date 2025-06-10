package com.ejemplo.tienda_online.controller;

import com.ejemplo.tienda_online.dto.LoginRequest;
import com.ejemplo.tienda_online.dto.RegisterRequest;
import com.ejemplo.tienda_online.dto.JwtResponse;
import com.ejemplo.tienda_online.model.Usuario;
import com.ejemplo.tienda_online.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para la autenticación y registro de usuarios.
 * Maneja las operaciones de login y registro del sistema.
 *
 * @author Sistema Tienda Online
 * @version 2.0
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthController {

    private final AuthService authService;

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param request Datos de registro del usuario
     * @return El usuario registrado
     */
    @PostMapping("/register")
    public ResponseEntity<Usuario> register(@RequestBody RegisterRequest request) {
        log.info("Solicitud de registro para usuario: {}", request.getUsername());

        Usuario usuarioRegistrado = authService.register(request);

        log.info("Usuario {} registrado exitosamente con roles {}",
                 usuarioRegistrado.getUsername(), usuarioRegistrado.getRoles());

        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioRegistrado);
    }

    /**
     * Autentica un usuario en el sistema.
     *
     * @param request Credenciales de login
     * @return Token JWT y datos del usuario
     */
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest request) {
        log.info("Intento de login para usuario: {}", request.getUsername());

        JwtResponse jwtResponse = authService.login(request);

        log.info("Login exitoso para usuario: {} con rol {}",
                 request.getUsername(), jwtResponse.getRole());

        return ResponseEntity.ok(jwtResponse);
    }
}
