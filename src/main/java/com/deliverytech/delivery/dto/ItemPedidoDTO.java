package com.deliverytech.delivery.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

// DTO para 'Request' (Itens dentro do Pedido)
public class ItemPedidoDTO {

    @NotNull(message = "ID do Produto é obrigatório")
    private Long produtoId;

    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 1, message = "Quantidade deve ser no mínimo 1")
    private Integer quantidade;

    // Getters e Setters
    public Long getProdutoId() { return produtoId; }
    public void setProdutoId(Long produtoId) { this.produtoId = produtoId; }
    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
}