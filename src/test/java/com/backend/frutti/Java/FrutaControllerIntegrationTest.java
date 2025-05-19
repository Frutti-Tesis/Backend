package com.backend.frutti.Java;

import com.backend.frutti.DTOs.FrutaDTO;
import com.backend.frutti.model.Estado;
import com.backend.frutti.model.LugarAnalisis;
import com.backend.frutti.model.Nombre;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class FrutaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void flujo_RegistrarFruta_Historial_ObtenerFruta_EliminarFruta() throws Exception {
        Long usuarioId = 1L;

        FrutaDTO frutaNueva = FrutaDTO.builder()
                .nombre(Nombre.Banana)
                .estado(Estado.Good)
                .precio(2.5f)
                .peso(0.4f)
                .lugarAnalisis(LugarAnalisis.Carulla)
                .fechaAnalisis(LocalDate.now())
                .usuarioId(usuarioId)
                .build();

        String frutaResponse = mockMvc.perform(post("/fruta/registrarFruta")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(frutaNueva)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        FrutaDTO frutaRegistrada = objectMapper.readValue(frutaResponse, FrutaDTO.class);
        Long frutaId = frutaRegistrada.getId();

        mockMvc.perform(get("/fruta/historial/{idUsuario}", usuarioId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(frutaId))
                .andExpect(jsonPath("$[0].usuarioId").value(usuarioId));

        mockMvc.perform(get("/fruta/obtenerFruta/{idFruta}, {idUsuario}", frutaId, usuarioId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(frutaId))
                .andExpect(jsonPath("$.usuarioId").value(usuarioId));

        mockMvc.perform(delete("/fruta/eliminar/{idFruta}/{idUsuario}", frutaId, usuarioId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/fruta/obtenerFruta/{idFruta}, {idUsuario}", frutaId, usuarioId))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void flujo_RegistrarFruta_ObtenerHistorial_EliminarHistorial() throws Exception {
        Long usuarioId = 1L;

        FrutaDTO frutaDTO = FrutaDTO.builder()
                .nombre(Nombre.Apple)
                .estado(Estado.Good)
                .precio(2.0f)
                .peso(0.5f)
                .lugarAnalisis(LugarAnalisis.Exito)
                .fechaAnalisis(LocalDate.now())
                .usuarioId(usuarioId)
                .build();

        mockMvc.perform(post("/fruta/registrarFruta")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(frutaDTO)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/fruta/historial/{idUsuario}", usuarioId))
                .andExpect(status().isOk());
    
        mockMvc.perform(delete("/fruta/eliminarHistorial/{idUsuario}", usuarioId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/fruta/historial/{idUsuario}", usuarioId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    public void registrarCincoFrutas_yListarTop3() throws Exception {
        Long usuarioId = 1L;

        for (int i = 0; i < 5; i++) {
            FrutaDTO fruta = FrutaDTO.builder()
                    .nombre(Nombre.values()[i])
                    .estado(i < 3 ? Estado.Good : Estado.Bad)
                    .precio(1.0f + i)
                    .peso(0.2f * i)
                    .lugarAnalisis(LugarAnalisis.Exito)
                    .fechaAnalisis(LocalDate.now().minusDays(i))
                    .usuarioId(usuarioId)
                    .build();

            mockMvc.perform(post("/fruta/registrarFruta")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(fruta)))
                    .andExpect(status().isOk());
        }

        mockMvc.perform(get("/fruta/listarFrutas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].estado").value("Good"));
    }
}
