package com.backend.frutti.DTOs;

import java.sql.Date;

import com.backend.frutti.model.Estado;

import lombok.*;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FrutaDTO {
    private Long id;

    @NotBlank
    private String nombre;

    @NotNull
    private Estado estado;

    @NotNull
    private float precio;

    @NotBlank
    private String lugarAnalisis;

    @NotNull
    @PastOrPresent
    private Date fechaAnalisis;

    private Long usuarioId;
}
