package com.backend.frutti.DTOs;

import java.sql.Date;

import lombok.*;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioDTO {
    private Long id;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String nombre;

    @NotNull
    private int edad;

    @NotNull
    @Past
    private Date fechaNacimiento;
}
