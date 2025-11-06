package com.deliverytech.delivery.dto;

import com.deliverytech.delivery.entity.Pedido;
import java.math.BigDecimal;
import java.time.LocalDateTime;

// DTO para 'Response' (Retorno de Pedido)
public class PedidoResponseDTO {

    private Long id;
    private Long clienteId;
    private Long restauranteId;
    private BigDecimal valorTotal;
    private Pedido.StatusPedido status;
    private LocalDateTime dataPedido;
    private String observacoes;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    public Long getRestauranteId() { return restauranteId; }
    public void setRestauranteId(Long restauranteId) { this.restauranteId = restauranteId; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
    public Pedido.StatusPedido getStatus() { return status; }
    public void setStatus(Pedido.StatusPedido status) { this.status = status; }
    public LocalDateTime getDataPedido() { return dataPedido; }
    public void setDataPedido(LocalDateTime dataPedido) { this.dataPedido = dataPedido; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}