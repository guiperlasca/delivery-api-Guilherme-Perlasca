package com.deliverytech.delivery.repository;

import com.deliverytech.delivery.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Busca por email (único)
    Optional<Cliente> findByEmail(String email);

    // Busca clientes ativos
    List<Cliente> findByAtivoTrue();

    // Busca clientes inativos
    List<Cliente> findByAtivoFalse();

    // Busca por nome (contém)
    List<Cliente> findByNomeContainingIgnoreCase(String nome);

    // Busca por telefone
    Optional<Cliente> findByTelefone(String telefone);

    // Verifica se email já existe (para validação)
    boolean existsByEmail(String email);

    // Busca com query customizada
    @Query("SELECT c FROM Cliente c WHERE c.ativo = :ativo ORDER BY c.dataCriacao DESC")
    List<Cliente> buscarPorStatusOrdenadoPorData(@Param("ativo") Boolean ativo);

    // Contagem de clientes ativos
    @Query("SELECT COUNT(c) FROM Cliente c WHERE c.ativo = true")
    Long contarClientesAtivos();
}
