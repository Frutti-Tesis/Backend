package com.backend.frutti.controller;

import com.backend.frutti.DTOs.JwtAuthenticationResponse;
import com.backend.frutti.DTOs.LoginRequestDTO;
import com.backend.frutti.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> login(@RequestBody @Valid LoginRequestDTO request) {
        return ResponseEntity.ok(authenticationService.login(request));
    }
}