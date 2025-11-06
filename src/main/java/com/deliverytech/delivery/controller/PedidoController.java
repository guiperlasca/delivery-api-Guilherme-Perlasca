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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api") // Modificado para suportar endpoints
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

    // POST /api/pedidos - Criar pedido (Usa DTO e @Valid)
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

    // POST /api/pedidos/calcular - Calcular total (sem salvar)
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

    // GET /api/pedidos - Listar todos os pedidos
    @GetMapping("/pedidos")
    public ResponseEntity<List<PedidoResponseDTO>> listarTodos() {
        List<Pedido> pedidos = pedidoService.buscarTodos();
        List<PedidoResponseDTO> dtos = pedidos.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // GET /api/pedidos/{id} - Buscar por ID
    @GetMapping("/pedidos/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Optional<Pedido> pedido = pedidoService.buscarPorId(id);

        if (pedido.isPresent()) {
            return ResponseEntity.ok(convertToResponseDTO(pedido.get()));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("erro", "Pedido não encontrado"));
    }

    // GET /api/clientes/{clienteId}/pedidos - Histórico do cliente
    @GetMapping("/clientes/{clienteId}/pedidos")
    public ResponseEntity<List<PedidoResponseDTO>> buscarPorCliente(@PathVariable Long clienteId) {
        List<Pedido> pedidos = pedidoService.buscarPorCliente(clienteId);
        List<PedidoResponseDTO> dtos = pedidos.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // GET /api/pedidos/restaurante/{restauranteId} - Pedidos por restaurante
    @GetMapping("/pedidos/restaurante/{restauranteId}")
    public ResponseEntity<List<PedidoResponseDTO>> buscarPorRestaurante(@PathVariable Long restauranteId) {
        List<Pedido> pedidos = pedidoService.buscarPorRestaurante(restauranteId);
        List<PedidoResponseDTO> dtos = pedidos.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // GET /api/pedidos/status/{status} - Pedidos por status
    @GetMapping("/pedidos/status/{status}")
    public ResponseEntity<List<PedidoResponseDTO>> buscarPorStatus(@PathVariable StatusPedido status) {
        List<Pedido> pedidos = pedidoService.buscarPorStatus(status);
        List<PedidoResponseDTO> dtos = pedidos.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }


    @GetMapping("/pedidos/em-andamento")
    public ResponseEntity<List<PedidoResponseDTO>> buscarEmAndamento() {
        List<Pedido> pedidos = pedidoService.buscarEmAndamento();
        List<PedidoResponseDTO> dtos = pedidos.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    @GetMapping("/pedidos/hoje")
    public ResponseEntity<List<PedidoResponseDTO>> buscarPedidosDeHoje() {
        List<Pedido> pedidos = pedidoService.buscarPedidosDeHoje();
        List<PedidoResponseDTO> dtos = pedidos.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
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
    @GetMapping("/pedidos/valor-acima")
    public ResponseEntity<List<PedidoResponseDTO>> buscarValorAcima(@RequestParam BigDecimal min) {
        List<Pedido> pedidos = pedidoService.buscarComValorAcimaDe(min);
        List<PedidoResponseDTO> dtos = pedidos.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    @GetMapping("/pedidos/relatorio")
    public ResponseEntity<List<PedidoResponseDTO>> buscarRelatorioPeriodoStatus(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim,
            @RequestParam StatusPedido status) {
        List<Pedido> pedidos = pedidoService.buscarPorPeriodoEStatus(inicio, fim, status);
        List<PedidoResponseDTO> dtos = pedidos.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // PATCH /api/pedidos/{id}/status - Atualizar status
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
    @PatchMapping("/pedidos/{id}/cancelar")
    public ResponseEntity<?> cancelarPedido(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            String motivo = body.get("motivo");
            Pedido pedidoAtualizado = pedidoService.cancelarPedido(id, motivo);
            return ResponseEntity.ok(convertToResponseDTO(pedidoAtualizado));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", e.getMessage()));
        }
    }
    @GetMapping("/pedidos/estatisticas")
    public ResponseEntity<?> estatisticas() {
        // ... (Inalterado) ...
        Long pendentes = pedidoService.contarPendentes();
        Long emAndamento = pedidoService.contarEmAndamento();
        Long entregues = pedidoService.contarEntregues();

        return ResponseEntity.ok(Map.of(
                "pedidosPendentes", pendentes,
                "pedidosEmAndamento", emAndamento,
                "pedidosEntregues", entregues,
                "timestamp", LocalDateTime.now()
        ));
    }
    @GetMapping("/pedidos/restaurante/{restauranteId}/total-vendido")
    public ResponseEntity<?> calcularTotalVendido(@PathVariable Long restauranteId) {
        // ... (Inalterado) ...
        BigDecimal total = pedidoService.calcularTotalVendidoPorRestaurante(restauranteId);
        return ResponseEntity.ok(Map.of(
                "restauranteId", restauranteId,
                "totalVendido", total,
                "timestamp", LocalDateTime.now()
        ));
    }
}