package com.ejemplo.tienda_online.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {
    @Id
    private String id;
    private String username;
    private String email;
    private String password;
    private List<RolUsuario> roles;

    public enum RolUsuario {
        ADMIN,
        CLIENTE
    }
}
