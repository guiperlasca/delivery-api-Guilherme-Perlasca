package com.deliverytech.delivery.service;

import com.deliverytech.delivery.entity.Pedido;
import com.deliverytech.delivery.entity.Pedido.StatusPedido;
import com.deliverytech.delivery.entity.Cliente;
import com.deliverytech.delivery.entity.Restaurante;
import com.deliverytech.delivery.repository.PedidoRepository;
import com.deliverytech.delivery.repository.ClienteRepository;
import com.deliverytech.delivery.repository.RestauranteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private RestauranteRepository restauranteRepository;

    // Criar pedido
    public Pedido criarPedido(Long clienteId, Long restauranteId, BigDecimal valorTotal, String observacoes) {
        // Validar cliente
        Optional<Cliente> cliente = clienteRepository.findById(clienteId);
        if (cliente.isEmpty() || !cliente.get().getAtivo()) {
            throw new RuntimeException("Cliente não encontrado ou inativo: " + clienteId);
        }

        // Validar restaurante
        Optional<Restaurante> restaurante = restauranteRepository.findById(restauranteId);
        if (restaurante.isEmpty() || !restaurante.get().getAtivo()) {
            throw new RuntimeException("Restaurante não encontrado ou inativo: " + restauranteId);
        }

        // Validar valor
        if (valorTotal == null || valorTotal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Valor total deve ser maior que zero");
        }

        // Criar pedido
        Pedido pedido = new Pedido(cliente.get(), restaurante.get(), valorTotal);
        pedido.setObservacoes(observacoes);

        return pedidoRepository.save(pedido);
    }

    // Buscar todos os pedidos
    public List<Pedido> buscarTodos() {
        return pedidoRepository.findAll();
    }

    // Buscar pedido por ID
    public Optional<Pedido> buscarPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    // Buscar pedidos por cliente
    public List<Pedido> buscarPorCliente(Long clienteId) {
        return pedidoRepository.buscarPorClienteOrdenadoPorData(clienteId);
    }

    // Buscar pedidos por restaurante
    public List<Pedido> buscarPorRestaurante(Long restauranteId) {
        return pedidoRepository.findByRestauranteId(restauranteId);
    }

    // Buscar por status
    public List<Pedido> buscarPorStatus(StatusPedido status) {
        return pedidoRepository.findByStatus(status);
    }

    // Buscar pedidos em andamento (para cozinha)
    public List<Pedido> buscarEmAndamento() {
        return pedidoRepository.buscarPedidosEmAndamento();
    }

    // Buscar pedidos de hoje
    public List<Pedido> buscarPedidosDeHoje() {
        return pedidoRepository.buscarPedidosDeHoje();
    }

    // Buscar por período
    public List<Pedido> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return pedidoRepository.findByDataPedidoBetween(inicio, fim);
    }

    // Busca pedidos com valor acima de X
    public List<Pedido> buscarComValorAcimaDe(BigDecimal valorMinimo) {
        return pedidoRepository.buscarPedidosComValorAcimaDe(valorMinimo);
    }

    // Busca por período e status
    public List<Pedido> buscarPorPeriodoEStatus(LocalDateTime inicio, LocalDateTime fim, StatusPedido status) {
        return pedidoRepository.buscarPorPeriodoEStatus(inicio, fim, status);
    }

    // Atualizar status do pedido
    public Pedido atualizarStatus(Long id, StatusPedido novoStatus) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(id);

        if (pedidoOpt.isEmpty()) {
            throw new RuntimeException("Pedido não encontrado: " + id);
        }

        Pedido pedido = pedidoOpt.get();

        // Validar transição de status
        validarTransicaoStatus(pedido.getStatus(), novoStatus);

        pedido.setStatus(novoStatus);
        return pedidoRepository.save(pedido);
    }

    // Confirmar pedido
    public Pedido confirmarPedido(Long id) {
        return atualizarStatus(id, StatusPedido.CONFIRMADO);
    }

    // Iniciar preparação
    public Pedido iniciarPreparacao(Long id) {
        return atualizarStatus(id, StatusPedido.PREPARANDO);
    }

    // Marcar como entregue
    public Pedido marcarComoEntregue(Long id) {
        return atualizarStatus(id, StatusPedido.ENTREGUE);
    }

    // Cancelar pedido
    public Pedido cancelarPedido(Long id, String motivo) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(id);

        if (pedidoOpt.isEmpty()) {
            throw new RuntimeException("Pedido não encontrado: " + id);
        }

        Pedido pedido = pedidoOpt.get();

        // Não permitir cancelamento se já entregue
        if (pedido.getStatus() == StatusPedido.ENTREGUE) {
            throw new RuntimeException("Não é possível cancelar pedido já entregue");
        }

        pedido.setStatus(StatusPedido.CANCELADO);
        pedido.setObservacoes(pedido.getObservacoes() + " | CANCELADO: " + motivo);

        return pedidoRepository.save(pedido);
    }

    // Calcular total vendido por restaurante
    public BigDecimal calcularTotalVendidoPorRestaurante(Long restauranteId) {
        BigDecimal total = pedidoRepository.calcularTotalVendidoPorRestaurante(restauranteId);
        return total != null ? total : BigDecimal.ZERO;
    }

    // Contar pedidos por status
    public Long contarPorStatus(StatusPedido status) {
        return pedidoRepository.contarPorStatus(status);
    }

    // Estatísticas básicas
    public Long contarPendentes() {
        return contarPorStatus(StatusPedido.PENDENTE);
    }

    public Long contarEmAndamento() {
        return contarPorStatus(StatusPedido.CONFIRMADO) + contarPorStatus(StatusPedido.PREPARANDO);
    }

    public Long contarEntregues() {
        return contarPorStatus(StatusPedido.ENTREGUE);
    }

    // Validação de transição de status
    private void validarTransicaoStatus(StatusPedido statusAtual, StatusPedido novoStatus) {
        // PENDENTE -> CONFIRMADO, CANCELADO
        if (statusAtual == StatusPedido.PENDENTE) {
            if (novoStatus != StatusPedido.CONFIRMADO && novoStatus != StatusPedido.CANCELADO) {
                throw new RuntimeException("Transição inválida: PENDENTE só pode ir para CONFIRMADO ou CANCELADO");
            }
        }

        // CONFIRMADO -> PREPARANDO, CANCELADO
        if (statusAtual == StatusPedido.CONFIRMADO) {
            if (novoStatus != StatusPedido.PREPARANDO && novoStatus != StatusPedido.CANCELADO) {
                throw new RuntimeException("Transição inválida: CONFIRMADO só pode ir para PREPARANDO ou CANCELADO");
            }
        }

        // PREPARANDO -> ENTREGUE, CANCELADO
        if (statusAtual == StatusPedido.PREPARANDO) {
            if (novoStatus != StatusPedido.ENTREGUE && novoStatus != StatusPedido.CANCELADO) {
                throw new RuntimeException("Transição inválida: PREPARANDO só pode ir para ENTREGUE ou CANCELADO");
            }
        }

        // ENTREGUE e CANCELADO são finais
        if (statusAtual == StatusPedido.ENTREGUE || statusAtual == StatusPedido.CANCELADO) {
            throw new RuntimeException("Não é possível alterar status de pedido " + statusAtual);
        }
    }
}
