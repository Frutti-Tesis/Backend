package com.backend.frutti.DTOs;

import java.time.LocalDate;

import com.backend.frutti.model.Estado;
import com.backend.frutti.model.LugarAnalisis;
import com.backend.frutti.model.Nombre;

import lombok.*;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FrutaDTO {
    private Long id;

    @NotNull
    private Nombre nombre;

    @NotNull
    private Estado estado;

    @NotNull
    private float precio;

    @NotNull
    private float peso;

    @NotNull
    private LugarAnalisis lugarAnalisis;

    @NotNull
    @PastOrPresent
    private LocalDate fechaAnalisis;

    private Long usuarioId;
}
