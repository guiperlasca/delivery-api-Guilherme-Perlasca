package com.deliverytech.delivery.service;

import com.deliverytech.delivery.dto.ClienteRequestDTO;
import com.deliverytech.delivery.entity.Cliente;
import com.deliverytech.delivery.exception.ConflictException;
import com.deliverytech.delivery.exception.EntityNotFoundException;
import com.deliverytech.delivery.repository.ClienteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ClienteService clienteService;

    @Test
    @DisplayName("Deve cadastrar cliente com sucesso")
    void deveCadastrarClienteComSucesso() {
        // Arrange
        ClienteRequestDTO dto = new ClienteRequestDTO();
        dto.setEmail("teste@email.com");
        dto.setNome("Teste");

        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setEmail("teste@email.com");

        when(clienteRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(modelMapper.map(dto, Cliente.class)).thenReturn(cliente);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        // Act
        Cliente resultado = clienteService.cadastrar(dto);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar email duplicado")
    void deveLancarErroEmailDuplicado() {
        // Arrange
        ClienteRequestDTO dto = new ClienteRequestDTO();
        dto.setEmail("existente@email.com");

        when(clienteRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        // Act & Assert
        assertThrows(ConflictException.class, () -> clienteService.cadastrar(dto));
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve buscar cliente por ID existente")
    void deveBuscarClientePorId() {
        // Arrange
        Long id = 1L;
        Cliente cliente = new Cliente();
        cliente.setId(id);

        when(clienteRepository.findById(id)).thenReturn(Optional.of(cliente));

        // Act
        Cliente resultado = clienteService.buscarPorId(id);

        // Assert
        assertEquals(id, resultado.getId());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar ID inexistente")
    void deveLancarErroIdInexistente() {
        // Arrange
        Long id = 99L;
        when(clienteRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> clienteService.buscarPorId(id));
    }
}
