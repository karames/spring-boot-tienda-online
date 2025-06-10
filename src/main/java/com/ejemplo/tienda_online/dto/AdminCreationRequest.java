package com.ejemplo.tienda_online.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO para solicitudes de creación de administradores.
 *
 * <p>Esta clase contiene los campos necesarios para crear
 * un nuevo usuario con rol de administrador en el sistema.</p>
 *
 * @author Sistema Tienda Online
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminCreationRequest {

    /**
     * Nombre de usuario único para el administrador.
     */
    private String username;

    /**
     * Dirección de correo electrónico del administrador.
     */
    private String email;

    /**
     * Contraseña para el administrador.
     */
    private String password;

    /**
     * Clave de configuración necesaria para crear administradores.
     */
    private String setupKey;
}
