package com.ejemplo.tienda_online.controller;

import com.ejemplo.tienda_online.dto.LoginRequest;
import com.ejemplo.tienda_online.dto.RegisterRequest;
import com.ejemplo.tienda_online.dto.JwtResponse;
import com.ejemplo.tienda_online.model.Usuario;
import com.ejemplo.tienda_online.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Usuario> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
