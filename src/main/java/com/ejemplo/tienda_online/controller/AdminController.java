package com.ejemplo.tienda_online.controller;

import com.ejemplo.tienda_online.model.Usuario;
import com.ejemplo.tienda_online.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AdminController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Usuario>> getAllUsers() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        // No devolver las contraseñas
        usuarios.forEach(u -> u.setPassword("***"));
        return ResponseEntity.ok(usuarios);
    }

    @DeleteMapping("/cleanup")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> cleanupUsers() {
        // Eliminar todos los usuarios excepto admin
        List<Usuario> usuarios = usuarioRepository.findAll();
        long eliminados = 0;

        for (Usuario usuario : usuarios) {
            if (!"admin".equals(usuario.getUsername())) {
                usuarioRepository.delete(usuario);
                eliminados++;
            }
        }

        return ResponseEntity.ok("Eliminados " + eliminados + " usuarios. Usuario admin conservado.");
    }

    @PostMapping("/reset-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> resetAdmin() {
        // Buscar y eliminar admin existente
        usuarioRepository.findByUsername("admin").ifPresent(usuarioRepository::delete);

        // Crear nuevo admin
        Usuario admin = Usuario.builder()
            .username("admin")
            .email("admin@tienda.com")
            .password(passwordEncoder.encode("admin"))
            .roles(Collections.singletonList(Usuario.RolUsuario.ADMIN))
            .build();
        usuarioRepository.save(admin);

        return ResponseEntity.ok("Usuario admin restablecido con credenciales: admin/admin");
    }

    @PostMapping("/init-users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> initTestUsers() {
        // Crear usuario cliente de prueba si no existe
        if (usuarioRepository.findByUsername("cliente").isEmpty()) {
            Usuario cliente = Usuario.builder()
                .username("cliente")
                .email("cliente@tienda.com")
                .password(passwordEncoder.encode("cliente"))
                .roles(Collections.singletonList(Usuario.RolUsuario.CLIENTE))
                .build();
            usuarioRepository.save(cliente);
        }

        return ResponseEntity.ok("Usuarios de prueba inicializados: admin/admin, cliente/cliente");
    }
}
