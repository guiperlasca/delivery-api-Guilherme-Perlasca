package com.deliverytech.delivery.service;

import com.deliverytech.delivery.entity.Produto;
import com.deliverytech.delivery.entity.Restaurante;
import com.deliverytech.delivery.repository.ProdutoRepository;
import com.deliverytech.delivery.repository.RestauranteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private RestauranteRepository restauranteRepository;

    // Cadastrar produto
    public Produto cadastrar(Produto produto) {
        validarProduto(produto);

        // Verificar se restaurante existe e está ativo
        Optional<Restaurante> restaurante = restauranteRepository.findById(produto.getRestaurante().getId());

        if (restaurante.isEmpty()) {
            throw new RuntimeException("Restaurante não encontrado: " + produto.getRestaurante().getId());
        }

        if (!restaurante.get().getAtivo()) {
            throw new RuntimeException("Não é possível cadastrar produto para restaurante inativo");
        }

        return produtoRepository.save(produto);
    }

    // Buscar todos os produtos disponíveis
    public List<Produto> buscarTodos() {
        return produtoRepository.findAll();
    }

    // Buscar produto por ID
    public Optional<Produto> buscarPorId(Long id) {
        return produtoRepository.findById(id);
    }

    // Buscar produtos por restaurante
    public List<Produto> buscarPorRestaurante(Long restauranteId) {
        return produtoRepository.findByRestauranteIdAndDisponivelTrue(restauranteId);
    }

    // Buscar todos os produtos de um restaurante (incluindo indisponíveis)
    public List<Produto> buscarTodosPorRestaurante(Long restauranteId) {
        return produtoRepository.findByRestauranteId(restauranteId);
    }

    // Buscar por categoria
    public List<Produto> buscarPorCategoria(String categoria) {
        return produtoRepository.findByCategoriaAndDisponivelTrue(categoria);
    }

    // Buscar por nome (contém)
    public List<Produto> buscarPorNome(String nome) {
        return produtoRepository.findByNomeContainingIgnoreCase(nome);
    }

    // Buscar por faixa de preço
    public List<Produto> buscarPorFaixaPreco(BigDecimal precoMin, BigDecimal precoMax) {
        return produtoRepository.findByPrecoBetween(precoMin, precoMax);
    }

    // Buscar produtos de restaurante por categoria
    public List<Produto> buscarPorRestauranteECategoria(Long restauranteId, String categoria) {
        return produtoRepository.buscarPorRestauranteECategoria(restauranteId, categoria);
    }

    // Buscar produtos ordenados por preço (mais barato primeiro)
    public List<Produto> buscarPorRestauranteOrdenadoPorPreco(Long restauranteId) {
        return produtoRepository.buscarPorRestauranteOrdenadoPorPreco(restauranteId);
    }

    // Buscar por preço máximo
    public List<Produto> buscarPorPrecoMaximo(Long restauranteId, BigDecimal precoMax) {
        return produtoRepository.buscarPorRestauranteEPrecoMaximo(restauranteId, precoMax);
    }

    // Buscar categorias disponíveis
    public List<String> buscarCategorias() {
        return produtoRepository.buscarCategoriasDisponiveis();
    }

    // Atualizar produto
    public Produto atualizar(Long id, Produto produtoAtualizado) {
        Optional<Produto> produtoExistente = produtoRepository.findById(id);

        if (produtoExistente.isEmpty()) {
            throw new RuntimeException("Produto não encontrado: " + id);
        }

        Produto produto = produtoExistente.get();

        // Atualizar campos (não permitir mudança de restaurante)
        produto.setNome(produtoAtualizado.getNome());
        produto.setDescricao(produtoAtualizado.getDescricao());
        produto.setPreco(produtoAtualizado.getPreco());
        produto.setCategoria(produtoAtualizado.getCategoria());

        validarProduto(produto);

        return produtoRepository.save(produto);
    }

    // Alterar disponibilidade
    public void alterarDisponibilidade(Long id, Boolean disponivel) {
        Optional<Produto> produto = produtoRepository.findById(id);

        if (produto.isEmpty()) {
            throw new RuntimeException("Produto não encontrado: " + id);
        }

        Produto produtoEntity = produto.get();
        produtoEntity.setDisponivel(disponivel);
        produtoRepository.save(produtoEntity);
    }

    // Tornar indisponível (soft delete)
    public void tornarIndisponivel(Long id) {
        alterarDisponibilidade(id, false);
    }

    // Tornar disponível
    public void tornarDisponivel(Long id) {
        alterarDisponibilidade(id, true);
    }

    // Deletar produto (hard delete)
    public void deletar(Long id) {
        if (!produtoRepository.existsById(id)) {
            throw new RuntimeException("Produto não encontrado: " + id);
        }
        produtoRepository.deleteById(id);
    }

    // Validações privadas
    private void validarProduto(Produto produto) {
        if (produto.getNome() == null || produto.getNome().trim().isEmpty()) {
            throw new RuntimeException("Nome do produto é obrigatório");
        }

        if (produto.getPreco() == null) {
            throw new RuntimeException("Preço é obrigatório");
        }

        if (produto.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Preço deve ser maior que zero");
        }

        if (produto.getCategoria() == null || produto.getCategoria().trim().isEmpty()) {
            throw new RuntimeException("Categoria é obrigatória");
        }

        if (produto.getRestaurante() == null || produto.getRestaurante().getId() == null) {
            throw new RuntimeException("Restaurante é obrigatório");
        }
    }
}
