package com.deliverytech.delivery.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

// DTO para 'Request' (Criação/Atualização) de Restaurantes
public class RestauranteRequestDTO {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, message = "Nome deve ter no mínimo 3 caracteres")
    private String nome;

    @NotBlank(message = "Categoria é obrigatória")
    private String categoria;

    private String endereco;

    @NotNull(message = "Taxa de entrega é obrigatória")
    @DecimalMin(value = "0.0", message = "Taxa de entrega deve ser 0 ou maior")
    private BigDecimal taxaEntrega;

    @DecimalMin(value = "0.0", message = "Avaliação deve ser entre 0 e 5")
    @DecimalMax(value = "5.0", message = "Avaliação deve ser entre 0 e 5")
    private BigDecimal avaliacao;

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public BigDecimal getTaxaEntrega() { return taxaEntrega; }
    public void setTaxaEntrega(BigDecimal taxaEntrega) { this.taxaEntrega = taxaEntrega; }
    public BigDecimal getAvaliacao() { return avaliacao; }
    public void setAvaliacao(BigDecimal avaliacao) { this.avaliacao = avaliacao; }
}