package com.ejemplo.tienda_online.controller;

import com.ejemplo.tienda_online.dto.AdminCreationRequest;
import com.ejemplo.tienda_online.model.Usuario;
import com.ejemplo.tienda_online.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class AdminSetupController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private static boolean SETUP_COMPLETED = false;

    @PostMapping("/setup/admin")
    public ResponseEntity<?> setupAdmin(@RequestBody AdminCreationRequest request) {
        // Solo permitir la creación del primer administrador si no existe ningún usuario administrador
        if (SETUP_COMPLETED || !usuarioRepository.findByRoles(Usuario.RolUsuario.ADMIN.name()).isEmpty()) {
            return ResponseEntity.badRequest().body("La configuración inicial ya se ha completado");
        }

        // Validaciones básicas
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("El nombre de usuario no puede estar vacío");
        }
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            return ResponseEntity.badRequest().body("La contraseña debe tener al menos 6 caracteres");
        }
        if (request.getSetupKey() == null || !request.getSetupKey().equals("clave-secreta-inicial")) {
            return ResponseEntity.badRequest().body("Clave de configuración inválida");
        }

        if (usuarioRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("El nombre de usuario ya existe");
        }

        Usuario admin = Usuario.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Collections.singletonList(Usuario.RolUsuario.ADMIN))
                .build();

        usuarioRepository.save(admin);
        SETUP_COMPLETED = true;

        return ResponseEntity.ok("Usuario administrador creado correctamente");
    }
}
