package com.deliverytech.delivery.service;

import com.deliverytech.delivery.dto.ProdutoRequestDTO; // Import
import com.deliverytech.delivery.entity.Produto;
import com.deliverytech.delivery.entity.Restaurante;
import com.deliverytech.delivery.repository.ProdutoRepository;
import com.deliverytech.delivery.repository.RestauranteRepository;
import org.modelmapper.ModelMapper; // Import
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

    @Autowired
    private ModelMapper modelMapper; // Injetar ModelMapper

    // Cadastrar produto (Recebe DTO)
    public Produto cadastrar(ProdutoRequestDTO produtoDTO) {
        // Validação do Roteiro 4: Validar restaurante existe
        Optional<Restaurante> restaurante = restauranteRepository.findById(produtoDTO.getRestauranteId());

        if (restaurante.isEmpty()) {
            throw new RuntimeException("Restaurante não encontrado: " + produtoDTO.getRestauranteId());
        }

        if (!restaurante.get().getAtivo()) {
            throw new RuntimeException("Não é possível cadastrar produto para restaurante inativo");
        }

        Produto produto = modelMapper.map(produtoDTO, Produto.class);
        produto.setRestaurante(restaurante.get()); // Associa o restaurante

        validarProduto(produto);
        return produtoRepository.save(produto);
    }

    // Buscar todos os produtos
    public List<Produto> buscarTodos() {
        return produtoRepository.findAll();
    }

    // Buscar produto por ID
    public Optional<Produto> buscarPorId(Long id) {
        return produtoRepository.findById(id);
    }

    // Buscar produtos por restaurante (apenas disponíveis)
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

    // ... (outros métodos de busca inalterados: buscarPorNome, buscarPorFaixaPreco, etc.) ...
    public List<Produto> buscarPorNome(String nome) {
        return produtoRepository.findByNomeContainingIgnoreCase(nome);
    }
    public List<Produto> buscarPorFaixaPreco(BigDecimal precoMin, BigDecimal precoMax) {
        return produtoRepository.findByPrecoBetween(precoMin, precoMax);
    }
    public List<Produto> buscarPorRestauranteECategoria(Long restauranteId, String categoria) {
        return produtoRepository.buscarPorRestauranteECategoria(restauranteId, categoria);
    }
    public List<Produto> buscarPorRestauranteOrdenadoPorPreco(Long restauranteId) {
        return produtoRepository.buscarPorRestauranteOrdenadoPorPreco(restauranteId);
    }
    public List<Produto> buscarPorPrecoMaximo(Long restauranteId, BigDecimal precoMax) {
        return produtoRepository.buscarPorRestauranteEPrecoMaximo(restauranteId, precoMax);
    }
    public List<String> buscarCategorias() {
        return produtoRepository.buscarCategoriasDisponiveis();
    }


    // Atualizar produto (Recebe DTO)
    public Produto atualizar(Long id, ProdutoRequestDTO produtoAtualizadoDTO) {
        Optional<Produto> produtoExistente = produtoRepository.findById(id);

        if (produtoExistente.isEmpty()) {
            throw new RuntimeException("Produto não encontrado: " + id);
        }

        Produto produto = produtoExistente.get();

        // Mapeia DTO para a entidade (exceto ID do restaurante, que não deve mudar)
        modelMapper.map(produtoAtualizadoDTO, produto);

        // Reforça que o restaurante não foi alterado pelo DTO
        produto.setRestaurante(produtoExistente.get().getRestaurante());

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

    // ... (métodos tornarIndisponivel, tornarDisponivel, deletar inalterados) ...
    public void tornarIndisponivel(Long id) {
        alterarDisponibilidade(id, false);
    }
    public void tornarDisponivel(Long id) {
        alterarDisponibilidade(id, true);
    }
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