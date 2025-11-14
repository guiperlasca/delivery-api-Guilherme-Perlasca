package com.deliverytech.delivery.controller;

import com.deliverytech.delivery.dto.ClienteRequestDTO;
import com.deliverytech.delivery.dto.ClienteResponseDTO;
import com.deliverytech.delivery.entity.Cliente;
import com.deliverytech.delivery.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
@Tag(name = "Clientes", description = "Operações para gerenciamento de clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping
    @Operation(summary = "Cadastrar novo cliente")
    @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso",
            content = @Content(schema = @Schema(implementation = ClienteResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "409", description = "Conflito (email já cadastrado)")
    public ResponseEntity<ClienteResponseDTO> cadastrar(@Valid @RequestBody ClienteRequestDTO clienteDTO) {
        Cliente clienteSalvo = clienteService.cadastrar(clienteDTO);
        ClienteResponseDTO responseDTO = modelMapper.map(clienteSalvo, ClienteResponseDTO.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping
    @Operation(summary = "Listar todos os clientes ativos")
    @ApiResponse(responseCode = "200", description = "Lista de clientes")
    public ResponseEntity<List<ClienteResponseDTO>> listarTodos() {
        List<Cliente> clientes = clienteService.buscarTodos();
        List<ClienteResponseDTO> responseDTOs = clientes.stream()
                .map(cliente -> modelMapper.map(cliente, ClienteResponseDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar cliente por ID")
    @ApiResponse(responseCode = "200", description = "Cliente encontrado",
            content = @Content(schema = @Schema(implementation = ClienteResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    public ResponseEntity<ClienteResponseDTO> buscarPorId(@Parameter(description = "ID do cliente") @PathVariable Long id) {
        Cliente cliente = clienteService.buscarPorId(id); // Lança 404 se não achar
        ClienteResponseDTO responseDTO = modelMapper.map(cliente, ClienteResponseDTO.class);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar clientes por nome")
    @ApiResponse(responseCode = "200", description = "Lista de clientes encontrados")
    public ResponseEntity<List<ClienteResponseDTO>> buscarPorNome(
            @Parameter(description = "Termo de busca para o nome") @RequestParam String nome) {
        List<Cliente> clientes = clienteService.buscarPorNome(nome);
        List<ClienteResponseDTO> responseDTOs = clientes.stream()
                .map(cliente -> modelMapper.map(cliente, ClienteResponseDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Buscar cliente por email")
    @ApiResponse(responseCode = "200", description = "Cliente encontrado",
            content = @Content(schema = @Schema(implementation = ClienteResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    public ResponseEntity<ClienteResponseDTO> buscarPorEmail(
            @Parameter(description = "Email exato do cliente") @PathVariable String email) {
        Cliente cliente = clienteService.buscarPorEmail(email); // Lança 404 se não achar
        ClienteResponseDTO responseDTO = modelMapper.map(cliente, ClienteResponseDTO.class);
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar dados do cliente")
    @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso",
            content = @Content(schema = @Schema(implementation = ClienteResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "409", description = "Conflito (email já cadastrado)")
    public ResponseEntity<ClienteResponseDTO> atualizar(
            @Parameter(description = "ID do cliente") @PathVariable Long id,
            @Valid @RequestBody ClienteRequestDTO clienteDTO) {
        Cliente clienteAtualizado = clienteService.atualizar(id, clienteDTO);
        ClienteResponseDTO responseDTO = modelMapper.map(clienteAtualizado, ClienteResponseDTO.class);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Inativar cliente (soft delete)")
    @ApiResponse(responseCode = "200", description = "Cliente inativado com sucesso")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    public ResponseEntity<Map<String, String>> inativar(@Parameter(description = "ID do cliente") @PathVariable Long id) {
        clienteService.inativar(id);
        return ResponseEntity.ok(Map.of("mensagem", "Cliente inativado com sucesso"));
    }

    @PatchMapping("/{id}/reativar")
    @Operation(summary = "Reativar cliente inativo")
    @ApiResponse(responseCode = "200", description = "Cliente reativado com sucesso")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    public ResponseEntity<Map<String, String>> reativar(@Parameter(description = "ID do cliente") @PathVariable Long id) {
        clienteService.reativar(id);
        return ResponseEntity.ok(Map.of("mensagem", "Cliente reativado com sucesso"));
    }

}