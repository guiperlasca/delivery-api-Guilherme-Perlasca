package com.deliverytech.delivery.controller;

import com.deliverytech.delivery.entity.Pedido;
import com.deliverytech.delivery.entity.Pedido.StatusPedido;
import com.deliverytech.delivery.service.PedidoService;
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

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    // POST /api/pedidos - Criar pedido
    @PostMapping
    public ResponseEntity<?> criarPedido(@RequestBody Map<String, Object> body) {
        try {
            Long clienteId = Long.valueOf(body.get("clienteId").toString());
            Long restauranteId = Long.valueOf(body.get("restauranteId").toString());
            BigDecimal valorTotal = new BigDecimal(body.get("valorTotal").toString());
            String observacoes = body.get("observacoes") != null ? body.get("observacoes").toString() : null;

            Pedido pedido = pedidoService.criarPedido(clienteId, restauranteId, valorTotal, observacoes);
            return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", "Dados inválidos: " + e.getMessage()));
        }
    }

    // GET /api/pedidos - Listar todos os pedidos
    @GetMapping
    public ResponseEntity<List<Pedido>> listarTodos() {
        List<Pedido> pedidos = pedidoService.buscarTodos();
        return ResponseEntity.ok(pedidos);
    }

    // GET /api/pedidos/{id} - Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Optional<Pedido> pedido = pedidoService.buscarPorId(id);

        if (pedido.isPresent()) {
            return ResponseEntity.ok(pedido.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("erro", "Pedido não encontrado"));
    }

    // GET /api/pedidos/cliente/{clienteId} - Pedidos por cliente
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Pedido>> buscarPorCliente(@PathVariable Long clienteId) {
        List<Pedido> pedidos = pedidoService.buscarPorCliente(clienteId);
        return ResponseEntity.ok(pedidos);
    }

    // GET /api/pedidos/restaurante/{restauranteId} - Pedidos por restaurante
    @GetMapping("/restaurante/{restauranteId}")
    public ResponseEntity<List<Pedido>> buscarPorRestaurante(@PathVariable Long restauranteId) {
        List<Pedido> pedidos = pedidoService.buscarPorRestaurante(restauranteId);
        return ResponseEntity.ok(pedidos);
    }

    // GET /api/pedidos/status/{status} - Pedidos por status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Pedido>> buscarPorStatus(@PathVariable StatusPedido status) {
        List<Pedido> pedidos = pedidoService.buscarPorStatus(status);
        return ResponseEntity.ok(pedidos);
    }

    // GET /api/pedidos/em-andamento - Pedidos em andamento (cozinha)
    @GetMapping("/em-andamento")
    public ResponseEntity<List<Pedido>> buscarEmAndamento() {
        List<Pedido> pedidos = pedidoService.buscarEmAndamento();
        return ResponseEntity.ok(pedidos);
    }

    // GET /api/pedidos/hoje - Pedidos de hoje
    @GetMapping("/hoje")
    public ResponseEntity<List<Pedido>> buscarPedidosDeHoje() {
        List<Pedido> pedidos = pedidoService.buscarPedidosDeHoje();
        return ResponseEntity.ok(pedidos);
    }

    // GET /api/pedidos/periodo?inicio=2023-10-01T00:00:00&fim=2023-10-31T23:59:59
    @GetMapping("/periodo")
    public ResponseEntity<List<Pedido>> buscarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        List<Pedido> pedidos = pedidoService.buscarPorPeriodo(inicio, fim);
        return ResponseEntity.ok(pedidos);
    }

    // --- INÍCIO DAS ADIÇÕES (ATIVIDADE 3) ---

    // GET /api/pedidos/valor-acima?min=50.00
    @GetMapping("/valor-acima")
    public ResponseEntity<List<Pedido>> buscarValorAcima(@RequestParam BigDecimal min) {
        List<Pedido> pedidos = pedidoService.buscarComValorAcimaDe(min);
        return ResponseEntity.ok(pedidos);
    }

    // GET /api/pedidos/relatorio?inicio=...&fim=...&status=ENTREGUE
    @GetMapping("/relatorio")
    public ResponseEntity<List<Pedido>> buscarRelatorioPeriodoStatus(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim,
            @RequestParam StatusPedido status) {
        List<Pedido> pedidos = pedidoService.buscarPorPeriodoEStatus(inicio, fim, status);
        return ResponseEntity.ok(pedidos);
    }


    // PATCH /api/pedidos/{id}/status - Atualizar status
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> atualizarStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            StatusPedido novoStatus = StatusPedido.valueOf(body.get("status").toUpperCase());
            Pedido pedidoAtualizado = pedidoService.atualizarStatus(id, novoStatus);
            return ResponseEntity.ok(pedidoAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", "Status inválido"));
        }
    }

    // PATCH /api/pedidos/{id}/confirmar - Confirmar pedido
    @PatchMapping("/{id}/confirmar")
    public ResponseEntity<?> confirmarPedido(@PathVariable Long id) {
        try {
            Pedido pedidoAtualizado = pedidoService.confirmarPedido(id);
            return ResponseEntity.ok(pedidoAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // PATCH /api/pedidos/{id}/preparar - Iniciar preparação
    @PatchMapping("/{id}/preparar")
    public ResponseEntity<?> iniciarPreparacao(@PathVariable Long id) {
        try {
            Pedido pedidoAtualizado = pedidoService.iniciarPreparacao(id);
            return ResponseEntity.ok(pedidoAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // PATCH /api/pedidos/{id}/entregar - Marcar como entregue
    @PatchMapping("/{id}/entregar")
    public ResponseEntity<?> marcarComoEntregue(@PathVariable Long id) {
        try {
            Pedido pedidoAtualizado = pedidoService.marcarComoEntregue(id);
            return ResponseEntity.ok(pedidoAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // PATCH /api/pedidos/{id}/cancelar - Cancelar pedido
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarPedido(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            String motivo = body.get("motivo");
            Pedido pedidoAtualizado = pedidoService.cancelarPedido(id, motivo);
            return ResponseEntity.ok(pedidoAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // GET /api/pedidos/estatisticas - Estatísticas gerais
    @GetMapping("/estatisticas")
    public ResponseEntity<?> estatisticas() {
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

    // GET /api/pedidos/restaurante/{restauranteId}/total-vendido
    @GetMapping("/restaurante/{restauranteId}/total-vendido")
    public ResponseEntity<?> calcularTotalVendido(@PathVariable Long restauranteId) {
        BigDecimal total = pedidoService.calcularTotalVendidoPorRestaurante(restauranteId);
        return ResponseEntity.ok(Map.of(
                "restauranteId", restauranteId,
                "totalVendido", total,
                "timestamp", LocalDateTime.now()
        ));
    }
}