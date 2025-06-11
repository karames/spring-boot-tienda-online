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

@RestController
@RequiredArgsConstructor
public class AdminSetupController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private static final String SETUP_KEY = "clave-secreta-inicial";
    private static final String MSG_SETUP_COMPLETADO = "La configuración inicial ya se ha completado";
    private static final String MSG_USUARIO_EXISTE = "El nombre de usuario ya existe";
    private static final String MSG_USUARIO_VACIO = "El nombre de usuario no puede estar vacío";
    private static final String MSG_PASSWORD_CORTA = "La contraseña debe tener al menos 6 caracteres";
    private static final String MSG_CLAVE_INVALIDA = "Clave de configuración inválida";
    private static boolean SETUP_COMPLETED = false;

    @PostMapping("/setup/admin")
    public ResponseEntity<?> setupAdmin(@RequestBody AdminCreationRequest request) {
        // Solo permitir la creación del primer administrador si no existe ningún usuario administrador
        if (SETUP_COMPLETED || !usuarioRepository.findByRoles(Usuario.RolUsuario.ADMIN.name()).isEmpty()) {
            return ResponseEntity.badRequest().body(MSG_SETUP_COMPLETADO);
        }
        // Validaciones básicas
        if (isNullOrEmpty(request.getUsername())) {
            return ResponseEntity.badRequest().body(MSG_USUARIO_VACIO);
        }
        if (isNullOrEmpty(request.getPassword()) || request.getPassword().length() < 6) {
            return ResponseEntity.badRequest().body(MSG_PASSWORD_CORTA);
        }
        if (isNullOrEmpty(request.getSetupKey()) || !SETUP_KEY.equals(request.getSetupKey())) {
            return ResponseEntity.badRequest().body(MSG_CLAVE_INVALIDA);
        }
        if (usuarioRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body(MSG_USUARIO_EXISTE);
        }
        // Crear y guardar admin
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

    private boolean isNullOrEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }
}
