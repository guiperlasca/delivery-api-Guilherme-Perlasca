package com.deliverytech.delivery.service;

import com.deliverytech.delivery.dto.ItemPedidoDTO;
import com.deliverytech.delivery.dto.PedidoRequestDTO;
import com.deliverytech.delivery.entity.Pedido;
import com.deliverytech.delivery.entity.Pedido.StatusPedido;
import com.deliverytech.delivery.entity.Cliente;
import com.deliverytech.delivery.entity.Produto;
import com.deliverytech.delivery.entity.Restaurante;
import com.deliverytech.delivery.exception.BusinessException;
import com.deliverytech.delivery.exception.EntityNotFoundException;
import com.deliverytech.delivery.repository.PedidoRepository;
import com.deliverytech.delivery.repository.ClienteRepository;
import com.deliverytech.delivery.repository.ProdutoRepository;
import com.deliverytech.delivery.repository.RestauranteRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    public Pedido criarPedido(PedidoRequestDTO pedidoDTO) {
        Cliente cliente = clienteRepository.findById(pedidoDTO.getClienteId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado: " + pedidoDTO.getClienteId()));
        if (!cliente.getAtivo()) {
            throw new BusinessException("Cliente inativo: " + cliente.getId());
        }

        Restaurante restaurante = restauranteRepository.findById(pedidoDTO.getRestauranteId())
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado: " + pedidoDTO.getRestauranteId()));
        if (!restaurante.getAtivo()) {
            throw new BusinessException("Restaurante inativo: " + restaurante.getId());
        }

        BigDecimal valorTotalItens = BigDecimal.ZERO;

        for (ItemPedidoDTO itemDTO : pedidoDTO.getItens()) {
            Produto produto = produtoRepository.findById(itemDTO.getProdutoId())
                    .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado: " + itemDTO.getProdutoId()));

            if (!produto.getDisponivel()) {
                throw new BusinessException("Produto indisponível: " + produto.getNome());
            }

            if (!produto.getRestaurante().getId().equals(restaurante.getId())) {
                throw new BusinessException("Produto '" + produto.getNome() + "' não pertence ao restaurante '" + restaurante.getNome() + "'");
            }

            valorTotalItens = valorTotalItens.add(
                    produto.getPreco().multiply(BigDecimal.valueOf(itemDTO.getQuantidade()))
            );
        }

        BigDecimal taxaEntrega = restaurante.getTaxaEntrega() != null ? restaurante.getTaxaEntrega() : BigDecimal.ZERO;
        BigDecimal valorTotalFinal = valorTotalItens.add(taxaEntrega);

        Pedido pedido = new Pedido(cliente, restaurante, valorTotalFinal);
        pedido.setObservacoes(pedidoDTO.getObservacoes());

        return pedidoRepository.save(pedido);
    }

    public BigDecimal calcularTotalPedido(PedidoRequestDTO pedidoDTO) {
        Restaurante restaurante = restauranteRepository.findById(pedidoDTO.getRestauranteId())
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado: " + pedidoDTO.getRestauranteId()));

        BigDecimal valorTotalItens = BigDecimal.ZERO;
        for (ItemPedidoDTO itemDTO : pedidoDTO.getItens()) {
            Produto produto = produtoRepository.findById(itemDTO.getProdutoId())
                    .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado: " + itemDTO.getProdutoId()));

            if (!produto.getDisponivel()) {
                throw new BusinessException("Produto indisponível: " + produto.getNome());
            }
            if (!produto.getRestaurante().getId().equals(restaurante.getId())) {
                throw new BusinessException("Produto '" + produto.getNome() + "' não pertence ao restaurante.");
            }

            valorTotalItens = valorTotalItens.add(
                    produto.getPreco().multiply(BigDecimal.valueOf(itemDTO.getQuantidade()))
            );
        }

        BigDecimal taxaEntrega = restaurante.getTaxaEntrega() != null ? restaurante.getTaxaEntrega() : BigDecimal.ZERO;
        return valorTotalItens.add(taxaEntrega);
    }

    public Pedido buscarPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado: " + id));
    }

    public Pedido atualizarStatus(Long id, StatusPedido novoStatus) {
        Pedido pedido = buscarPorId(id);
        validarTransicaoStatus(pedido.getStatus(), novoStatus);
        pedido.setStatus(novoStatus);
        return pedidoRepository.save(pedido);
    }

    public Pedido confirmarPedido(Long id) {
        return atualizarStatus(id, StatusPedido.CONFIRMADO);
    }
    public Pedido iniciarPreparacao(Long id) {
        return atualizarStatus(id, StatusPedido.PREPARANDO);
    }
    public Pedido marcarComoEntregue(Long id) {
        return atualizarStatus(id, StatusPedido.ENTREGUE);
    }

    public Pedido cancelarPedido(Long id, String motivo) {
        Pedido pedido = buscarPorId(id);

        if (pedido.getStatus() == StatusPedido.ENTREGUE) {
            throw new BusinessException("Não é possível cancelar pedido já entregue");
        }
        if (pedido.getStatus() == StatusPedido.CANCELADO) {
            return pedido; // Já está cancelado
        }

        pedido.setStatus(StatusPedido.CANCELADO);
        String obs = pedido.getObservacoes() != null ? pedido.getObservacoes() : "";
        pedido.setObservacoes(obs + " | CANCELADO: " + (motivo != null ? motivo : "Motivo não informado"));

        return pedidoRepository.save(pedido);
    }

    private void validarTransicaoStatus(StatusPedido statusAtual, StatusPedido novoStatus) {
        if (statusAtual == StatusPedido.PENDENTE) {
            if (novoStatus != StatusPedido.CONFIRMADO && novoStatus != StatusPedido.CANCELADO) {
                throw new BusinessException("Transição inválida: PENDENTE só pode ir para CONFIRMADO ou CANCELADO");
            }
        }
        if (statusAtual == StatusPedido.CONFIRMADO) {
            if (novoStatus != StatusPedido.PREPARANDO && novoStatus != StatusPedido.CANCELADO) {
                throw new BusinessException("Transição inválida: CONFIRMADO só pode ir para PREPARANDO ou CANCELADO");
            }
        }
        if (statusAtual == StatusPedido.PREPARANDO) {
            if (novoStatus != StatusPedido.ENTREGUE && novoStatus != StatusPedido.CANCELADO) {
                throw new BusinessException("Transição inválida: PREPARANDO só pode ir para ENTREGUE ou CANCELADO");
            }
        }
        if (statusAtual == StatusPedido.ENTREGUE || statusAtual == StatusPedido.CANCELADO) {
            throw new BusinessException("Não é possível alterar status de pedido " + statusAtual);
        }
    }

    // --- Métodos de Busca e Relatório ---

    public List<Pedido> buscarTodos() {
        return pedidoRepository.findAll();
    }
    public List<Pedido> buscarPorCliente(Long clienteId) {
        return pedidoRepository.buscarPorClienteOrdenadoPorData(clienteId);
    }
    public List<Pedido> buscarPorRestaurante(Long restauranteId) {
        return pedidoRepository.findByRestauranteId(restauranteId);
    }
    public List<Pedido> buscarPorStatus(StatusPedido status) {
        return pedidoRepository.findByStatus(status);
    }
    public List<Pedido> buscarEmAndamento() {
        return pedidoRepository.buscarPedidosEmAndamento();
    }
    public List<Pedido> buscarPedidosDeHoje() {
        return pedidoRepository.buscarPedidosDeHoje();
    }
    public List<Pedido> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return pedidoRepository.findByDataPedidoBetween(inicio, fim);
    }
    public List<Pedido> buscarComValorAcimaDe(BigDecimal valorMinimo) {
        return pedidoRepository.buscarPedidosComValorAcimaDe(valorMinimo);
    }
    public List<Pedido> buscarPorPeriodoEStatus(LocalDateTime inicio, LocalDateTime fim, StatusPedido status) {
        return pedidoRepository.buscarPorPeriodoEStatus(inicio, fim, status);
    }
    public BigDecimal calcularTotalVendidoPorRestaurante(Long restauranteId) {
        BigDecimal total = pedidoRepository.calcularTotalVendidoPorRestaurante(restauranteId);
        return total != null ? total : BigDecimal.ZERO;
    }
    public Long contarPorStatus(StatusPedido status) {
        return pedidoRepository.contarPorStatus(status);
    }
    public Long contarPendentes() {
        return contarPorStatus(StatusPedido.PENDENTE);
    }
    public Long contarEmAndamento() {
        return contarPorStatus(StatusPedido.CONFIRMADO) + contarPorStatus(StatusPedido.PREPARANDO);
    }
    public Long contarEntregues() {
        return contarPorStatus(StatusPedido.ENTREGUE);
    }
}