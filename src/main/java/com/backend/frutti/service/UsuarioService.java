package com.backend.frutti.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.backend.frutti.DTOs.UsuarioDTO;
import com.backend.frutti.DTOs.UsuarioUpdateDTO;
import com.backend.frutti.model.Usuario;
import com.backend.frutti.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioDTO registrarUsuario(UsuarioDTO dto) {
        Usuario usuario = Usuario.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .nombre(dto.getNombre())
                .edad(dto.getEdad())
                .genero(dto.getGenero())
                .build();

        Usuario UsuarioGuardado = usuarioRepository.save(usuario);

        return UsuarioDTO.builder()
                .id(UsuarioGuardado.getId())
                .email(UsuarioGuardado.getEmail())
                .password(UsuarioGuardado.getPassword())
                .nombre(UsuarioGuardado.getNombre())
                .edad(UsuarioGuardado.getEdad())
                .genero(UsuarioGuardado.getGenero())
                .build();
    }

    @Transactional
    public List<UsuarioDTO> listarUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return usuarios.stream()
                .map(u -> new UsuarioDTO(u.getId(), u.getEmail(), u.getPassword(), u.getNombre(), u.getEdad(),
                        u.getGenero()))
                .collect(Collectors.toList());
    }

    @Transactional
    public UsuarioDTO actualizarUsuario(Long id, UsuarioUpdateDTO dto) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);
        if (optionalUsuario.isPresent()) {
            Usuario usuario = optionalUsuario.get();

            usuario.setEmail(dto.getEmail());
            usuario.setNombre(dto.getNombre());
            if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
                usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
            }

            usuarioRepository.save(usuario);

            return UsuarioDTO.builder()
                    .id(usuario.getId())
                    .email(usuario.getEmail())
                    .password(usuario.getPassword())
                    .nombre(usuario.getNombre())
                    .edad(usuario.getEdad())
                    .genero(usuario.getGenero())
                    .build();
        } else {
            return null;
        }
    }

    @Transactional
    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
                return usuarioRepository.findByEmail(email)
                        .map(user -> (UserDetails) user)
                        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));
            }
        };
    }

    @Transactional
    public Long obtenerIdUsuario(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));
        return usuario.getId();
    }

    @Transactional
    public UsuarioDTO obtenerUsuario(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));

        return new UsuarioDTO(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getPassword(),
                usuario.getNombre(),
                usuario.getEdad(),
                usuario.getGenero());
    }
}
