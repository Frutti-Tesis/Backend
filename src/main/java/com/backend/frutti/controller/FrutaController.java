package com.backend.frutti.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.frutti.DTOs.FrutaDTO;
import com.backend.frutti.service.FrutaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/fruta")
public class FrutaController {
    @Autowired
    private FrutaService frutaService;

    @PostMapping("/registrarFruta")
    public ResponseEntity<FrutaDTO> registrarFruta(@RequestBody @Valid FrutaDTO frutaNueva) {
        return ResponseEntity.ok(frutaService.registrarFruta(frutaNueva));
    }

    @GetMapping("/listarFrutas")
    public List<FrutaDTO> listarFrutas() {
        return frutaService.listarMejoresFrutas();
    }

    @DeleteMapping("/eliminar/{idFruta}/{idUsuario}")
    public ResponseEntity<Void> eliminarFruta(@PathVariable Long idFruta, @PathVariable Long idUsuario) {
        frutaService.eliminarFruta(idFruta, idUsuario);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/historial/{id}")
    public List<FrutaDTO> obtenerHistorialFrutas(@PathVariable Long id) {
        return frutaService.obtenerHistorialFrutas(id);
    }

    @GetMapping("/obtenerFruta/{idFruta}, {idUsuario}")
    public ResponseEntity<FrutaDTO> obtenerFruta(@PathVariable Long idFruta, @PathVariable Long idUsuario) {
        FrutaDTO frutaDTO = frutaService.obtenerFruta(idFruta, idUsuario);
        return ResponseEntity.ok(frutaDTO);
    }

    @DeleteMapping("/eliminarHistorial/{idUsuario}")
    public ResponseEntity<Void> eliminarHistorial(@PathVariable Long idUsuario) {
        frutaService.eliminarHistorialUsuario(idUsuario);
        return ResponseEntity.noContent().build();
    }

}
