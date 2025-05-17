package com.backend.frutti.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.frutti.DTOs.FrutaDTO;
import com.backend.frutti.model.Fruta;
import com.backend.frutti.model.Usuario;
import com.backend.frutti.repository.FrutaRepository;
import com.backend.frutti.repository.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;
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
                .peso(dto.getPeso())
                .lugarAnalisis(dto.getLugarAnalisis())
                .fechaAnalisis(dto.getFechaAnalisis())
                .usuario(usuario)
                .build();

        Fruta frutaGuardada = frutaRepository.save(fruta);

        usuario.setFrutasAnalizadas(usuario.getFrutasAnalizadas() + 1);
        usuarioRepository.save(usuario);

        return FrutaDTO.builder()
                .id(frutaGuardada.getId())
                .nombre(frutaGuardada.getNombre())
                .estado(frutaGuardada.getEstado())
                .precio(frutaGuardada.getPrecio())
                .peso(frutaGuardada.getPeso())
                .lugarAnalisis(frutaGuardada.getLugarAnalisis())
                .fechaAnalisis(frutaGuardada.getFechaAnalisis())
                .usuarioId(frutaGuardada.getUsuario().getId())
                .build();
    }

    @Transactional
    public List<FrutaDTO> listarMejoresFrutas() {
        List<Fruta> frutas = frutaRepository.findAll();

        frutas.sort((f1, f2) -> {
            int estadoCompare = f1.getEstado().compareTo(f2.getEstado());
            if (estadoCompare != 0) {
                return estadoCompare;
            }

            int fechaCompare = f2.getFechaAnalisis().compareTo(f1.getFechaAnalisis());
            if (fechaCompare != 0) {
                return fechaCompare;
            }

            return Float.compare(f1.getPrecio(), f2.getPrecio());
        });

        List<Fruta> frutasTop3 = frutas.size() > 3 ? frutas.subList(0, 3) : frutas;

        List<FrutaDTO> frutasDTO = new ArrayList<>();
        for (Fruta fruta : frutasTop3) {
            frutasDTO.add(new FrutaDTO(
                    fruta.getId(),
                    fruta.getNombre(),
                    fruta.getEstado(),
                    fruta.getPrecio(),
                    fruta.getPeso(),
                    fruta.getLugarAnalisis(),
                    fruta.getFechaAnalisis(),
                    fruta.getUsuario().getId()));
        }

        return frutasDTO;
    }

    @Transactional
    public void eliminarFruta(Long idFruta, Long idUsuario) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(idUsuario);
        if (optionalUsuario.isPresent()) {
            Usuario usuario = optionalUsuario.get();

            usuario.setFrutasAnalizadas(usuario.getFrutasAnalizadas() - 1);
            usuarioRepository.save(usuario);
        }
        frutaRepository.deleteById(idFruta);
    }

    @Transactional
    public List<FrutaDTO> obtenerHistorialFrutas(Long usuarioId) {
        List<Fruta> frutas = frutaRepository.obtenerHistorialFrutas(usuarioId);
        return frutas.stream()
                .map(u -> new FrutaDTO(u.getId(), u.getNombre(), u.getEstado(), u.getPrecio(), u.getPeso(),
                        u.getLugarAnalisis(),
                        u.getFechaAnalisis(), u.getUsuario().getId()))
                .collect(Collectors.toList());
    }

    @Transactional
    public FrutaDTO obtenerFruta(Long frutaId, Long usuarioId) {
        Fruta fruta = frutaRepository.obtenerFruta(usuarioId, frutaId);
        if (fruta == null) {
            throw new EntityNotFoundException("Fruta no encontrada para el usuario");
        }
        return new FrutaDTO(
                fruta.getId(),
                fruta.getNombre(),
                fruta.getEstado(),
                fruta.getPrecio(),
                fruta.getPeso(),
                fruta.getLugarAnalisis(),
                fruta.getFechaAnalisis(),
                fruta.getUsuario().getId());
    }

    @Transactional
    public void eliminarHistorialUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        frutaRepository.deleteByUsuarioId(usuarioId);

        usuario.setFrutasAnalizadas(0);
        usuarioRepository.save(usuario);
    }

}
