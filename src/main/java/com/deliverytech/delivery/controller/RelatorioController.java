package com.deliverytech.delivery.controller;

import com.deliverytech.delivery.dto.ClienteResponseDTO;
import com.deliverytech.delivery.dto.PedidoResponseDTO;
import com.deliverytech.delivery.dto.ProdutoResponseDTO;
import com.deliverytech.delivery.entity.Pedido;
import com.deliverytech.delivery.service.ClienteService;
import com.deliverytech.delivery.service.PedidoService;
import com.deliverytech.delivery.service.ProdutoService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/relatorios")
@CrossOrigin(origins = "*")
public class RelatorioController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Endpoint: GET /api/relatorios/vendas-por-restaurante
     */
    @GetMapping("/vendas-por-restaurante")
    public ResponseEntity<?> calcularTotalVendido(@RequestParam Long restauranteId) {
        BigDecimal total = pedidoService.calcularTotalVendidoPorRestaurante(restauranteId);
        return ResponseEntity.ok(Map.of(
                "restauranteId", restauranteId,
                "totalVendido", total,
                "timestamp", LocalDateTime.now()
        ));
    }

    /**
     * Endpoint: GET /api/relatorios/produtos-mais-vendidos
     * Roteiro 5, Atividade 1.4 [cite: 93]

     */
    @GetMapping("/produtos-mais-vendidos")
    public ResponseEntity<?> getTopProdutosVendidos() {
        List<ProdutoResponseDTO> topProdutos = produtoService.getTopProdutosVendidos();

        if (topProdutos.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "mensagem", "Lógica de top produtos ainda não implementada ou sem dados.",
                    "dica", "Isso requer salvar os itens de cada pedido no banco de dados."
            ));
        }
        return ResponseEntity.ok(topProdutos);
    }

    /**
     * Endpoint: GET /api/relatorios/clientes-ativos
     */
    @GetMapping("/clientes-ativos")
    public ResponseEntity<?> estatisticasClientes() {
        Long totalAtivos = clienteService.contarAtivos();
        return ResponseEntity.ok(Map.of(
                "totalClientesAtivos", totalAtivos,
                "timestamp", LocalDateTime.now()
        ));
    }

    /**
     * Endpoint: GET /api/relatorios/pedidos-por-periodo
     */
    @GetMapping("/pedidos-por-periodo")
    public ResponseEntity<List<PedidoResponseDTO>> buscarPedidosPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim,
            @RequestParam(required = false) Pedido.StatusPedido status) {

        List<Pedido> pedidos;
        if (status != null) {
            pedidos = pedidoService.buscarPorPeriodoEStatus(inicio, fim, status);
        } else {
            pedidos = pedidoService.buscarPorPeriodo(inicio, fim);
        }

        List<PedidoResponseDTO> dtos = pedidos.stream()
                .map(pedido -> modelMapper.map(pedido, PedidoResponseDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Endpoint extra (movido do PedidoController) para estatísticas de status
     */
    @GetMapping("/pedidos/estatisticas")
    public ResponseEntity<?> estatisticasPedidos() {
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
}