package com.ejemplo.tienda_online.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

/**
 * DTO para las respuestas de autenticación JWT.
 *
 * Este DTO encapsula toda la información que se devuelve al cliente
 * después de una autenticación exitosa, incluyendo el token JWT
 * y la información básica del usuario.
 *
 * @author Sistema Tienda Online
 * @version 1.0
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtResponse {

    /**
     * Token JWT generado para el usuario autenticado.
     * Este token debe ser incluido en las cabeceras de autorización
     * para las siguientes peticiones autenticadas.
     */
    private String token;

    /**
     * Tipo de token (generalmente "Bearer").
     * Indica el esquema de autenticación que se debe usar.
     */
    @Builder.Default
    private String type = "Bearer";

    /**
     * Nombre de usuario del usuario autenticado.
     */
    private String username;

    /**
     * Email del usuario autenticado.
     */
    private String email;

    /**
     * Rol del usuario en el sistema.
     * Determina los permisos y accesos disponibles.
     */
    private String role;

    /**
     * Fecha y hora de cuando se generó el token.
     */
    @Builder.Default
    private LocalDateTime issuedAt = LocalDateTime.now();

    /**
     * Fecha y hora de expiración del token.
     * Después de esta fecha, el token ya no será válido.
     */
    private LocalDateTime expiresAt;

    /**
     * Constructor simplificado para mantener compatibilidad con versiones anteriores.
     *
     * @param token Token JWT
     * @param username Nombre de usuario
     * @param role Rol del usuario
     * @deprecated Use el builder o el constructor completo en su lugar
     */
    @Deprecated
    public JwtResponse(String token, String username, String role) {
        this.token = token;
        this.username = username;
        this.role = role;
        this.type = "Bearer";
        this.issuedAt = LocalDateTime.now();
    }

    /**
     * Verifica si el token está próximo a expirar (dentro de 5 minutos).
     *
     * @return true si el token expira en menos de 5 minutos
     */
    public boolean isNearExpiration() {
        if (expiresAt == null) {
            return false;
        }
        return LocalDateTime.now().plusMinutes(5).isAfter(expiresAt);
    }

    /**
     * Verifica si el token ya ha expirado.
     *
     * @return true si el token ha expirado
     */
    public boolean isExpired() {
        if (expiresAt == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(expiresAt);
    }
}
