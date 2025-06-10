package com.ejemplo.tienda_online.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para las solicitudes de registro de nuevos usuarios.
 *
 * Este DTO contiene toda la información necesaria para crear una nueva
 * cuenta de usuario en el sistema, incluyendo validaciones de formato
 * y seguridad.
 *
 * @author Sistema Tienda Online
 * @version 1.0
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    /**
     * Nombre de usuario único para el nuevo usuario.
     * Debe ser único en el sistema y seguir un formato específico.
     */
    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    @Size(min = 3, max = 20, message = "El nombre de usuario debe tener entre 3 y 20 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$",
             message = "El nombre de usuario solo puede contener letras, números, puntos, guiones y guiones bajos")
    private String username;

    /**
     * Dirección de correo electrónico del usuario.
     * Debe ser un email válido y único en el sistema.
     */
    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "Debe proporcionar un email válido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    private String email;

    /**
     * Contraseña para la nueva cuenta.
     * Debe cumplir con los requisitos mínimos de seguridad.
     */
    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 8, max = 100, message = "La contraseña debe tener entre 8 y 100 caracteres")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
             message = "La contraseña debe contener al menos una letra minúscula, una mayúscula y un número")
    private String password;
}
