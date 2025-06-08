package com.ejemplo.tienda_online.controller;

import com.ejemplo.tienda_online.model.Usuario;
import com.ejemplo.tienda_online.repository.UsuarioRepository;
import com.ejemplo.tienda_online.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador para diagnóstico del sistema
 * IMPORTANTE: Solo disponible en perfil de desarrollo
 */
@RestController
@RequestMapping("/dev/diagnostico")
@RequiredArgsConstructor
public class DiagnosticoController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsServiceImpl userDetailsService;

    @GetMapping("/usuario/{username}")
    public ResponseEntity<?> verificarUsuario(@PathVariable String username) {
        Map<String, Object> response = new HashMap<>();

        // Verificar si el usuario existe en la base de datos
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);

        if (usuarioOpt.isEmpty()) {
            response.put("existe", false);
            response.put("mensaje", "El usuario no existe en la base de datos");
            return ResponseEntity.ok(response);
        }

        Usuario usuario = usuarioOpt.get();
        response.put("existe", true);
        response.put("id", usuario.getId());
        response.put("username", usuario.getUsername());
        response.put("email", usuario.getEmail());
        response.put("passwordLength", usuario.getPassword() != null ? usuario.getPassword().length() : 0);
        response.put("roles", usuario.getRoles());

        // Verificar si el servicio de UserDetails puede cargarlo
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            response.put("userDetailsLoadable", true);
            response.put("authorities", userDetails.getAuthorities());
        } catch (Exception e) {
            response.put("userDetailsLoadable", false);
            response.put("userDetailsError", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/password/verify/{username}/{rawPassword}")
    public ResponseEntity<?> verificarPassword(@PathVariable String username, @PathVariable String rawPassword) {
        Map<String, Object> response = new HashMap<>();

        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        if (usuarioOpt.isEmpty()) {
            response.put("existe", false);
            response.put("mensaje", "Usuario no encontrado");
            return ResponseEntity.ok(response);
        }

        Usuario usuario = usuarioOpt.get();
        boolean passwordMatches = passwordEncoder.matches(rawPassword, usuario.getPassword());

        response.put("passwordMatches", passwordMatches);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/usuarios")
    public ResponseEntity<?> borrarTodosLosUsuarios(@RequestParam(required = true) String claveSeguridad) {
        if (!"borrar-todos-confirmado".equals(claveSeguridad)) {
            return ResponseEntity.badRequest().body("Clave de seguridad incorrecta. Esta operación es peligrosa.");
        }

        // Contar usuarios antes de borrar
        long totalUsuarios = usuarioRepository.count();

        // Borrar todos los usuarios
        usuarioRepository.deleteAll();

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Se han borrado todos los usuarios");
        response.put("totalBorrados", totalUsuarios);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/usuarios/count")
    public ResponseEntity<?> contarUsuarios() {
        long totalUsuarios = usuarioRepository.count();

        Map<String, Object> response = new HashMap<>();
        response.put("totalUsuarios", totalUsuarios);

        // Obtener distribución por roles
        List<Usuario.RolUsuario> roles = List.of(Usuario.RolUsuario.values());
        Map<String, Long> conteoRoles = new HashMap<>();

        for (Usuario.RolUsuario rol : roles) {
            long usuariosConRol = usuarioRepository.countByRoles(rol);
            conteoRoles.put(rol.name(), usuariosConRol);
        }

        response.put("distribucionRoles", conteoRoles);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/usuarios")
    public ResponseEntity<?> listarUsuarios(@RequestParam(required = false) String filtroRol) {
        List<Usuario> usuarios;

        if (filtroRol != null && !filtroRol.isEmpty()) {
            try {
                Usuario.RolUsuario rol = Usuario.RolUsuario.valueOf(filtroRol.toUpperCase());
                usuarios = usuarioRepository.findByRoles(rol.name());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Rol inválido. Valores permitidos: ADMIN, CLIENTE");
            }
        } else {
            usuarios = usuarioRepository.findAll();
        }

        // Sanitizar las contraseñas para no exponerlas
        usuarios.forEach(user -> user.setPassword("[OCULTA]"));

        Map<String, Object> response = new HashMap<>();
        response.put("totalUsuarios", usuarios.size());
        response.put("usuarios", usuarios);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/healthcheck")
    public ResponseEntity<?> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("timestamp", new java.util.Date().toString());
        response.put("message", "El controlador de diagnóstico está funcionando correctamente");

        // Verificar si están disponibles los servicios clave
        response.put("usuarioRepositoryAvailable", usuarioRepository != null);
        response.put("userDetailsServiceAvailable", userDetailsService != null);

        return ResponseEntity.ok(response);
    }
}
