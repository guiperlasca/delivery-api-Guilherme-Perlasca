package com.deliverytech.delivery.service;

import com.deliverytech.delivery.dto.ItemPedidoDTO;
import com.deliverytech.delivery.dto.PedidoRequestDTO;
import com.deliverytech.delivery.entity.Cliente;
import com.deliverytech.delivery.entity.Pedido;
import com.deliverytech.delivery.entity.Produto;
import com.deliverytech.delivery.entity.Restaurante;
import com.deliverytech.delivery.exception.BusinessException;
import com.deliverytech.delivery.repository.ClienteRepository;
import com.deliverytech.delivery.repository.PedidoRepository;
import com.deliverytech.delivery.repository.ProdutoRepository;
import com.deliverytech.delivery.repository.RestauranteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;
    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private RestauranteRepository restauranteRepository;
    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private PedidoService pedidoService;

    @Test
    @DisplayName("Deve criar pedido com sucesso")
    void deveCriarPedidoComSucesso() {
        // Arrange
        Long clienteId = 1L;
        Long restauranteId = 2L;
        Long produtoId = 3L;

        // Mock Cliente
        Cliente cliente = new Cliente();
        cliente.setId(clienteId);
        cliente.setAtivo(true);
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));

        // Mock Restaurante
        Restaurante restaurante = new Restaurante();
        restaurante.setId(restauranteId);
        restaurante.setAtivo(true);
        restaurante.setTaxaEntrega(BigDecimal.valueOf(10.0));
        when(restauranteRepository.findById(restauranteId)).thenReturn(Optional.of(restaurante));

        // Mock Produto
        Produto produto = new Produto();
        produto.setId(produtoId);
        produto.setPreco(BigDecimal.valueOf(20.0));
        produto.setDisponivel(true);
        produto.setRestaurante(restaurante);
        when(produtoRepository.findById(produtoId)).thenReturn(Optional.of(produto));

        // Mock DTO
        PedidoRequestDTO dto = new PedidoRequestDTO();
        dto.setClienteId(clienteId);
        dto.setRestauranteId(restauranteId);
        ItemPedidoDTO item = new ItemPedidoDTO();
        item.setProdutoId(produtoId);
        item.setQuantidade(2); // 2 * 20.0 = 40.0
        dto.setItens(List.of(item));

        // Mock Save
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        Pedido pedidoCriado = pedidoService.criarPedido(dto);

        // Assert
        assertNotNull(pedidoCriado);
        // Valor total = (2 * 20.0) + 10.0 (taxa) = 50.0
        assertEquals(BigDecimal.valueOf(50.0), pedidoCriado.getValorTotal());
        assertEquals(Pedido.StatusPedido.PENDENTE, pedidoCriado.getStatus());
        verify(pedidoRepository).save(any(Pedido.class));
    }

    @Test
    @DisplayName("Deve lançar erro se produto não pertence ao restaurante")
    void deveLancarErroProdutoRestauranteInvalido() {
        // Arrange
        Long restauranteId = 1L;
        Long outroRestauranteId = 2L;

        Cliente cliente = new Cliente();
        cliente.setAtivo(true);

        Restaurante restaurante = new Restaurante();
        restaurante.setId(restauranteId);
        restaurante.setAtivo(true);

        Restaurante outroRestaurante = new Restaurante();
        outroRestaurante.setId(outroRestauranteId);

        Produto produto = new Produto();
        produto.setId(1L);
        produto.setDisponivel(true);
        produto.setRestaurante(outroRestaurante); // Produto pertence a outro

        when(clienteRepository.findById(any())).thenReturn(Optional.of(cliente));
        when(restauranteRepository.findById(restauranteId)).thenReturn(Optional.of(restaurante));
        when(produtoRepository.findById(any())).thenReturn(Optional.of(produto));

        PedidoRequestDTO dto = new PedidoRequestDTO();
        dto.setClienteId(1L);
        dto.setRestauranteId(restauranteId);
        ItemPedidoDTO item = new ItemPedidoDTO();
        item.setProdutoId(1L);
        item.setQuantidade(1);
        dto.setItens(List.of(item));

        // Act & Assert
        assertThrows(BusinessException.class, () -> pedidoService.criarPedido(dto));
    }
}
