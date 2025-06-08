package com.ejemplo.tienda_online.dto;

import lombok.Data;

@Data
public class AdminCreationRequest {
    private String username;
    private String email;
    private String password;
    private String setupKey;
}
