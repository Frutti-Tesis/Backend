package com.backend.frutti.service;

import com.backend.frutti.DTOs.JwtAuthenticationResponse;
import com.backend.frutti.DTOs.LoginRequestDTO;
import com.backend.frutti.model.Usuario;
import com.backend.frutti.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UsuarioRepository usuarioRepository;

    public JwtAuthenticationResponse login(LoginRequestDTO request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Credenciales inválidas");
        }

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Email no encontrado."));

        String jwt = jwtService.generateToken(usuario);
        return new JwtAuthenticationResponse(jwt, usuario.getEmail(), "USUARIO", usuario.getNombre());
    }

}