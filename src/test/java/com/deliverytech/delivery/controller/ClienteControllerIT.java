package com.deliverytech.delivery.controller;

import com.deliverytech.delivery.dto.ClienteRequestDTO;
import com.deliverytech.delivery.entity.Cliente;
import com.deliverytech.delivery.repository.ClienteRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ClienteControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        clienteRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve criar cliente com sucesso")
    @WithMockUser(username = "admin", roles = { "ADMIN" }) // Simula usuário autenticado
    void deveCriarCliente() throws Exception {
        ClienteRequestDTO dto = new ClienteRequestDTO();
        dto.setNome("Tony Stark");
        dto.setEmail("tony@stark.com");
        dto.setTelefone("11999999999");
        dto.setEndereco("Malibu Point, 10880");

        mockMvc.perform(post("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome", is("Tony Stark")))
                .andExpect(jsonPath("$.email", is("tony@stark.com")));
    }

    @Test
    @DisplayName("Deve retornar erro 400 ao criar cliente inválido")
    @WithMockUser(roles = "ADMIN")
    void deveRetornarErroClienteInvalido() throws Exception {
        ClienteRequestDTO dto = new ClienteRequestDTO();
        dto.setNome(""); // Inválido (NotBlank)

        mockMvc.perform(post("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve listar clientes")
    @WithMockUser(roles = "ADMIN")
    void deveListarClientes() throws Exception {
        Cliente c1 = new Cliente("Thor", "thor@asgard.com", "999", "Asgard");
        clienteRepository.save(c1);

        mockMvc.perform(get("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome", is("Thor")));
    }
}
