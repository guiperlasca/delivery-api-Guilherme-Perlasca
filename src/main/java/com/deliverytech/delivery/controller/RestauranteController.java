package com.deliverytech.delivery.controller;

import com.deliverytech.delivery.dto.RestauranteRequestDTO;
import com.deliverytech.delivery.dto.RestauranteResponseDTO;
import com.deliverytech.delivery.entity.Restaurante;
import com.deliverytech.delivery.service.RestauranteService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/restaurantes")
@CrossOrigin(origins = "*")
public class RestauranteController {

    @Autowired
    private RestauranteService restauranteService;

    @Autowired
    private ModelMapper modelMapper;

    // POST /api/restaurantes - Cadastrar restaurante
    @PostMapping
    public ResponseEntity<?> cadastrar(@Valid @RequestBody RestauranteRequestDTO restauranteDTO) {
        try {
            Restaurante restauranteSalvo = restauranteService.cadastrar(restauranteDTO);
            RestauranteResponseDTO responseDTO = modelMapper.map(restauranteSalvo, RestauranteResponseDTO.class);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // GET /api/restaurantes - Listar todos os restaurantes ativos
    @GetMapping
    public ResponseEntity<List<RestauranteResponseDTO>> listarTodos(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false, defaultValue = "true") Boolean ativo) {

        List<Restaurante> restaurantes;

        if (categoria != null) {
            restaurantes = restauranteService.buscarPorCategoria(categoria)
                    .stream()
                    .filter(r -> r.getAtivo().equals(ativo))
                    .collect(Collectors.toList());
        } else {
            if (ativo) {
                restaurantes = restauranteService.buscarTodos();
            } else {
                restaurantes = restauranteService.buscarTodos();
            }
        }

        List<RestauranteResponseDTO> responseDTOs = restaurantes.stream()
                .map(r -> modelMapper.map(r, RestauranteResponseDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    // GET /api/restaurantes/{id} - Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Optional<Restaurante> restaurante = restauranteService.buscarPorId(id);

        if (restaurante.isPresent()) {
            RestauranteResponseDTO responseDTO = modelMapper.map(restaurante.get(), RestauranteResponseDTO.class);
            return ResponseEntity.ok(responseDTO);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("erro", "Restaurante não encontrado"));
    }

    // GET /api/restaurantes/buscar?nome=Pizza - Buscar por nome
    @GetMapping("/buscar")
    public ResponseEntity<List<RestauranteResponseDTO>> buscarPorNome(@RequestParam String nome) {
        List<Restaurante> restaurantes = restauranteService.buscarPorNome(nome);
        List<RestauranteResponseDTO> responseDTOs = restaurantes.stream()
                .map(r -> modelMapper.map(r, RestauranteResponseDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    // GET /api/restaurantes/categoria/{categoria} - Buscar por categoria
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<RestauranteResponseDTO>> buscarPorCategoria(@PathVariable String categoria) {
        List<Restaurante> restaurantes = restauranteService.buscarPorCategoria(categoria);
        List<RestauranteResponseDTO> responseDTOs = restaurantes.stream()
                .map(r -> modelMapper.map(r, RestauranteResponseDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    // GET /api/restaurantes/top-avaliados - Ordenados por avaliação
    @GetMapping("/top-avaliados")
    public ResponseEntity<List<RestauranteResponseDTO>> buscarTopAvaliados() {
        List<Restaurante> restaurantes = restauranteService.buscarOrdenadosPorAvaliacao();
        List<RestauranteResponseDTO> responseDTOs = restaurantes.stream()
                .map(r -> modelMapper.map(r, RestauranteResponseDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    // GET /api/restaurantes/acima-media - Acima da média
    @GetMapping("/acima-media")
    public ResponseEntity<List<RestauranteResponseDTO>> buscarAcimaMedia() {
        List<Restaurante> restaurantes = restauranteService.buscarAcimaMedia();
        List<RestauranteResponseDTO> responseDTOs = restaurantes.stream()
                .map(r -> modelMapper.map(r, RestauranteResponseDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    // GET /api/restaurantes/avaliacao?min=4.0 - Por avaliação mínima
    @GetMapping("/avaliacao")
    public ResponseEntity<List<RestauranteResponseDTO>> buscarPorAvaliacao(@RequestParam Double min) {
        List<Restaurante> restaurantes = restauranteService.buscarPorAvaliacaoMinima(min);
        List<RestauranteResponseDTO> responseDTOs = restaurantes.stream()
                .map(r -> modelMapper.map(r, RestauranteResponseDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    // GET /api/restaurantes/categorias - Listar categorias disponíveis
    @GetMapping("/categorias")
    public ResponseEntity<List<String>> listarCategorias() {
        List<String> categorias = restauranteService.buscarCategorias();
        return ResponseEntity.ok(categorias);
    }

    // PUT /api/restaurantes/{id} - Atualizar restaurante
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @Valid @RequestBody RestauranteRequestDTO restauranteDTO) {
        try {
            Restaurante restauranteAtualizado = restauranteService.atualizar(id, restauranteDTO);
            RestauranteResponseDTO responseDTO = modelMapper.map(restauranteAtualizado, RestauranteResponseDTO.class);
            return ResponseEntity.ok(responseDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // PATCH /api/restaurantes/{id}/avaliacao - Atualizar avaliação
    @PatchMapping("/{id}/avaliacao")
    public ResponseEntity<?> atualizarAvaliacao(@PathVariable Long id, @RequestBody Map<String, Double> body) {
        try {
            Double novaAvaliacao = body.get("avaliacao");
            Restaurante restauranteAtualizado = restauranteService.atualizarAvaliacao(id, novaAvaliacao);
            RestauranteResponseDTO responseDTO = modelMapper.map(restauranteAtualizado, RestauranteResponseDTO.class);
            return ResponseEntity.ok(responseDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // DELETE /api/restaurantes/{id} - Inativar restaurante
    @DeleteMapping("/{id}")
    public ResponseEntity<?> inativar(@PathVariable Long id) {
        try {
            restauranteService.inativar(id);
            return ResponseEntity.ok(Map.of("mensagem", "Restaurante inativado com sucesso"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // PATCH /api/restaurantes/{id}/reativar - Reativar restaurante
    @PatchMapping("/{id}/reativar")
    public ResponseEntity<?> reativar(@PathVariable Long id) {
        try {
            restauranteService.reativar(id);
            return ResponseEntity.ok(Map.of("mensagem", "Restaurante reativado com sucesso"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // GET /api/restaurantes/{id}/taxa-entrega/{cep}
    @GetMapping("/{id}/taxa-entrega/{cep}")
    public ResponseEntity<?> calcularTaxaEntrega(@PathVariable Long id, @PathVariable String cep) {
        try {
            BigDecimal taxa = restauranteService.calcularTaxaEntrega(id, cep);
            return ResponseEntity.ok(Map.of("restauranteId", id, "cep", cep, "taxaCalculada", taxa));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // PATCH /api/restaurantes/{id}/status - Ativar/desativar
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> alterarStatus(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        try {
            Boolean novoStatus = body.get("ativo");
            if (novoStatus == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("erro", "Corpo da requisição deve conter 'ativo: true|false'"));
            }
            Restaurante restauranteAtualizado = restauranteService.alterarStatus(id, novoStatus);
            RestauranteResponseDTO responseDTO = modelMapper.map(restauranteAtualizado, RestauranteResponseDTO.class);
            return ResponseEntity.ok(responseDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // GET /api/restaurantes/proximos/{cep} - Restaurantes próximos
    @GetMapping("/proximos/{cep}")
    public ResponseEntity<List<RestauranteResponseDTO>> buscarProximos(@PathVariable String cep) {
        List<Restaurante> restaurantes = restauranteService.buscarProximos(cep); // Método Stub
        List<RestauranteResponseDTO> responseDTOs = restaurantes.stream()
                .map(r -> modelMapper.map(r, RestauranteResponseDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }
}