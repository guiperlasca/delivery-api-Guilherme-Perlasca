package com.deliverytech.delivery.service;

import com.deliverytech.delivery.dto.ProdutoRequestDTO;
import com.deliverytech.delivery.dto.ProdutoResponseDTO;
import com.deliverytech.delivery.entity.Produto;
import com.deliverytech.delivery.entity.Restaurante;
import com.deliverytech.delivery.exception.BusinessException;
import com.deliverytech.delivery.exception.EntityNotFoundException;
import com.deliverytech.delivery.repository.ProdutoRepository;
import com.deliverytech.delivery.repository.RestauranteRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private RestauranteRepository restauranteRepository;

    @Autowired
    private ModelMapper modelMapper;

    public Produto cadastrar(ProdutoRequestDTO produtoDTO) {
        Restaurante restaurante = restauranteRepository.findById(produtoDTO.getRestauranteId())
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado: " + produtoDTO.getRestauranteId()));

        if (!restaurante.getAtivo()) {
            throw new BusinessException("Não é possível cadastrar produto para restaurante inativo");
        }

        Produto produto = modelMapper.map(produtoDTO, Produto.class);
        produto.setRestaurante(restaurante);

        validarProduto(produto);
        return produtoRepository.save(produto);
    }

    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado: " + id));
    }

    public Produto atualizar(Long id, ProdutoRequestDTO produtoAtualizadoDTO) {
        Produto produto = buscarPorId(id); // Garante 404

        // O restaurante não pode ser alterado na atualização de um produto
        if (!produto.getRestaurante().getId().equals(produtoAtualizadoDTO.getRestauranteId())) {
            throw new BusinessException("Não é permitido alterar o restaurante de um produto.");
        }

        modelMapper.map(produtoAtualizadoDTO, produto);
        produto.setRestaurante(produto.getRestaurante()); // Garante que a entidade está associada

        validarProduto(produto);
        return produtoRepository.save(produto);
    }

    public void alterarDisponibilidade(Long id, Boolean disponivel) {
        Produto produto = buscarPorId(id);
        if (disponivel == null) {
            throw new BusinessException("Status de disponibilidade (true/false) é obrigatório.");
        }
        produto.setDisponivel(disponivel);
        produtoRepository.save(produto);
    }

    public void deletar(Long id) {
        Produto produto = buscarPorId(id); // Garante 404
        produtoRepository.delete(produto);
    }

    private void validarProduto(Produto produto) {
        // Validações do @Valid cuidam de @NotBlank, @NotNull
        if (produto.getPreco() != null && produto.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Preço deve ser maior que zero");
        }
    }

    // --- Métodos de Busca ---

    public List<Produto> buscarTodos() {
        return produtoRepository.findAll();
    }
    public List<Produto> buscarPorRestaurante(Long restauranteId) {
        return produtoRepository.findByRestauranteIdAndDisponivelTrue(restauranteId);
    }
    public List<Produto> buscarTodosPorRestaurante(Long restauranteId) {
        return produtoRepository.findByRestauranteId(restauranteId);
    }
    public List<Produto> buscarPorCategoria(String categoria) {
        return produtoRepository.findByCategoriaAndDisponivelTrue(categoria);
    }
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

    // Método Stub para Relatórios
    public List<ProdutoResponseDTO> getTopProdutosVendidos() {
        System.out.println("WARN: Chamada ao método stub getTopProdutosVendidos().");
        Optional<Produto> p1 = produtoRepository.findById(1L); // Exemplo com Big Mac
        if (p1.isPresent()) {
            ProdutoResponseDTO dto = modelMapper.map(p1.get(), ProdutoResponseDTO.class);
            dto.setRestauranteId(p1.get().getRestaurante().getId());
            return List.of(dto);
        }
        return new ArrayList<>();
    }
}