package com.deliverytech.delivery.repository;

import com.deliverytech.delivery.entity.Produto;
import com.deliverytech.delivery.entity.Restaurante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    // Busca produtos por restaurante
    List<Produto> findByRestaurante(Restaurante restaurante);

    // Busca produtos por restaurante ID
    List<Produto> findByRestauranteId(Long restauranteId);

    // Busca produtos disponíveis por restaurante
    List<Produto> findByRestauranteIdAndDisponivelTrue(Long restauranteId);

    // Busca por categoria
    List<Produto> findByCategoria(String categoria);

    // Busca por categoria e disponível
    List<Produto> findByCategoriaAndDisponivelTrue(String categoria);

    // Busca por nome (contém)
    List<Produto> findByNomeContainingIgnoreCase(String nome);

    // Busca por faixa de preço
    List<Produto> findByPrecoBetween(BigDecimal precoMin, BigDecimal precoMax);

    // Produtos de um restaurante por categoria
    @Query("SELECT p FROM Produto p WHERE p.restaurante.id = :restauranteId AND p.categoria = :categoria AND p.disponivel = true")
    List<Produto> buscarPorRestauranteECategoria(@Param("restauranteId") Long restauranteId, @Param("categoria") String categoria);

    // Produtos mais baratos primeiro por restaurante
    @Query("SELECT p FROM Produto p WHERE p.restaurante.id = :restauranteId AND p.disponivel = true ORDER BY p.preco ASC")
    List<Produto> buscarPorRestauranteOrdenadoPorPreco(@Param("restauranteId") Long restauranteId);

    // Busca produtos por restaurante e preço máximo
    @Query("SELECT p FROM Produto p WHERE p.restaurante.id = :restauranteId AND p.preco <= :precoMax AND p.disponivel = true ORDER BY p.preco ASC")
    List<Produto> buscarPorRestauranteEPrecoMaximo(@Param("restauranteId") Long restauranteId, @Param("precoMax") BigDecimal precoMax);

    // Todas as categorias de produtos disponíveis
    @Query("SELECT DISTINCT p.categoria FROM Produto p WHERE p.disponivel = true ORDER BY p.categoria")
    List<String> buscarCategoriasDisponiveis();
}
