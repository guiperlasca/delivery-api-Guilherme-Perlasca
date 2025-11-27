package com.deliverytech.delivery.service;

import com.deliverytech.delivery.dto.RestauranteRequestDTO;
import com.deliverytech.delivery.entity.Restaurante;
import com.deliverytech.delivery.entity.Usuario;
import com.deliverytech.delivery.exception.BusinessException;
import com.deliverytech.delivery.exception.EntityNotFoundException;
import com.deliverytech.delivery.repository.RestauranteRepository;
import com.deliverytech.delivery.security.SecurityUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RestauranteService {

    @Autowired
    private RestauranteRepository restauranteRepository;

    @Autowired
    private SecurityUtils securityUtils;

    // Verifica se o usuário logado é dono do restaurante (ou Admin)
    public boolean isOwner(Long restauranteId) {
        Usuario user = securityUtils.getCurrentUser();
        if (user == null)
            return false;

        // Admin pode tudo
        if (user.getRole().name().equals("ADMIN"))
            return true;

        // Se for restaurante, verifica se o ID bate
        return user.getRole().name().equals("RESTAURANTE") &&
                user.getRestauranteId() != null &&
                user.getRestauranteId().equals(restauranteId);
    }

    @Autowired
    private ModelMapper modelMapper;

    // Cadastrar restaurante
    @CacheEvict(value = "restaurantes", allEntries = true)
    public Restaurante cadastrar(RestauranteRequestDTO restauranteDTO) {
        Restaurante restaurante = modelMapper.map(restauranteDTO, Restaurante.class);

        if (restaurante.getAvaliacao() == null) {
            restaurante.setAvaliacao(BigDecimal.valueOf(5.0));
        }

        validarRestaurante(restaurante); // Validações de negócio
        return restauranteRepository.save(restaurante);
    }

    // Buscar por ID
    @Cacheable(value = "restaurantes", key = "#id")
    public Restaurante buscarPorId(Long id) {
        return restauranteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado: " + id));
    }

    // Atualizar restaurante
    @CacheEvict(value = "restaurantes", allEntries = true)
    public Restaurante atualizar(Long id, RestauranteRequestDTO restauranteAtualizadoDTO) {
        // buscaPorId já trata o 404
        Restaurante restaurante = buscarPorId(id);

        modelMapper.map(restauranteAtualizadoDTO, restaurante);
        validarRestaurante(restaurante);

        return restauranteRepository.save(restaurante);
    }

    // Atualizar avaliação
    @CacheEvict(value = "restaurantes", allEntries = true)
    public Restaurante atualizarAvaliacao(Long id, Double novaAvaliacao) {
        Restaurante restaurante = buscarPorId(id);

        if (novaAvaliacao == null || novaAvaliacao < 0 || novaAvaliacao > 5) {
            throw new BusinessException("Avaliação deve estar entre 0 e 5");
        }

        restaurante.setAvaliacao(BigDecimal.valueOf(novaAvaliacao));
        return restauranteRepository.save(restaurante);
    }

    // Inativar restaurante
    @CacheEvict(value = "restaurantes", allEntries = true)
    public void inativar(Long id) {
        Restaurante restaurante = buscarPorId(id);
        restaurante.setAtivo(false);
        restauranteRepository.save(restaurante);
    }

    // Reativar restaurante
    @CacheEvict(value = "restaurantes", allEntries = true)
    public void reativar(Long id) {
        Restaurante restaurante = buscarPorId(id);
        restaurante.setAtivo(true);
        restauranteRepository.save(restaurante);
    }

    // (Roteiro 5, Atividade 1.1)
    @CacheEvict(value = "restaurantes", allEntries = true)
    public Restaurante alterarStatus(Long id, boolean novoStatus) {
        Restaurante restaurante = buscarPorId(id);
        restaurante.setAtivo(novoStatus);
        return restauranteRepository.save(restaurante);
    }

    // (Roteiro 4) Calcula a taxa de entrega (Lógica simulada)
    public BigDecimal calcularTaxaEntrega(Long restauranteId, String cep) {
        Restaurante restaurante = buscarPorId(restauranteId);

        BigDecimal taxaPadrao = restaurante.getTaxaEntrega() != null ? restaurante.getTaxaEntrega()
                : BigDecimal.valueOf(10);

        if (cep != null && cep.endsWith("0")) {
            return BigDecimal.valueOf(5.00);
        }
        return taxaPadrao;
    }

    // Validações privadas
    private void validarRestaurante(Restaurante restaurante) {
        // Validações de @Valid cuidam de "NotBlank", "NotNull"
        // Focamos nas regras de negócio
        if (restaurante.getAvaliacao() != null) {
            if (restaurante.getAvaliacao().compareTo(BigDecimal.ZERO) < 0 ||
                    restaurante.getAvaliacao().compareTo(BigDecimal.valueOf(5)) > 0) {
                throw new BusinessException("Avaliação deve estar entre 0 e 5");
            }
        }
    }

    // Métodos de busca (sem alterações, apenas chamando o repository)
    @Cacheable(value = "restaurantes")
    public List<Restaurante> buscarTodos() {
        return restauranteRepository.findByAtivoTrue();
    }

    public List<Restaurante> buscarPorNome(String nome) {
        return restauranteRepository.findByNomeContainingIgnoreCase(nome);
    }

    public List<Restaurante> buscarPorCategoria(String categoria) {
        return restauranteRepository.findByCategoriaAndAtivoTrue(categoria);
    }

    public List<Restaurante> buscarOrdenadosPorAvaliacao() {
        return restauranteRepository.buscarAtivosOrdenadosPorAvaliacao();
    }

    public List<Restaurante> buscarAcimaMedia() {
        return restauranteRepository.buscarAcimaMediaAvaliacao();
    }

    public List<Restaurante> buscarPorAvaliacaoMinima(Double avaliacaoMinima) {
        return restauranteRepository.findByAvaliacaoGreaterThanEqual(BigDecimal.valueOf(avaliacaoMinima));
    }

    public List<String> buscarCategorias() {
        return restauranteRepository.buscarCategoriasDisponiveis();
    }

    public List<Restaurante> buscarProximos(String cep) {
        System.out.println("WARN: Chamada ao método stub buscarProximos(). Implementar lógica de busca por CEP.");
        return restauranteRepository.findByAtivoTrue().stream().limit(5).collect(Collectors.toList());
    }
}