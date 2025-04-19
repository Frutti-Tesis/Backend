package com.backend.frutti.model;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Table(name = "Frutas")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Fruta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private Estado estado;
    private float precio;
    private String lugarAnalisis;
    private Date fechaAnalisis;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    @JsonIgnore
    private Usuario usuario;
}