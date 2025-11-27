package com.deliverytech.delivery.controller;

import com.deliverytech.delivery.dto.ItemPedidoDTO;
import com.deliverytech.delivery.dto.PedidoRequestDTO;
import com.deliverytech.delivery.entity.Cliente;
import com.deliverytech.delivery.entity.Produto;
import com.deliverytech.delivery.entity.Restaurante;
import com.deliverytech.delivery.repository.ClienteRepository;
import com.deliverytech.delivery.repository.PedidoRepository;
import com.deliverytech.delivery.repository.ProdutoRepository;
import com.deliverytech.delivery.repository.RestauranteRepository;
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

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PedidoControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private RestauranteRepository restauranteRepository;
    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private PedidoRepository pedidoRepository;

    private Cliente cliente;
    private Restaurante restaurante;
    private Produto produto;

    @BeforeEach
    void setUp() {
        pedidoRepository.deleteAll();
        produtoRepository.deleteAll();
        restauranteRepository.deleteAll();
        clienteRepository.deleteAll();

        // Massa de dados para teste
        cliente = clienteRepository.save(new Cliente("Bruce Banner", "hulk@avengers.com", "999999", "Lab"));

        restaurante = new Restaurante("Shawarma Palace", "Árabe", "NY");
        restaurante.setTaxaEntrega(BigDecimal.valueOf(5.0));
        restaurante = restauranteRepository.save(restaurante);

        produto = new Produto("Shawarma Misto", "Delicioso", BigDecimal.valueOf(20.0), "Lanche", restaurante);
        produto = produtoRepository.save(produto);
    }

    @Test
    @DisplayName("Deve criar pedido com sucesso")
    @WithMockUser(roles = "CLIENTE") // Apenas CLIENTE pode criar pedidos (conforme SecurityConfig/Controller)
    void deveCriarPedido() throws Exception {
        PedidoRequestDTO dto = new PedidoRequestDTO();
        dto.setClienteId(cliente.getId());
        dto.setRestauranteId(restaurante.getId());

        ItemPedidoDTO item = new ItemPedidoDTO();
        item.setProdutoId(produto.getId());
        item.setQuantidade(2); // 2 * 20.0 = 40.0
        dto.setItens(List.of(item));

        // Total esperado: 40.0 (itens) + 5.0 (taxa) = 45.0

        mockMvc.perform(post("/api/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.valorTotal", is(45.0)))
                .andExpect(jsonPath("$.status", is("PENDENTE")));
    }

    @Test
    @DisplayName("Deve retornar 400 ao tentar criar pedido sem itens")
    @WithMockUser(roles = "CLIENTE")
    void deveFalharSemItens() throws Exception {
        PedidoRequestDTO dto = new PedidoRequestDTO();
        dto.setClienteId(cliente.getId());
        dto.setRestauranteId(restaurante.getId());
        dto.setItens(List.of()); // Lista vazia

        mockMvc.perform(post("/api/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}
