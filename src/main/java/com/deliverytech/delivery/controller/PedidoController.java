package com.deliverytech.delivery.controller;

import com.deliverytech.delivery.dto.PedidoRequestDTO;
import com.deliverytech.delivery.dto.PedidoResponseDTO;
import com.deliverytech.delivery.entity.Pedido;
import com.deliverytech.delivery.entity.Pedido.StatusPedido;
import com.deliverytech.delivery.service.PedidoService;
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
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api") // Rota base /api
@CrossOrigin(origins = "*")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ModelMapper modelMapper;

    // Mapeamento auxiliar para Entidade -> DTO Response
    private PedidoResponseDTO convertToResponseDTO(Pedido pedido) {
        PedidoResponseDTO dto = modelMapper.map(pedido, PedidoResponseDTO.class);
        dto.setClienteId(pedido.getCliente().getId());
        dto.setRestauranteId(pedido.getRestaurante().getId());
        return dto;
    }

    /**
     * POST /api/pedidos - Criar pedido
     */
    @PostMapping("/pedidos")
    public ResponseEntity<?> criarPedido(@Valid @RequestBody PedidoRequestDTO pedidoDTO) {
        try {
            Pedido pedido = pedidoService.criarPedido(pedidoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToResponseDTO(pedido));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    /**
     * POST /api/pedidos/calcular - Calcular total (sem salvar)
     */
    @PostMapping("/pedidos/calcular")
    public ResponseEntity<?> calcularTotal(@Valid @RequestBody PedidoRequestDTO pedidoDTO) {
        try {
            BigDecimal total = pedidoService.calcularTotalPedido(pedidoDTO);
            return ResponseEntity.ok(Map.of("valorTotalCalculado", total));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    /**
     * GET /api/pedidos - Listar todos os pedidos com filtros
     */
    @GetMapping("/pedidos")
    public ResponseEntity<List<PedidoResponseDTO>> listarTodos(
            @RequestParam(required = false) Pedido.StatusPedido status,
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

    /**
     * GET /api/pedidos/{id} - Buscar por ID
     */
    @GetMapping("/pedidos/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Optional<Pedido> pedido = pedidoService.buscarPorId(id);

        if (pedido.isPresent()) {
            return ResponseEntity.ok(convertToResponseDTO(pedido.get()));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("erro", "Pedido não encontrado"));
    }

    /**
     * GET /api/clientes/{clienteId}/pedidos - Histórico do cliente
     */
    @GetMapping("/clientes/{clienteId}/pedidos")
    public ResponseEntity<List<PedidoResponseDTO>> buscarPorCliente(@PathVariable Long clienteId) {
        List<Pedido> pedidos = pedidoService.buscarPorCliente(clienteId);
        List<PedidoResponseDTO> dtos = pedidos.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * GET /api/pedidos/restaurante/{restauranteId}
     * GET /api/restaurantes/{restauranteId}/pedidos (Alias de rota)
     */
    @GetMapping(value = {"/pedidos/restaurante/{restauranteId}", "/restaurantes/{restauranteId}/pedidos"})
    public ResponseEntity<List<PedidoResponseDTO>> buscarPorRestaurante(@PathVariable Long restauranteId) {
        List<Pedido> pedidos = pedidoService.buscarPorRestaurante(restauranteId);
        List<PedidoResponseDTO> dtos = pedidos.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * GET /api/pedidos/status/{status} - Pedidos por status
     */
    @GetMapping("/pedidos/status/{status}")
    public ResponseEntity<List<PedidoResponseDTO>> buscarPorStatus(@PathVariable StatusPedido status) {
        List<Pedido> pedidos = pedidoService.buscarPorStatus(status);
        List<PedidoResponseDTO> dtos = pedidos.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * GET /api/pedidos/em-andamento - Pedidos para a cozinha
     */
    @GetMapping("/pedidos/em-andamento")
    public ResponseEntity<List<PedidoResponseDTO>> buscarEmAndamento() {
        List<Pedido> pedidos = pedidoService.buscarEmAndamento();
        List<PedidoResponseDTO> dtos = pedidos.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * GET /api/pedidos/hoje - Pedidos de hoje
     */
    @GetMapping("/pedidos/hoje")
    public ResponseEntity<List<PedidoResponseDTO>> buscarPedidosDeHoje() {
        List<Pedido> pedidos = pedidoService.buscarPedidosDeHoje();
        List<PedidoResponseDTO> dtos = pedidos.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * GET /api/pedidos/periodo - Pedidos por período (Query Params)
     */
    @GetMapping("/pedidos/periodo")
    public ResponseEntity<List<PedidoResponseDTO>> buscarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        List<Pedido> pedidos = pedidoService.buscarPorPeriodo(inicio, fim);
        List<PedidoResponseDTO> dtos = pedidos.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * GET /api/pedidos/valor-acima - Pedidos com valor acima de X
     */
    @GetMapping("/pedidos/valor-acima")
    public ResponseEntity<List<PedidoResponseDTO>> buscarValorAcima(@RequestParam BigDecimal min) {
        List<Pedido> pedidos = pedidoService.buscarComValorAcimaDe(min);
        List<PedidoResponseDTO> dtos = pedidos.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /*
     * Endpoints de Relatórios movidos para RelatorioController:
     * - /api/pedidos/relatorio
     * - /api/pedidos/estatisticas
     * - /api/pedidos/restaurante/{restauranteId}/total-vendido
     */


    /**
     * PATCH /api/pedidos/{id}/status - Atualizar status genérico
     */
    @PatchMapping("/pedidos/{id}/status")
    public ResponseEntity<?> atualizarStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            StatusPedido novoStatus = StatusPedido.valueOf(body.get("status").toUpperCase());
            Pedido pedidoAtualizado = pedidoService.atualizarStatus(id, novoStatus);
            return ResponseEntity.ok(convertToResponseDTO(pedidoAtualizado));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", "Status inválido"));
        }
    }

    /**
     * PATCH /api/pedidos/{id}/confirmar - Confirma o pedido
     */
    @PatchMapping("/pedidos/{id}/confirmar")
    public ResponseEntity<?> confirmarPedido(@PathVariable Long id) {
        try {
            Pedido pedidoAtualizado = pedidoService.confirmarPedido(id);
            return ResponseEntity.ok(convertToResponseDTO(pedidoAtualizado));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    /**
     * PATCH /api/pedidos/{id}/preparar - Inicia preparação
     */
    @PatchMapping("/pedidos/{id}/preparar")
    public ResponseEntity<?> iniciarPreparacao(@PathVariable Long id) {
        try {
            Pedido pedidoAtualizado = pedidoService.iniciarPreparacao(id);
            return ResponseEntity.ok(convertToResponseDTO(pedidoAtualizado));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    /**
     * PATCH /api/pedidos/{id}/entregar - Marca como entregue
     */
    @PatchMapping("/pedidos/{id}/entregar")
    public ResponseEntity<?> marcarComoEntregue(@PathVariable Long id) {
        try {
            Pedido pedidoAtualizado = pedidoService.marcarComoEntregue(id);
            return ResponseEntity.ok(convertToResponseDTO(pedidoAtualizado));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    /**
     * PATCH /api/pedidos/{id}/cancelar - Cancela o pedido (com motivo)
     */
    @PatchMapping("/pedidos/{id}/cancelar")
    public ResponseEntity<?> cancelarPedido(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            String motivo = body.get("motivo");
            if (motivo == null || motivo.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("erro", "Motivo do cancelamento é obrigatório"));
            }
            Pedido pedidoAtualizado = pedidoService.cancelarPedido(id, motivo);
            return ResponseEntity.ok(convertToResponseDTO(pedidoAtualizado));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    /**
     * DELETE /api/pedidos/{id} - Cancela o pedido (sem motivo)
     */
    @DeleteMapping("/pedidos/{id}")
    public ResponseEntity<?> cancelarPedido(@PathVariable Long id) {
        try {
            String motivo = "Cancelado pelo usuário via DELETE";
            Pedido pedidoAtualizado = pedidoService.cancelarPedido(id, motivo);
            return ResponseEntity.ok(convertToResponseDTO(pedidoAtualizado));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", e.getMessage()));
        }
    }
}