package com.ejemplo.tienda_online;

import com.ejemplo.tienda_online.model.Usuario;
import com.ejemplo.tienda_online.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;

@SpringBootApplication
public class TiendaOnlineApplication {

	public static void main(String[] args) {
		SpringApplication.run(TiendaOnlineApplication.class, args);
	}

	@Bean
	CommandLineRunner initAdmin(UsuarioRepository usuarioRepo, PasswordEncoder encoder) {
		return args -> {
			// Crear usuario admin por defecto si no existe
			if (usuarioRepo.findByUsername("admin").isEmpty()) {
				Usuario admin = Usuario.builder()
					.username("admin")
					.email("admin@tienda.com")
					.password(encoder.encode("admin"))
					.roles(Collections.singletonList(Usuario.RolUsuario.ADMIN))
					.build();
				usuarioRepo.save(admin);
				System.out.println("Usuario admin creado - username: admin, password: admin");
			}

			// Crear usuario cliente de prueba si no existe
			if (usuarioRepo.findByUsername("cliente").isEmpty()) {
				Usuario cliente = Usuario.builder()
					.username("cliente")
					.email("cliente@tienda.com")
					.password(encoder.encode("cliente"))
					.roles(Collections.singletonList(Usuario.RolUsuario.CLIENTE))
					.build();
				usuarioRepo.save(cliente);
				System.out.println("Usuario cliente creado - username: cliente, password: cliente");
			}
		};
	}
}
