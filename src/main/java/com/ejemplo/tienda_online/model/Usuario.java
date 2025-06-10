package com.ejemplo.tienda_online.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

/**
 * Entidad que representa un usuario del sistema.
 * Contiene información de autenticación y autorización.
 *
 * @author Sistema Tienda Online
 * @version 2.0
 */
@Document(collection = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    @Indexed(unique = true)
    private String email;

    private String password;

    private List<RolUsuario> roles;

    @Builder.Default
    private Instant fechaCreacion = Instant.now();

    @Builder.Default
    private boolean activo = true;

    /**
     * Roles disponibles en el sistema.
     */
    public enum RolUsuario {
        ADMIN("Administrador"),
        CLIENTE("Cliente");

        private final String descripcion;

        RolUsuario(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }

    /**
     * Verifica si el usuario tiene un rol específico.
     *
     * @param rol Rol a verificar
     * @return true si el usuario tiene el rol
     */
    public boolean hasRole(RolUsuario rol) {
        return roles != null && roles.contains(rol);
    }

    /**
     * Verifica si el usuario es administrador.
     *
     * @return true si el usuario tiene rol ADMIN
     */
    public boolean isAdmin() {
        return hasRole(RolUsuario.ADMIN);
    }

    /**
     * Verifica si el usuario es cliente.
     *
     * @return true si el usuario tiene rol CLIENTE
     */
    public boolean isCliente() {
        return hasRole(RolUsuario.CLIENTE);
    }

    /**
     * Obtiene el rol principal del usuario.
     *
     * @return El primer rol del usuario o null si no tiene roles
     */
    public RolUsuario getRolPrincipal() {
        return roles != null && !roles.isEmpty() ? roles.get(0) : null;
    }
}
