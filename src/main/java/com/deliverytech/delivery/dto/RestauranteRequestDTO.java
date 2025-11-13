package com.deliverytech.delivery.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Schema(description = "DTO para criar ou atualizar um Restaurante")
public class RestauranteRequestDTO {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, message = "Nome deve ter no mínimo 3 caracteres")
    @Schema(description = "Nome do restaurante", example = "Pizza Express")
    private String nome;

    @NotBlank(message = "Categoria é obrigatória")
    @Schema(description = "Categoria principal do restaurante", example = "Italiana")
    private String categoria;

    @Schema(description = "Endereço completo do restaurante", example = "Rua das Pizzas, 123")
    private String endereco;

    @NotNull(message = "Taxa de entrega é obrigatória")
    @DecimalMin(value = "0.0", message = "Taxa de entrega deve ser 0 ou maior")
    @Schema(description = "Valor da taxa de entrega", example = "5.00")
    private BigDecimal taxaEntrega;

    @DecimalMin(value = "0.0", message = "Avaliação deve ser entre 0 e 5")
    @DecimalMax(value = "5.0", message = "Avaliação deve ser entre 0 e 5")
    @Schema(description = "Avaliação média do restaurante (0.0 a 5.0)", example = "4.5")
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