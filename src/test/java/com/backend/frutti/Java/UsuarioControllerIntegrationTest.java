package com.backend.frutti.Java;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.backend.frutti.DTOs.CambiarContraseñaDTO;
import com.backend.frutti.DTOs.UsuarioDTO;
import com.backend.frutti.DTOs.UsuarioUpdateDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsuarioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void flujo_Registrar_ObtenerUsuario_ObtenerId_ActualizarUsuario() throws Exception {
        UsuarioDTO nuevoUsuario = UsuarioDTO.builder()
                .email("flujo@email.com")
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
        Long idUsuario = usuarioRegistrado.getId();
        String emailUsuario = usuarioRegistrado.getEmail();

        mockMvc.perform(get("/usuario/obtenerUsuario/{email}", emailUsuario))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(emailUsuario))
                .andExpect(jsonPath("$.nombre").value("Nombre Inicial"));

        String idResponse = mockMvc.perform(get("/usuario/obtenerid/{email}", emailUsuario))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertEquals(idUsuario, Long.valueOf(idResponse));

        UsuarioUpdateDTO actualizacion = UsuarioUpdateDTO.builder()
                .nombre("Nombre Actualizado")
                .email("flujoAcualizado@email.com")
                .edad(30)
                .build();

        mockMvc.perform(patch("/usuario/actualizarUsuario/{id}", idUsuario)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(actualizacion)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Nombre Actualizado"))
                .andExpect(jsonPath("$.email").value("flujoAcualizado@email.com"))
                .andExpect(jsonPath("$.edad").value(30));
    }

    @Test
    public void flujo_Registrar_ObtenerUsuario_ObtenerId_ActualizarContrasena() throws Exception {
        UsuarioDTO nuevoUsuario = UsuarioDTO.builder()
                .email("contrasena@email.com")
                .password("original123")
                .nombre("CambioClave")
                .edad(20)
                .frutasAnalizadas(0)
                .genero("Male")
                .build();

        String response = mockMvc.perform(post("/usuario/registrarUsuario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevoUsuario)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        UsuarioDTO usuarioRegistrado = objectMapper.readValue(response, UsuarioDTO.class);
        Long idUsuario = usuarioRegistrado.getId();
        String emailUsuario = usuarioRegistrado.getEmail();

        mockMvc.perform(get("/usuario/obtenerUsuario/{email}", emailUsuario))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(emailUsuario));

        String idObtenido = mockMvc.perform(get("/usuario/obtenerid/{email}", emailUsuario))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertEquals(idUsuario, Long.valueOf(idObtenido));

        CambiarContraseñaDTO cambio = new CambiarContraseñaDTO();
        cambio.setNuevaPassword("claveNueva123");

        mockMvc.perform(patch("/usuario/actualizarContraseña/{id}", idUsuario)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cambio)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(idUsuario));
    }

    @Test
    public void flujo_Registrar_ObtenerUsuario_ObtenerId_EliminarUsuario() throws Exception {
        UsuarioDTO nuevoUsuario = UsuarioDTO.builder()
                .email("flujo.final@email.com")
                .password("abc123")
                .nombre("Flujo Final")
                .edad(29)
                .frutasAnalizadas(0)
                .genero("Male")
                .build();

        String response = mockMvc.perform(post("/usuario/registrarUsuario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevoUsuario)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        UsuarioDTO usuarioRegistrado = objectMapper.readValue(response, UsuarioDTO.class);
        Long idUsuario = usuarioRegistrado.getId();
        String emailUsuario = usuarioRegistrado.getEmail();

        mockMvc.perform(get("/usuario/obtenerUsuario/{email}", emailUsuario))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(emailUsuario));

        String idObtenido = mockMvc.perform(get("/usuario/obtenerid/{email}", emailUsuario))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertEquals(idUsuario, Long.valueOf(idObtenido));

        mockMvc.perform(delete("/usuario/eliminar/{id}", idUsuario))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/usuario/obtenerUsuario/{email}", emailUsuario))
                .andExpect(status().is4xxClientError());

    }

}
