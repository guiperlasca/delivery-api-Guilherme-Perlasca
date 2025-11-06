package com.deliverytech.delivery.controller;

import com.deliverytech.delivery.dto.ClienteRequestDTO;
import com.deliverytech.delivery.dto.ClienteResponseDTO;
import com.deliverytech.delivery.entity.Cliente;
import com.deliverytech.delivery.service.ClienteService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ModelMapper modelMapper;

    // POST /api/clientes - Cadastrar cliente (AGORA USA DTOs e @Valid)
    @PostMapping
    public ResponseEntity<?> cadastrar(@Valid @RequestBody ClienteRequestDTO clienteDTO) {
        try {
            Cliente clienteSalvo = clienteService.cadastrar(clienteDTO);
            // Converte a entidade salva para o DTO de Resposta
            ClienteResponseDTO responseDTO = modelMapper.map(clienteSalvo, ClienteResponseDTO.class);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // GET /api/clientes - Listar todos os clientes ativos
    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> listarTodos() {
        List<Cliente> clientes = clienteService.buscarTodos();
        // Converte a lista de entidades para uma lista de DTOs
        List<ClienteResponseDTO> responseDTOs = clientes.stream()
                .map(cliente -> modelMapper.map(cliente, ClienteResponseDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    // GET /api/clientes/{id} - Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Optional<Cliente> cliente = clienteService.buscarPorId(id);

        if (cliente.isPresent()) {
            ClienteResponseDTO responseDTO = modelMapper.map(cliente.get(), ClienteResponseDTO.class);
            return ResponseEntity.ok(responseDTO);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("erro", "Cliente não encontrado"));
    }

    // GET /api/clientes/buscar?nome=João
    @GetMapping("/buscar")
    public ResponseEntity<List<ClienteResponseDTO>> buscarPorNome(@RequestParam String nome) {
        List<Cliente> clientes = clienteService.buscarPorNome(nome);
        List<ClienteResponseDTO> responseDTOs = clientes.stream()
                .map(cliente -> modelMapper.map(cliente, ClienteResponseDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    // GET /api/clientes/email/{email}
    @GetMapping("/email/{email}")
    public ResponseEntity<?> buscarPorEmail(@PathVariable String email) {
        Optional<Cliente> cliente = clienteService.buscarPorEmail(email);

        if (cliente.isPresent()) {
            ClienteResponseDTO responseDTO = modelMapper.map(cliente.get(), ClienteResponseDTO.class);
            return ResponseEntity.ok(responseDTO);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("erro", "Cliente não encontrado com este email"));
    }

    // PUT /api/clientes/{id} - Atualizar cliente
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @Valid @RequestBody ClienteRequestDTO clienteDTO) {
        try {
            Cliente clienteAtualizado = clienteService.atualizar(id, clienteDTO);
            ClienteResponseDTO responseDTO = modelMapper.map(clienteAtualizado, ClienteResponseDTO.class);
            return ResponseEntity.ok(responseDTO); // MODIFICADO
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // DELETE /api/clientes/{id} - Inativar cliente (soft delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> inativar(@PathVariable Long id) {
        // ... (método inalterado)
        try {
            clienteService.inativar(id);
            return ResponseEntity.ok(Map.of("mensagem", "Cliente inativado com sucesso"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // PATCH /api/clientes/{id}/reativar - Reativar cliente
    @PatchMapping("/{id}/reativar")
    public ResponseEntity<?> reativar(@PathVariable Long id) {
        // ... (método inalterado)
        try {
            clienteService.reativar(id);
            return ResponseEntity.ok(Map.of("mensagem", "Cliente reativado com sucesso"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // GET /api/clientes/estatisticas - Estatísticas básicas
    @GetMapping("/estatisticas")
    public ResponseEntity<?> estatisticas() {
        // ... (método inalterado)
        Long totalAtivos = clienteService.contarAtivos();
        return ResponseEntity.ok(Map.of(
                "totalClientesAtivos", totalAtivos,
                "timestamp", java.time.LocalDateTime.now()
        ));
    }
}