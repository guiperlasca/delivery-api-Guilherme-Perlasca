package com.deliverytech.delivery.repository;

import com.deliverytech.delivery.entity.Pedido;
import com.deliverytech.delivery.entity.Pedido.StatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // Pedidos por cliente
    List<Pedido> findByClienteId(Long clienteId);

    // Pedidos por restaurante
    List<Pedido> findByRestauranteId(Long restauranteId);

    // Pedidos por status
    List<Pedido> findByStatus(StatusPedido status);

    // Pedidos por cliente e status
    List<Pedido> findByClienteIdAndStatus(Long clienteId, StatusPedido status);

    // Pedidos por período
    List<Pedido> findByDataPedidoBetween(LocalDateTime inicio, LocalDateTime fim);

    // ✅ SOLUÇÃO DEFINITIVA - Native query H2
    @Query(value = "SELECT * FROM pedidos WHERE FORMATDATETIME(data_pedido, 'yyyy-MM-dd') = FORMATDATETIME(CURRENT_TIMESTAMP, 'yyyy-MM-dd') ORDER BY data_pedido DESC", nativeQuery = true)
    List<Pedido> buscarPedidosDeHoje();

    // Pedidos por cliente ordenados por data (mais recente primeiro)
    @Query("SELECT p FROM Pedido p WHERE p.cliente.id = :clienteId ORDER BY p.dataPedido DESC")
    List<Pedido> buscarPorClienteOrdenadoPorData(@Param("clienteId") Long clienteId);

    // Total vendido por restaurante
    @Query("SELECT SUM(p.valorTotal) FROM Pedido p WHERE p.restaurante.id = :restauranteId AND p.status != 'CANCELADO'")
    BigDecimal calcularTotalVendidoPorRestaurante(@Param("restauranteId") Long restauranteId);

    // Contagem de pedidos por status
    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.status = :status")
    Long contarPorStatus(@Param("status") StatusPedido status);

    // Pedidos pendentes (para cozinha)
    @Query("SELECT p FROM Pedido p WHERE p.status IN ('PENDENTE', 'CONFIRMADO', 'PREPARANDO') ORDER BY p.dataPedido ASC")
    List<Pedido> buscarPedidosEmAndamento();
}
