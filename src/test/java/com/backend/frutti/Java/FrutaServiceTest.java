package com.backend.frutti.Java;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.backend.frutti.DTOs.FrutaDTO;
import com.backend.frutti.model.Estado;
import com.backend.frutti.model.Fruta;
import com.backend.frutti.model.LugarAnalisis;
import com.backend.frutti.model.Nombre;
import com.backend.frutti.model.Usuario;
import com.backend.frutti.repository.FrutaRepository;
import com.backend.frutti.repository.UsuarioRepository;
import com.backend.frutti.service.FrutaService;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class FrutaServiceTest {

    @InjectMocks
    private FrutaService frutaService;

    @Mock
    private FrutaRepository frutaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Test
    public void testRegistrarFruta_CreacionExitosa() {
        Long usuarioId = 1L;
        FrutaDTO dto = FrutaDTO.builder()
                .nombre(Nombre.Banana)
                .estado(Estado.Good)
                .precio(1.5f)
                .peso(0.3f)
                .lugarAnalisis(LugarAnalisis.Exito)
                .fechaAnalisis(LocalDate.now())
                .usuarioId(usuarioId)
                .build();

        Usuario usuario = Usuario.builder()
                .id(usuarioId)
                .frutasAnalizadas(2)
                .build();

        Fruta frutaGuardada = Fruta.builder()
                .id(10L)
                .nombre(dto.getNombre())
                .estado(dto.getEstado())
                .precio(dto.getPrecio())
                .peso(dto.getPeso())
                .lugarAnalisis(dto.getLugarAnalisis())
                .fechaAnalisis(dto.getFechaAnalisis())
                .usuario(usuario)
                .build();

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(frutaRepository.save(any(Fruta.class))).thenReturn(frutaGuardada);
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        FrutaDTO resultado = frutaService.registrarFruta(dto);

        assertNotNull(resultado);
        assertEquals(10L, resultado.getId());
        assertEquals(Nombre.Banana, resultado.getNombre());
        assertEquals(Estado.Good, resultado.getEstado());
        assertEquals(LugarAnalisis.Exito, resultado.getLugarAnalisis());
        assertEquals(1.5f, resultado.getPrecio());
        assertEquals(0.3f, resultado.getPeso());
        assertEquals(LocalDate.now(), resultado.getFechaAnalisis());
        assertEquals(usuarioId, resultado.getUsuarioId());

        verify(usuarioRepository, times(1)).findById(usuarioId);
        verify(frutaRepository, times(1)).save(any(Fruta.class));
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    public void testRegistrarFruta_UsuarioNoExistente() {
        Long usuarioId = 99L;

        FrutaDTO dto = FrutaDTO.builder()
                .nombre(Nombre.Apple)
                .estado(Estado.Bad)
                .precio(2.0f)
                .peso(0.4f)
                .lugarAnalisis(LugarAnalisis.D1)
                .fechaAnalisis(LocalDate.now())
                .usuarioId(usuarioId)
                .build();

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            frutaService.registrarFruta(dto);
        });

        assertEquals("Usuario no encontrado", exception.getMessage());

        verify(usuarioRepository, times(1)).findById(usuarioId);
        verify(frutaRepository, never()).save(any(Fruta.class));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    public void testListarMejoresFrutas_Exitoso() {
        Usuario usuario = Usuario.builder().id(1L).build();

        Fruta fruta1 = Fruta.builder()
                .id(1L)
                .nombre(Nombre.Apple)
                .estado(Estado.Good)
                .precio(2.0f)
                .peso(0.4f)
                .lugarAnalisis(LugarAnalisis.Ara)
                .fechaAnalisis(LocalDate.now())
                .usuario(usuario)
                .build();

        Fruta fruta2 = Fruta.builder()
                .id(2L)
                .nombre(Nombre.Orange)
                .estado(Estado.Good)
                .precio(1.5f)
                .peso(0.5f)
                .lugarAnalisis(LugarAnalisis.Exito)
                .fechaAnalisis(LocalDate.now().minusDays(1))
                .usuario(usuario)
                .build();

        Fruta fruta3 = Fruta.builder()
                .id(3L)
                .nombre(Nombre.Banana)
                .estado(Estado.Bad)
                .precio(1.0f)
                .peso(0.3f)
                .lugarAnalisis(LugarAnalisis.Carulla)
                .fechaAnalisis(LocalDate.now().minusDays(2))
                .usuario(usuario)
                .build();

        Fruta fruta4 = Fruta.builder()
                .id(4L)
                .nombre(Nombre.Lime)
                .estado(Estado.Good)
                .precio(1.2f)
                .peso(0.2f)
                .lugarAnalisis(LugarAnalisis.D1)
                .fechaAnalisis(LocalDate.now())
                .usuario(usuario)
                .build();

        List<Fruta> frutas = new ArrayList<>(List.of(fruta1, fruta2, fruta3, fruta4));

        when(frutaRepository.findAll()).thenReturn(frutas);

        List<FrutaDTO> resultado = frutaService.listarMejoresFrutas();

        assertNotNull(resultado);
        assertEquals(3, resultado.size());

        assertEquals(Nombre.Lime, resultado.get(0).getNombre()); // Estado Good, fecha reciente, precio más bajo
        assertEquals(Nombre.Apple, resultado.get(1).getNombre()); // Estado Good, fecha igual, precio más alto
        assertEquals(Nombre.Orange, resultado.get(2).getNombre()); // Estado Good, fecha anterior

        verify(frutaRepository, times(1)).findAll();
    }

    @Test
    public void testEliminarFruta_Exitoso() {
        Long idFruta = 1L;
        Long idUsuario = 10L;

        Usuario usuario = Usuario.builder()
                .id(idUsuario)
                .frutasAnalizadas(3)
                .build();

        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuario));
        doNothing().when(frutaRepository).deleteById(idFruta);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        frutaService.eliminarFruta(idFruta, idUsuario);

        assertEquals(2, usuario.getFrutasAnalizadas());

        verify(usuarioRepository, times(1)).findById(idUsuario);
        verify(usuarioRepository, times(1)).save(usuario);
        verify(frutaRepository, times(1)).deleteById(idFruta);
    }

    @Test
    public void testEliminarFruta_UsuarioNoExistente() {
        Long idFruta = 2L;
        Long idUsuario = 99L;

        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.empty());
        doNothing().when(frutaRepository).deleteById(idFruta);

        frutaService.eliminarFruta(idFruta, idUsuario);

        verify(usuarioRepository, times(1)).findById(idUsuario);
        verify(usuarioRepository, never()).save(any(Usuario.class));
        verify(frutaRepository, times(1)).deleteById(idFruta);
    }

    @Test
    public void testObtenerHistorialFrutas_Exitoso() {
        Long idUsuario = 1L;

        Usuario usuario = Usuario.builder().id(idUsuario).build();

        Fruta fruta1 = Fruta.builder()
                .id(1L)
                .nombre(Nombre.Guava)
                .estado(Estado.Good)
                .precio(1.2f)
                .peso(0.25f)
                .lugarAnalisis(LugarAnalisis.Carulla)
                .fechaAnalisis(LocalDate.now())
                .usuario(usuario)
                .build();

        Fruta fruta2 = Fruta.builder()
                .id(2L)
                .nombre(Nombre.Lime)
                .estado(Estado.Bad)
                .precio(0.9f)
                .peso(0.2f)
                .lugarAnalisis(LugarAnalisis.D1)
                .fechaAnalisis(LocalDate.now().minusDays(1))
                .usuario(usuario)
                .build();

        List<Fruta> frutas = List.of(fruta1, fruta2);

        when(frutaRepository.obtenerHistorialFrutas(idUsuario)).thenReturn(frutas);

        List<FrutaDTO> resultado = frutaService.obtenerHistorialFrutas(idUsuario);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());

        assertEquals(Nombre.Guava, resultado.get(0).getNombre());
        assertEquals(Nombre.Lime, resultado.get(1).getNombre());

        assertEquals(idUsuario, resultado.get(0).getUsuarioId());
        assertEquals(idUsuario, resultado.get(1).getUsuarioId());

        verify(frutaRepository, times(1)).obtenerHistorialFrutas(idUsuario);
    }

    @Test
    public void testObtenerHistorialFrutas_Vacio() {
        Long idUsuario = 1L;

        when(frutaRepository.obtenerHistorialFrutas(idUsuario)).thenReturn(List.of());

        List<FrutaDTO> resultado = frutaService.obtenerHistorialFrutas(idUsuario);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());

        verify(frutaRepository, times(1)).obtenerHistorialFrutas(idUsuario);
    }

    @Test
    public void testObtenerFruta_exitoso() {
        Long frutaId = 1L;
        Long usuarioId = 10L;

        Usuario usuario = Usuario.builder()
                .id(usuarioId)
                .build();

        Fruta fruta = Fruta.builder()
                .id(frutaId)
                .nombre(Nombre.Orange)
                .estado(Estado.Good)
                .precio(2.0f)
                .peso(0.35f)
                .lugarAnalisis(LugarAnalisis.Exito)
                .fechaAnalisis(LocalDate.now().minusDays(1))
                .usuario(usuario)
                .build();

        when(frutaRepository.obtenerFruta(usuarioId, frutaId)).thenReturn(fruta);

        FrutaDTO resultado = frutaService.obtenerFruta(frutaId, usuarioId);

        assertNotNull(resultado);
        assertEquals(frutaId, resultado.getId());
        assertEquals(Nombre.Orange, resultado.getNombre());
        assertEquals(Estado.Good, resultado.getEstado());
        assertEquals(2.0f, resultado.getPrecio());
        assertEquals(0.35f, resultado.getPeso());
        assertEquals(LugarAnalisis.Exito, resultado.getLugarAnalisis());
        assertEquals(LocalDate.now().minusDays(1), resultado.getFechaAnalisis());
        assertEquals(usuarioId, resultado.getUsuarioId());

        verify(frutaRepository, times(1)).obtenerFruta(usuarioId, frutaId);
    }

    @Test
    public void testObtenerFruta_NoEncontrada() {
        Long frutaId = 99L;
        Long usuarioId = 10L;

        when(frutaRepository.obtenerFruta(usuarioId, frutaId)).thenReturn(null);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            frutaService.obtenerFruta(frutaId, usuarioId);
        });

        assertEquals("Fruta no encontrada para el usuario", exception.getMessage());

        verify(frutaRepository, times(1)).obtenerFruta(usuarioId, frutaId);
    }

    @Test
    public void testEliminarHistorialUsuario_Exitoso() {
        Long usuarioId = 1L;

        Usuario usuario = Usuario.builder()
                .id(usuarioId)
                .frutasAnalizadas(5)
                .build();

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        doNothing().when(frutaRepository).deleteByUsuarioId(usuarioId);
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        frutaService.eliminarHistorialUsuario(usuarioId);

        assertEquals(0, usuario.getFrutasAnalizadas());

        verify(usuarioRepository, times(1)).findById(usuarioId);
        verify(frutaRepository, times(1)).deleteByUsuarioId(usuarioId);
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    public void testEliminarHistorialUsuario_UsuarioNoExistente() {
        Long usuarioIdInexistente = 999L;

        when(usuarioRepository.findById(usuarioIdInexistente)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            frutaService.eliminarHistorialUsuario(usuarioIdInexistente);
        });

        assertEquals("Usuario no encontrado", exception.getMessage());

        verify(usuarioRepository, times(1)).findById(usuarioIdInexistente);
        verify(frutaRepository, never()).deleteByUsuarioId(anyLong());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

}
