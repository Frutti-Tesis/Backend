package com.backend.frutti.Java;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.backend.frutti.DTOs.UsuarioDTO;
import com.backend.frutti.DTOs.UsuarioUpdateDTO;
import com.backend.frutti.model.Usuario;
import com.backend.frutti.repository.UsuarioRepository;
import com.backend.frutti.service.UsuarioService;

public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    public UsuarioServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegistrarUsuario_CreacionExitosa() {

        UsuarioDTO dto = UsuarioDTO.builder()
                .email("test@email.com")
                .password("1234")
                .nombre("Santiago")
                .edad(22)
                .frutasAnalizadas(0)
                .genero("Male")
                .build();

        String passwordCodificada = "hashedPassword";
        when(passwordEncoder.encode("1234")).thenReturn(passwordCodificada);

        Usuario usuarioGuardado = Usuario.builder()
                .id(1L)
                .email(dto.getEmail())
                .password(passwordCodificada)
                .nombre(dto.getNombre())
                .edad(dto.getEdad())
                .frutasAnalizadas(dto.getFrutasAnalizadas())
                .genero(dto.getGenero())
                .build();

        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioGuardado);

        UsuarioDTO resultado = usuarioService.registrarUsuario(dto);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(dto.getEmail(), resultado.getEmail());
        assertEquals(passwordCodificada, resultado.getPassword());
        assertEquals(dto.getNombre(), resultado.getNombre());
        assertEquals(dto.getEdad(), resultado.getEdad());
        assertEquals(dto.getGenero(), resultado.getGenero());
        assertEquals(dto.getFrutasAnalizadas(), resultado.getFrutasAnalizadas());

        verify(usuarioRepository, times(1)).save(any(Usuario.class));
        verify(passwordEncoder, times(1)).encode("1234");
    }

    @Test
    public void testRegistrarUsuario_EmailYaRegistrado() {

        UsuarioDTO dto = UsuarioDTO.builder()
                .email("ya@registrado.com")
                .password("1234")
                .nombre("Santiago")
                .edad(22)
                .frutasAnalizadas(0)
                .genero("Male")
                .build();

        Usuario existente = Usuario.builder()
                .id(1L)
                .email(dto.getEmail())
                .password("hashedPassword")
                .nombre("Otro Usuario")
                .edad(30)
                .frutasAnalizadas(5)
                .genero("Male")
                .build();

        when(usuarioRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(existente));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.registrarUsuario(dto);
        });

        assertEquals("El email ya está registrado", exception.getMessage());

        verify(usuarioRepository, never()).save(any(Usuario.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    public void testListarUsuarios_Exitoso() {

        Usuario usuario1 = Usuario.builder()
                .id(1L)
                .email("usuario1@email.com")
                .password("pass1")
                .nombre("Usuario Uno")
                .edad(25)
                .frutasAnalizadas(3)
                .genero("Male")
                .build();

        Usuario usuario2 = Usuario.builder()
                .id(2L)
                .email("usuario2@email.com")
                .password("pass2")
                .nombre("Usuario Dos")
                .edad(30)
                .frutasAnalizadas(5)
                .genero("Female")
                .build();

        List<Usuario> usuariosMock = List.of(usuario1, usuario2);

        when(usuarioRepository.findAll()).thenReturn(usuariosMock);

        List<UsuarioDTO> resultado = usuarioService.listarUsuarios();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());

        assertEquals("usuario1@email.com", resultado.get(0).getEmail());
        assertEquals("usuario2@email.com", resultado.get(1).getEmail());

        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    public void testActualizarUsuario_Exitoso() {

        Long userId = 1L;

        Usuario usuarioExistente = Usuario.builder()
                .id(userId)
                .email("original@email.com")
                .password("hashed123")
                .nombre("Nombre Original")
                .edad(25)
                .frutasAnalizadas(3)
                .genero("Male")
                .build();

        UsuarioUpdateDTO dto = UsuarioUpdateDTO.builder()
                .email("actualizado@email.com")
                .nombre("Nombre Actualizado")
                .edad(30)
                .build();

        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(usuarioExistente));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioExistente);

        UsuarioDTO resultado = usuarioService.actualizarUsuario(userId, dto);

        assertNotNull(resultado);
        assertEquals(userId, resultado.getId());
        assertEquals("actualizado@email.com", resultado.getEmail());
        assertEquals("Nombre Actualizado", resultado.getNombre());
        assertEquals(30, resultado.getEdad());
        assertEquals("hashed123", resultado.getPassword());
        assertEquals(3, resultado.getFrutasAnalizadas());
        assertEquals("Male", resultado.getGenero());

        verify(usuarioRepository, times(1)).findById(userId);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    public void testActualizarUsuario_EmailYaEnUso() {
        Long idUsuario = 1L;

        Usuario usuarioExistente = Usuario.builder()
                .id(idUsuario)
                .email("original@email.com")
                .password("hashed123")
                .nombre("Original")
                .edad(22)
                .frutasAnalizadas(0)
                .genero("Male")
                .build();

        Usuario otroUsuario = Usuario.builder()
                .id(2L)
                .email("ya@registrado.com")
                .build();

        UsuarioUpdateDTO dto = UsuarioUpdateDTO.builder()
                .email("ya@registrado.com")
                .nombre("Nombre Actualizado")
                .edad(30)
                .build();

        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuarioExistente));
        when(usuarioRepository.findByEmail("ya@registrado.com")).thenReturn(Optional.of(otroUsuario));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.actualizarUsuario(idUsuario, dto);
        });

        assertEquals("El email ya está en uso por otro usuario", exception.getMessage());

        verify(usuarioRepository, times(1)).findById(idUsuario);
        verify(usuarioRepository, times(1)).findByEmail("ya@registrado.com");
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    public void testEliminarUsuario_Exitoso() {

        Long idUsuario = 1L;

        when(usuarioRepository.existsById(idUsuario)).thenReturn(true);
        doNothing().when(usuarioRepository).deleteById(idUsuario);

        usuarioService.eliminarUsuario(idUsuario);

        verify(usuarioRepository, times(1)).existsById(idUsuario);
        verify(usuarioRepository, times(1)).deleteById(idUsuario);
    }

    @Test
    public void testEliminarUsuario_Inexistente() {

        Long idInexistente = 999L;

        when(usuarioRepository.existsById(idInexistente)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.eliminarUsuario(idInexistente);
        });

        assertEquals("El usuario no existe", exception.getMessage());

        verify(usuarioRepository, times(1)).existsById(idInexistente);
        verify(usuarioRepository, never()).deleteById(anyLong());
    }

    @Test
    public void testObtenerIdUsuario_Exitoso() {

        String email = "usuario@email.com";
        Long idEsperado = 1L;

        Usuario usuarioMock = Usuario.builder()
                .id(idEsperado)
                .email(email)
                .password("hashedPass")
                .nombre("Usuario Test")
                .edad(25)
                .frutasAnalizadas(5)
                .genero("Male")
                .build();

        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuarioMock));

        Long idObtenido = usuarioService.obtenerIdUsuario(email);

        assertNotNull(idObtenido);
        assertEquals(idEsperado, idObtenido);

        verify(usuarioRepository, times(1)).findByEmail(email);
    }

    @Test
    public void testObtenerIdUsuario_EmailNoExistente() {
        String emailNoRegistrado = "noexiste@email.com";

        when(usuarioRepository.findByEmail(emailNoRegistrado)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.obtenerIdUsuario(emailNoRegistrado);
        });

        assertEquals("Usuario no encontrado con email: " + emailNoRegistrado, exception.getMessage());

        verify(usuarioRepository, times(1)).findByEmail(emailNoRegistrado);
    }

    @Test
    public void testObtenerUsuario_EmailExistente() {

        String email = "usuario@email.com";

        Usuario usuarioMock = Usuario.builder()
                .id(1L)
                .email(email)
                .password("hashedPass")
                .nombre("Santiago")
                .edad(22)
                .frutasAnalizadas(4)
                .genero("Male")
                .build();

        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuarioMock));

        UsuarioDTO resultado = usuarioService.obtenerUsuario(email);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(email, resultado.getEmail());
        assertEquals("hashedPass", resultado.getPassword());
        assertEquals("Santiago", resultado.getNombre());
        assertEquals(22, resultado.getEdad());
        assertEquals(4, resultado.getFrutasAnalizadas());
        assertEquals("Male", resultado.getGenero());

        verify(usuarioRepository, times(1)).findByEmail(email);
    }

    @Test
    public void testObtenerUsuario_EmailNoExistente() {

        String emailNoRegistrado = "noexiste@email.com";

        when(usuarioRepository.findByEmail(emailNoRegistrado)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.obtenerUsuario(emailNoRegistrado);
        });

        assertEquals("Usuario no encontrado con email: " + emailNoRegistrado, exception.getMessage());

        verify(usuarioRepository, times(1)).findByEmail(emailNoRegistrado);
    }

    @Test
    public void testActualizarContrasena_Exitosa() {

        Long idUsuario = 1L;
        String nuevaContrasena = "nueva123";
        String contrasenaCodificada = "hashedNueva123";

        Usuario usuarioExistente = Usuario.builder()
                .id(idUsuario)
                .email("usuario@email.com")
                .password("anteriorHashed")
                .nombre("Santiago")
                .edad(22)
                .frutasAnalizadas(5)
                .genero("Masculino")
                .build();

        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuarioExistente));
        when(passwordEncoder.encode(nuevaContrasena)).thenReturn(contrasenaCodificada);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioExistente);

        UsuarioDTO resultado = usuarioService.actualizarContraseña(idUsuario, nuevaContrasena);

        assertNotNull(resultado);
        assertEquals(contrasenaCodificada, resultado.getPassword());
        assertEquals("usuario@email.com", resultado.getEmail());
        assertEquals("Santiago", resultado.getNombre());

        verify(usuarioRepository, times(1)).findById(idUsuario);
        verify(passwordEncoder, times(1)).encode(nuevaContrasena);
        verify(usuarioRepository, times(1)).save(usuarioExistente);
    }

    @Test
    public void testActualizarContrasena_UsuarioNoExistente_LanzaExcepcion() {

        Long idInexistente = 999L;
        String nuevaContrasena = "nueva123";

        when(usuarioRepository.findById(idInexistente)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.actualizarContraseña(idInexistente, nuevaContrasena);
        });

        assertEquals("Usuario no encontrado con id: " + idInexistente, exception.getMessage());

        verify(usuarioRepository, times(1)).findById(idInexistente);
        verify(passwordEncoder, never()).encode(anyString());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

}
