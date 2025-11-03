package com.deliverytech.delivery.repository;

import com.deliverytech.delivery.entity.Restaurante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface RestauranteRepository extends JpaRepository<Restaurante, Long> {

    // Busca por nome
    List<Restaurante> findByNomeContainingIgnoreCase(String nome);

    // Busca por categoria
    List<Restaurante> findByCategoria(String categoria);

    // Busca restaurantes ativos
    List<Restaurante> findByAtivoTrue();

    // Busca por categoria e ativos
    List<Restaurante> findByCategoriaAndAtivoTrue(String categoria);

    // Busca por avaliação mínima
    List<Restaurante> findByAvaliacaoGreaterThanEqual(BigDecimal avaliacaoMinima);

    // Busca por taxa de entrega menor ou igual
    List<Restaurante> findByTaxaEntregaLessThanEqual(BigDecimal taxa);

    // Busca os 5 primeiros restaurantes ordenados por nome
    List<Restaurante> findTop5ByOrderByNomeAsc();

    // Busca ordenada por avaliação (melhor primeiro)
    @Query("SELECT r FROM Restaurante r WHERE r.ativo = true ORDER BY r.avaliacao DESC")
    List<Restaurante> buscarAtivosOrdenadosPorAvaliacao();

    // Busca por categoria ordenada por avaliação
    @Query("SELECT r FROM Restaurante r WHERE r.categoria = :categoria AND r.ativo = true ORDER BY r.avaliacao DESC")
    List<Restaurante> buscarPorCategoriaOrdenadoPorAvaliacao(@Param("categoria") String categoria);

    // Restaurantes com avaliação acima da média
    @Query("SELECT r FROM Restaurante r WHERE r.avaliacao > (SELECT AVG(rest.avaliacao) FROM Restaurante rest) AND r.ativo = true")
    List<Restaurante> buscarAcimaMediaAvaliacao();

    // Todas as categorias disponíveis
    @Query("SELECT DISTINCT r.categoria FROM Restaurante r WHERE r.ativo = true ORDER BY r.categoria")
    List<String> buscarCategoriasDisponiveis();
}