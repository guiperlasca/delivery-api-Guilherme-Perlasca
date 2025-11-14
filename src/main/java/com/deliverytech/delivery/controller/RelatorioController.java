package com.deliverytech.delivery.controller;

import com.deliverytech.delivery.dto.PedidoResponseDTO;
import com.deliverytech.delivery.dto.ProdutoResponseDTO;
import com.deliverytech.delivery.entity.Pedido;
import com.deliverytech.delivery.service.ClienteService;
import com.deliverytech.delivery.service.PedidoService;
import com.deliverytech.delivery.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Relatórios", description = "Endpoints para estatísticas e dados de BI")
public class RelatorioController {

    @Autowired
    private PedidoService pedidoService;
    @Autowired
    private ProdutoService produtoService;
    @Autowired
    private ClienteService clienteService;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/vendas-por-restaurante")
    @Operation(summary = "Total vendido por um restaurante")
    @ApiResponse(responseCode = "200", description = "Total calculado")
    public ResponseEntity<?> calcularTotalVendido(
            @Parameter(description = "ID do restaurante") @RequestParam Long restauranteId) {
        BigDecimal total = pedidoService.calcularTotalVendidoPorRestaurante(restauranteId);
        return ResponseEntity.ok(Map.of(
                "restauranteId", restauranteId,
                "totalVendido", total,
                "timestamp", LocalDateTime.now()
        ));
    }

    @GetMapping("/produtos-mais-vendidos")
    @Operation(summary = "Produtos mais vendidos (Simulado)")
    @ApiResponse(responseCode = "200", description = "Lista de produtos")
    public ResponseEntity<?> getTopProdutosVendidos() {
        List<ProdutoResponseDTO> topProdutos = produtoService.getTopProdutosVendidos(); // Método stub

        if (topProdutos.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "mensagem", "Lógica de top produtos ainda não implementada ou sem dados.",
                    "dica", "Esta função é um stub e retorna o produto ID 1 como exemplo."
            ));
        }
        return ResponseEntity.ok(topProdutos);
    }

    @GetMapping("/clientes-ativos")
    @Operation(summary = "Contagem total de clientes ativos")
    @ApiResponse(responseCode = "200", description = "Total de clientes")
    public ResponseEntity<?> estatisticasClientes() {
        Long totalAtivos = clienteService.contarAtivos();
        return ResponseEntity.ok(Map.of(
                "totalClientesAtivos", totalAtivos,
                "timestamp", LocalDateTime.now()
        ));
    }

    @GetMapping("/pedidos-por-periodo")
    @Operation(summary = "Relatório de pedidos por período e status (opcional)")
    @ApiResponse(responseCode = "200", description = "Lista de pedidos")
    public ResponseEntity<List<PedidoResponseDTO>> buscarPedidosPorPeriodo(
            @Parameter(description = "Data/Hora de início (YYYY-MM-DDTHH:MM:SS)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @Parameter(description = "Data/Hora de fim (YYYY-MM-DDTHH:MM:SS)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim,
            @Parameter(description = "Status (opcional)")
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

    @GetMapping("/pedidos/estatisticas")
    @Operation(summary = "Dashboard de status de pedidos")
    @ApiResponse(responseCode = "200", description = "Contagem de pedidos")
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