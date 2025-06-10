package com.ejemplo.tienda_online.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para las solicitudes de inicio de sesión.
 *
 * Este DTO encapsula las credenciales necesarias para que un usuario
 * pueda autenticarse en el sistema.
 *
 * @author Sistema Tienda Online
 * @version 1.0
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    /**
     * Nombre de usuario o email del usuario que intenta autenticarse.
     * No puede estar vacío y debe tener al menos 3 caracteres.
     */
    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
    private String username;

    /**
     * Contraseña del usuario.
     * No puede estar vacía y debe tener al menos 6 caracteres por seguridad.
     */
    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;
}
