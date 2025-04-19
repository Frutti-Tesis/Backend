package com.backend.frutti.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.frutti.DTOs.FrutaDTO;
import com.backend.frutti.model.Fruta;
import com.backend.frutti.model.Usuario;
import com.backend.frutti.repository.FrutaRepository;
import com.backend.frutti.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
public class FrutaService {
    @Autowired
    private FrutaRepository frutaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public FrutaDTO registrarFruta(FrutaDTO dto) {

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Fruta fruta = Fruta.builder()
                .nombre(dto.getNombre())
                .estado(dto.getEstado())
                .precio(dto.getPrecio())
                .lugarAnalisis(dto.getLugarAnalisis())
                .fechaAnalisis(dto.getFechaAnalisis())
                .usuario(usuario)
                .build();

        Fruta frutaGuardada = frutaRepository.save(fruta);

        return FrutaDTO.builder()
                .id(frutaGuardada.getId())
                .nombre(frutaGuardada.getNombre())
                .estado(frutaGuardada.getEstado())
                .precio(frutaGuardada.getPrecio())
                .lugarAnalisis(frutaGuardada.getLugarAnalisis())
                .fechaAnalisis(frutaGuardada.getFechaAnalisis())
                .usuarioId(frutaGuardada.getUsuario().getId())
                .build();
    }

    @Transactional
    public List<Fruta> listarFrutas() {
        return frutaRepository.findAll();
    }

    @Transactional
    public boolean actualizarFruta(Long id, FrutaDTO dto) {
        return frutaRepository.actualizarFruta(id, dto.getNombre(), dto.getPrecio(), dto.getLugarAnalisis()) > 0;
    }

    @Transactional
    public void eliminarFruta(Long id) {
        frutaRepository.deleteById(id);
    }

    @Transactional
    public List<FrutaDTO> obtenerHistorialFrutas(Long usuarioId) {
        List<Fruta> frutas = frutaRepository.obtenerHistorialFrutas(usuarioId);
        return frutas.stream()
                .map(u -> new FrutaDTO(u.getId(), u.getNombre(), u.getEstado(), u.getPrecio(), u.getLugarAnalisis(),
                        u.getFechaAnalisis(), u.getUsuario().getId()))
                .collect(Collectors.toList());
    }
}
