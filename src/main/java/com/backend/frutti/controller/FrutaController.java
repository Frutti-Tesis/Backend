package com.backend.frutti.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.frutti.DTOs.FrutaDTO;
import com.backend.frutti.model.Fruta;
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
    public List<Fruta> listarFrutas() {
        return frutaService.listarFrutas();
    }

    @PatchMapping("/actualizarFruta/{id}")
    public ResponseEntity<String> actualizarFruta(@PathVariable Long id,
            @RequestBody @Valid FrutaDTO fruta) {
        boolean actualizado = frutaService.actualizarFruta(id, fruta);
        return actualizado ? ResponseEntity.ok("Fruta actualizada correctamente.")
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Fruta no encontrado.");
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminarFruta(@PathVariable Long id) {
        frutaService.eliminarFruta(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/historial/{id}")
    public List<FrutaDTO> obtenerHistorialFrutas(@PathVariable Long id) {
        return frutaService.obtenerHistorialFrutas(id);
    }
}
