package com.ejemplo.tienda_online.service;

import com.ejemplo.tienda_online.dto.JwtResponse;
import com.ejemplo.tienda_online.dto.LoginRequest;
import com.ejemplo.tienda_online.dto.RegisterRequest;
import com.ejemplo.tienda_online.model.Usuario;
import com.ejemplo.tienda_online.repository.UsuarioRepository;
import com.ejemplo.tienda_online.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public Usuario register(RegisterRequest request) {
        if (usuarioRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario ya existe");
        }
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El email ya está registrado");
        }
        if (request.getPassword() == null || request.getPassword().length() < 4) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña debe tener al menos 4 caracteres");
        }
        Usuario usuario = Usuario.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Collections.singletonList(Usuario.RolUsuario.CLIENTE))
                .build();
        return usuarioRepository.save(usuario);
    }

    public JwtResponse login(LoginRequest request) {
        try {
            // Primero verificamos si el usuario existe
            Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));

            // Intentamos la autenticación
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Verificar que el usuario tiene al menos un rol
            if (usuario.getRoles() == null || usuario.getRoles().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "El usuario no tiene roles asignados");
            }

            String role = usuario.getRoles().get(0).name();
            String token = jwtUtil.generateToken(usuario.getUsername(), role);

            // Log de depuración
            System.out.println("Login exitoso para: " + usuario.getUsername() + ", con rol: " + role);

            return new JwtResponse(token, usuario.getUsername(), role);
        } catch (ResponseStatusException e) {
            // Re-lanzar excepciones que ya están personalizadas
            throw e;
        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            System.out.println("Error de credenciales para usuario: " + request.getUsername());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Contraseña incorrecta");
        } catch (Exception e) {
            // Para depuración, imprime el tipo de excepción
            System.out.println("Error de autenticación: " + e.getClass().getName() + " - " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Error de autenticación: " + e.getMessage());
        }
    }
}
