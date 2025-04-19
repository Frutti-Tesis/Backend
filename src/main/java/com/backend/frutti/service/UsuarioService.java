package com.backend.frutti.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.backend.frutti.DTOs.UsuarioDTO;
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
                .fechaNacimiento(dto.getFechaNacimiento())
                .build();

        Usuario UsuarioGuardado = usuarioRepository.save(usuario);

        return UsuarioDTO.builder()
                .id(UsuarioGuardado.getId())
                .email(UsuarioGuardado.getEmail())
                .password(UsuarioGuardado.getPassword())
                .nombre(UsuarioGuardado.getNombre())
                .edad(UsuarioGuardado.getEdad())
                .fechaNacimiento(UsuarioGuardado.getFechaNacimiento())
                .build();
    }

    @Transactional
    public List<UsuarioDTO> listarUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return usuarios.stream()
                .map(u -> new UsuarioDTO(u.getId(), u.getEmail(), u.getPassword(), u.getNombre(), u.getEdad(),
                        u.getFechaNacimiento()))
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean actualizarUsuario(Long id, UsuarioDTO dto) {
        return usuarioRepository.actualizarUsuario(id, dto.getEmail(), dto.getNombre(), dto.getEdad()) > 0;
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

}
