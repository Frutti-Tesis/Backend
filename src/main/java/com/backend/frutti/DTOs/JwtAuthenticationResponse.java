package com.backend.frutti.DTOs;

import lombok.*;

@Data
@NoArgsConstructor
@Builder
public class JwtAuthenticationResponse {
    private String token;
    private String email;
    private String nombre;
    private String rol;

    public JwtAuthenticationResponse(String token, String email, String rol, String nombre) {
        this.token = token;
        this.email = email;
        this.rol = rol;
        this.nombre = nombre;
    }
}