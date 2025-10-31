package com.deliverytech.delivery.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "restaurantes")
public class Restaurante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(length = 50)
    private String categoria;

    @Column(length = 200)
    private String endereco;

    @Column(precision = 3, scale = 2)
    private BigDecimal avaliacao;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    // Construtores
    public Restaurante() {}

    public Restaurante(String nome, String categoria, String endereco) {
        this.nome = nome;
        this.categoria = categoria;
        this.endereco = endereco;
        this.avaliacao = BigDecimal.valueOf(5.0);
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public BigDecimal getAvaliacao() { return avaliacao; }
    public void setAvaliacao(BigDecimal avaliacao) { this.avaliacao = avaliacao; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
}
