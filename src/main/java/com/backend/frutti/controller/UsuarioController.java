package com.backend.frutti.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.backend.frutti.DTOs.UsuarioDTO;
import com.backend.frutti.service.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/registrarUsuario")
    public ResponseEntity<UsuarioDTO> registrarUsuario(@RequestBody @Valid UsuarioDTO usuarioNuevo) {
        return ResponseEntity.ok(usuarioService.registrarUsuario(usuarioNuevo));
    }

    @GetMapping("/listarUsuarios")
    public List<UsuarioDTO> listarUsuarios() {
        return usuarioService.listarUsuarios();
    }

    @PatchMapping("/actualizarUsuario/{id}")
    public ResponseEntity<String> actualizarUsuario(@PathVariable Long id,
            @RequestBody @Valid UsuarioDTO usuario) {
        boolean actualizado = usuarioService.actualizarUsuario(id, usuario);
        return actualizado ? ResponseEntity.ok("Usuario actualizado correctamente.")
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/obtenerid/{email}")
    public Long obtenerIdUsuario(@PathVariable String email) {
        return usuarioService.obtenerIdUsuario(email);
    }
    
}
