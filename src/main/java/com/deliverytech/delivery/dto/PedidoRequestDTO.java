package com.deliverytech.delivery.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

// DTO para 'Request' (Criação de Pedido)
public class PedidoRequestDTO {

    @NotNull(message = "ID do Cliente é obrigatório")
    private Long clienteId;

    @NotNull(message = "ID do Restaurante é obrigatório")
    private Long restauranteId;

    private String observacoes;

    @Valid // Validar os itens da lista
    @NotEmpty(message = "Lista de itens não pode estar vazia")
    private List<ItemPedidoDTO> itens;

    // Getters e Setters
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    public Long getRestauranteId() { return restauranteId; }
    public void setRestauranteId(Long restauranteId) { this.restauranteId = restauranteId; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    public List<ItemPedidoDTO> getItens() { return itens; }
    public void setItens(List<ItemPedidoDTO> itens) { this.itens = itens; }
}