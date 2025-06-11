package com.ejemplo.tienda_online.util;

import com.ejemplo.tienda_online.model.Usuario;
import com.ejemplo.tienda_online.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * Utilidad para crear usuarios desde la línea de comandos.
 * Para utilizarla, crea un perfil específico en application.properties:
 *
 * spring.profiles.active=admin-creator
 *
 * Y luego ejecuta la aplicación con ese perfil:
 * mvnw spring-boot:run -Dspring.profiles.active=admin-creator
 */
@Component
@RequiredArgsConstructor
public class AdminCreatorUtil implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Solo se ejecuta si se activa el perfil admin-creator
        if (!List.of(args).contains("--admin-creator")) {
            return;
        }

        // No cerramos el Scanner para evitar cerrar System.in y posibles problemas en la JVM.
        final Scanner scanner = new Scanner(System.in); // warning aceptado: el proceso termina con System.exit(0)
        System.out.println("\n=== CREACIÓN DE USUARIO ADMINISTRADOR ===");

        System.out.print("Ingrese nombre de usuario: ");
        String username = scanner.nextLine();

        if (usuarioRepository.findByUsername(username).isPresent()) {
            System.out.println("Error: El usuario ya existe");
            return;
        }

        System.out.print("Ingrese email: ");
        String email = scanner.nextLine();

        if (usuarioRepository.findByEmail(email).isPresent()) {
            System.out.println("Error: El email ya está registrado");
            return;
        }

        System.out.print("Ingrese contraseña: ");
        String password = scanner.nextLine();

        if (password.length() < 4) {
            System.out.println("Error: La contraseña debe tener al menos 4 caracteres");
            return;
        }

        Usuario admin = Usuario.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .roles(Collections.singletonList(Usuario.RolUsuario.ADMIN))
                .build();

        usuarioRepository.save(admin);
        System.out.println("\n¡Usuario administrador creado con éxito!");
        System.out.println("Username: " + username);
        System.out.println("Rol: ADMIN");

        // Salir después de crear el usuario
        System.exit(0);
    }
}
