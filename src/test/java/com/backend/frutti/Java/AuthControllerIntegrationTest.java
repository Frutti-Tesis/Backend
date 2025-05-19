package com.backend.frutti.Java;

import com.backend.frutti.DTOs.LoginRequestDTO;
import com.backend.frutti.DTOs.UsuarioDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void login_Exitoso() throws Exception {
        UsuarioDTO nuevoUsuario = UsuarioDTO.builder()
                .email("login@email.com")
                .password("1234")
                .nombre("Nombre Inicial")
                .edad(25)
                .frutasAnalizadas(0)
                .genero("Male")
                .build();

        String response = mockMvc.perform(post("/usuario/registrarUsuario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevoUsuario)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        UsuarioDTO usuarioRegistrado = objectMapper.readValue(response, UsuarioDTO.class);

        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail(usuarioRegistrado.getEmail());
        request.setPassword(nuevoUsuario.getPassword());

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    public void login_Fallido_CredencialesInvalidas() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("usuario@email.com");
        request.setPassword("contrasena_invalida");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());
    }
}
