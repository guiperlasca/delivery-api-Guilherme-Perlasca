package com.deliverytech.delivery.service;

import com.deliverytech.delivery.entity.Restaurante;
import com.deliverytech.delivery.repository.RestauranteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class RestauranteService {

    @Autowired
    private RestauranteRepository restauranteRepository;

    // Cadastrar restaurante
    public Restaurante cadastrar(Restaurante restaurante) {
        validarRestaurante(restaurante);

        // Definir avaliação inicial se não informada
        if (restaurante.getAvaliacao() == null) {
            restaurante.setAvaliacao(BigDecimal.valueOf(5.0));
        }

        return restauranteRepository.save(restaurante);
    }

    // Buscar todos os restaurantes ativos
    public List<Restaurante> buscarTodos() {
        return restauranteRepository.findByAtivoTrue();
    }

    // Buscar restaurante por ID
    public Optional<Restaurante> buscarPorId(Long id) {
        return restauranteRepository.findById(id);
    }

    // Buscar por nome
    public List<Restaurante> buscarPorNome(String nome) {
        return restauranteRepository.findByNomeContainingIgnoreCase(nome);
    }

    // Buscar por categoria
    public List<Restaurante> buscarPorCategoria(String categoria) {
        return restauranteRepository.findByCategoriaAndAtivoTrue(categoria);
    }

    // Buscar ordenados por avaliação (melhores primeiro)
    public List<Restaurante> buscarOrdenadosPorAvaliacao() {
        return restauranteRepository.buscarAtivosOrdenadosPorAvaliacao();
    }

    // Buscar restaurantes acima da média
    public List<Restaurante> buscarAcimaMedia() {
        return restauranteRepository.buscarAcimaMediaAvaliacao();
    }

    // Buscar por avaliação mínima
    public List<Restaurante> buscarPorAvaliacaoMinima(Double avaliacaoMinima) {
        return restauranteRepository.findByAvaliacaoGreaterThanEqual(BigDecimal.valueOf(avaliacaoMinima));
    }

    // Buscar categorias disponíveis
    public List<String> buscarCategorias() {
        return restauranteRepository.buscarCategoriasDisponiveis();
    }

    // Atualizar restaurante
    public Restaurante atualizar(Long id, Restaurante restauranteAtualizado) {
        Optional<Restaurante> restauranteExistente = restauranteRepository.findById(id);

        if (restauranteExistente.isEmpty()) {
            throw new RuntimeException("Restaurante não encontrado: " + id);
        }

        Restaurante restaurante = restauranteExistente.get();

        // Atualizar campos
        restaurante.setNome(restauranteAtualizado.getNome());
        restaurante.setCategoria(restauranteAtualizado.getCategoria());
        restaurante.setEndereco(restauranteAtualizado.getEndereco());

        // Manter avaliação se não informada
        if (restauranteAtualizado.getAvaliacao() != null) {
            restaurante.setAvaliacao(restauranteAtualizado.getAvaliacao());
        }

        validarRestaurante(restaurante);

        return restauranteRepository.save(restaurante);
    }

    // Atualizar avaliação
    public Restaurante atualizarAvaliacao(Long id, Double novaAvaliacao) {
        Optional<Restaurante> restaurante = restauranteRepository.findById(id);

        if (restaurante.isEmpty()) {
            throw new RuntimeException("Restaurante não encontrado: " + id);
        }

        if (novaAvaliacao < 0 || novaAvaliacao > 5) {
            throw new RuntimeException("Avaliação deve estar entre 0 e 5");
        }

        Restaurante restauranteEntity = restaurante.get();
        restauranteEntity.setAvaliacao(BigDecimal.valueOf(novaAvaliacao));

        return restauranteRepository.save(restauranteEntity);
    }

    // Inativar restaurante
    public void inativar(Long id) {
        Optional<Restaurante> restaurante = restauranteRepository.findById(id);

        if (restaurante.isEmpty()) {
            throw new RuntimeException("Restaurante não encontrado: " + id);
        }

        Restaurante restauranteEntity = restaurante.get();
        restauranteEntity.setAtivo(false);
        restauranteRepository.save(restauranteEntity);
    }

    // Reativar restaurante
    public void reativar(Long id) {
        Optional<Restaurante> restaurante = restauranteRepository.findById(id);

        if (restaurante.isEmpty()) {
            throw new RuntimeException("Restaurante não encontrado: " + id);
        }

        Restaurante restauranteEntity = restaurante.get();
        restauranteEntity.setAtivo(true);
        restauranteRepository.save(restauranteEntity);
    }

    // Validações privadas
    private void validarRestaurante(Restaurante restaurante) {
        if (restaurante.getNome() == null || restaurante.getNome().trim().isEmpty()) {
            throw new RuntimeException("Nome do restaurante é obrigatório");
        }

        if (restaurante.getCategoria() == null || restaurante.getCategoria().trim().isEmpty()) {
            throw new RuntimeException("Categoria é obrigatória");
        }

        if (restaurante.getAvaliacao() != null) {
            if (restaurante.getAvaliacao().compareTo(BigDecimal.ZERO) < 0 ||
                    restaurante.getAvaliacao().compareTo(BigDecimal.valueOf(5)) > 0) {
                throw new RuntimeException("Avaliação deve estar entre 0 e 5");
            }
        }
    }
}
