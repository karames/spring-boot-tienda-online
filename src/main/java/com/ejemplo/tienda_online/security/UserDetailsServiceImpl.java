package com.ejemplo.tienda_online.security;

import com.ejemplo.tienda_online.model.Usuario;
import com.ejemplo.tienda_online.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            Usuario usuario = usuarioRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

            // Verificar que la contraseña no sea nula
            if (usuario.getPassword() == null) {
                throw new UsernameNotFoundException("La contraseña del usuario no está configurada para: " + username);
            }

            // Verificar que el usuario tiene roles
            if (usuario.getRoles() == null || usuario.getRoles().isEmpty()) {
                System.out.println("ADVERTENCIA: Usuario sin roles: " + username);
                // Asignar un rol predeterminado para evitar errores
                return User.builder()
                        .username(usuario.getUsername())
                        .password(usuario.getPassword())
                        .roles("USER")
                        .build();
            }

            String[] roles = usuario.getRoles().stream()
                    .map(Enum::name)
                    .toArray(String[]::new);

            System.out.println("Usuario cargado: " + username + " con roles: " + String.join(", ", roles));

            return User.builder()
                    .username(usuario.getUsername())
                    .password(usuario.getPassword())
                    .roles(roles)
                    .build();
        } catch (Exception e) {
            System.out.println("Error al cargar usuario: " + e.getMessage());
            throw new UsernameNotFoundException("Error al cargar usuario: " + username, e);
        }
    }
}
