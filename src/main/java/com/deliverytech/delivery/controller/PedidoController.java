package com.deliverytech.delivery.controller;

import com.deliverytech.delivery.dto.PedidoRequestDTO;
import com.deliverytech.delivery.dto.PedidoResponseDTO;
import com.deliverytech.delivery.entity.Pedido;
import com.deliverytech.delivery.entity.Pedido.StatusPedido;
import com.deliverytech.delivery.exception.BusinessException;
import com.deliverytech.delivery.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@Tag(name = "Pedidos", description = "Operações para gerenciamento de pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ModelMapper modelMapper;

    private PedidoResponseDTO convertToResponseDTO(Pedido pedido) {
        PedidoResponseDTO dto = modelMapper.map(pedido, PedidoResponseDTO.class);
        dto.setClienteId(pedido.getCliente().getId());
        dto.setRestauranteId(pedido.getRestaurante().getId());
        return dto;
    }

    @PostMapping("/pedidos")
    @Operation(summary = "Criar novo pedido")
    @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso",
            content = @Content(schema = @Schema(implementation = PedidoResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos (ex: sem itens)")
    @ApiResponse(responseCode = "404", description = "Cliente, Restaurante ou Produto não encontrado")
    @ApiResponse(responseCode = "422", description = "Regra de negócio violada (ex: cliente inativo)")
    public ResponseEntity<PedidoResponseDTO> criarPedido(@Valid @RequestBody PedidoRequestDTO pedidoDTO) {
        Pedido pedido = pedidoService.criarPedido(pedidoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToResponseDTO(pedido));
    }

    @PostMapping("/pedidos/calcular")
    @Operation(summary = "Calcular total de um pedido (sem salvar)")
    @ApiResponse(responseCode = "200", description = "Total calculado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "404", description = "Restaurante ou Produto não encontrado")
    @ApiResponse(responseCode = "422", description = "Produto indisponível")
    public ResponseEntity<Map<String, BigDecimal>> calcularTotal(@Valid @RequestBody PedidoRequestDTO pedidoDTO) {
        BigDecimal total = pedidoService.calcularTotalPedido(pedidoDTO);
        return ResponseEntity.ok(Map.of("valorTotalCalculado", total));
    }

    @GetMapping("/pedidos")
    @Operation(summary = "Listar todos os pedidos", description = "Permite filtrar por status ou data.")
    @ApiResponse(responseCode = "200", description = "Lista de pedidos")
    public ResponseEntity<List<PedidoResponseDTO>> listarTodos(
            @Parameter(description = "Filtrar por status", example = "PENDENTE")
            @RequestParam(required = false) Pedido.StatusPedido status,
            @Parameter(description = "Filtrar por data (YYYY-MM-DD)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {

        List<Pedido> pedidos;

        if (status != null) {
            pedidos = pedidoService.buscarPorStatus(status);
        } else if (data != null) {
            LocalDateTime inicio = data.atStartOfDay();
            LocalDateTime fim = data.plusDays(1).atStartOfDay();
            pedidos = pedidoService.buscarPorPeriodo(inicio, fim);
        } else {
            pedidos = pedidoService.buscarTodos();
        }

        List<PedidoResponseDTO> dtos = pedidos.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/pedidos/{id}")
    @Operation(summary = "Buscar pedido por ID")
    @ApiResponse(responseCode = "200", description = "Pedido encontrado",
            content = @Content(schema = @Schema(implementation = PedidoResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    public ResponseEntity<PedidoResponseDTO> buscarPorId(@Parameter(description = "ID do pedido") @PathVariable Long id) {
        Pedido pedido = pedidoService.buscarPorId(id);
        return ResponseEntity.ok(convertToResponseDTO(pedido));
    }

    @GetMapping("/clientes/{clienteId}/pedidos")
    @Operation(summary = "Buscar histórico de pedidos de um cliente")
    @ApiResponse(responseCode = "200", description = "Lista de pedidos")
    public ResponseEntity<List<PedidoResponseDTO>> buscarPorCliente(
            @Parameter(description = "ID do cliente") @PathVariable Long clienteId) {
        List<Pedido> pedidos = pedidoService.buscarPorCliente(clienteId);
        List<PedidoResponseDTO> dtos = pedidos.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping(value = {"/pedidos/restaurante/{restauranteId}", "/restaurantes/{restauranteId}/pedidos"})
    @Operation(summary = "Buscar pedidos de um restaurante")
    @ApiResponse(responseCode = "200", description = "Lista de pedidos")
    public ResponseEntity<List<PedidoResponseDTO>> buscarPorRestaurante(
            @Parameter(description = "ID do restaurante") @PathVariable Long restauranteId) {
        List<Pedido> pedidos = pedidoService.buscarPorRestaurante(restauranteId);
        List<PedidoResponseDTO> dtos = pedidos.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PatchMapping("/pedidos/{id}/status")
    @Operation(summary = "Atualizar status de um pedido (genérico)")
    @ApiResponse(responseCode = "200", description = "Status atualizado")
    @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    @ApiResponse(responseCode = "422", description = "Transição de status inválida")
    public ResponseEntity<PedidoResponseDTO> atualizarStatus(
            @Parameter(description = "ID do pedido") @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        try {
            StatusPedido novoStatus = StatusPedido.valueOf(body.get("status").toUpperCase());
            Pedido pedidoAtualizado = pedidoService.atualizarStatus(id, novoStatus);
            return ResponseEntity.ok(convertToResponseDTO(pedidoAtualizado));
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Status inválido: " + body.get("status"));
        }
    }

    @PatchMapping("/pedidos/{id}/confirmar")
    @Operation(summary = "Confirmar um pedido (PENDENTE -> CONFIRMADO)")
    @ApiResponse(responseCode = "200", description = "Pedido confirmado")
    public ResponseEntity<PedidoResponseDTO> confirmarPedido(@Parameter(description = "ID do pedido") @PathVariable Long id) {
        Pedido pedidoAtualizado = pedidoService.confirmarPedido(id);
        return ResponseEntity.ok(convertToResponseDTO(pedidoAtualizado));
    }

    @PatchMapping("/pedidos/{id}/preparar")
    @Operation(summary = "Iniciar preparo (CONFIRMADO -> PREPARANDO)")
    @ApiResponse(responseCode = "200", description = "Pedido em preparação")
    public ResponseEntity<PedidoResponseDTO> iniciarPreparacao(@Parameter(description = "ID do pedido") @PathVariable Long id) {
        Pedido pedidoAtualizado = pedidoService.iniciarPreparacao(id);
        return ResponseEntity.ok(convertToResponseDTO(pedidoAtualizado));
    }

    @PatchMapping("/pedidos/{id}/entregar")
    @Operation(summary = "Marcar como entregue (PREPARANDO -> ENTREGUE)")
    @ApiResponse(responseCode = "200", description = "Pedido entregue")
    public ResponseEntity<PedidoResponseDTO> marcarComoEntregue(@Parameter(description = "ID do pedido") @PathVariable Long id) {
        Pedido pedidoAtualizado = pedidoService.marcarComoEntregue(id);
        return ResponseEntity.ok(convertToResponseDTO(pedidoAtualizado));
    }

    @PatchMapping("/pedidos/{id}/cancelar")
    @Operation(summary = "Cancelar um pedido (com motivo)")
    @ApiResponse(responseCode = "200", description = "Pedido cancelado")
    @ApiResponse(responseCode = "422", description = "Pedido já entregue não pode ser cancelado")
    public ResponseEntity<PedidoResponseDTO> cancelarPedido(
            @Parameter(description = "ID do pedido") @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        String motivo = body.get("motivo");
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new BusinessException("Motivo do cancelamento é obrigatório");
        }
        Pedido pedidoAtualizado = pedidoService.cancelarPedido(id, motivo);
        return ResponseEntity.ok(convertToResponseDTO(pedidoAtualizado));
    }

    @DeleteMapping("/pedidos/{id}")
    @Operation(summary = "Cancelar um pedido (via DELETE, sem motivo)")
    @ApiResponse(responseCode = "200", description = "Pedido cancelado")
    public ResponseEntity<PedidoResponseDTO> cancelarPedido(@Parameter(description = "ID do pedido") @PathVariable Long id) {
        Pedido pedidoAtualizado = pedidoService.cancelarPedido(id, "Cancelado pelo usuário via DELETE");
        return ResponseEntity.ok(convertToResponseDTO(pedidoAtualizado));
    }
}