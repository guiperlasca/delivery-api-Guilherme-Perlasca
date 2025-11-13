package com.deliverytech.delivery.controller;

import com.deliverytech.delivery.dto.RestauranteRequestDTO;
import com.deliverytech.delivery.dto.RestauranteResponseDTO;
import com.deliverytech.delivery.entity.Restaurante;
import com.deliverytech.delivery.service.RestauranteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Restaurantes", description = "Operações para gerenciamento de restaurantes")
public class RestauranteController {

    @Autowired
    private RestauranteService restauranteService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping
    @Operation(summary = "Cadastrar novo restaurante",
            description = "Cria um novo restaurante no sistema.")
    @ApiResponse(responseCode = "201", description = "Restaurante criado com sucesso",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = RestauranteResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos (erros de validação)")
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

    @GetMapping
    @Operation(summary = "Listar restaurantes",
            description = "Lista todos os restaurantes, permitindo filtrar por categoria e status (ativo).")
    @ApiResponse(responseCode = "200", description = "Lista de restaurantes")
    public ResponseEntity<List<RestauranteResponseDTO>> listarTodos(
            @Parameter(description = "Filtrar por categoria", example = "Pizza")
            @RequestParam(required = false) String categoria,

            @Parameter(description = "Filtrar por status (true=ativos, false=inativos)", example = "true")
            @RequestParam(required = false, defaultValue = "true") Boolean ativo) {

        List<Restaurante> restaurantes;

        // Os métodos de serviço `buscarTodos` e `buscarPorCategoria` já retornam apenas ativos.
        List<Restaurante> baseList;
        if (categoria != null) {
            baseList = restauranteService.buscarPorCategoria(categoria);
        } else {
            baseList = restauranteService.buscarTodos();
        }

        // Se o usuário pedir `ativo=false`, a lista (baseada nos serviços atuais) será vazia.
        if (!ativo) {
            restaurantes = baseList.stream()
                    .filter(r -> r.getAtivo().equals(false))
                    .collect(Collectors.toList());
        } else {
            restaurantes = baseList;
        }

        List<RestauranteResponseDTO> responseDTOs = restaurantes.stream()
                .map(r -> modelMapper.map(r, RestauranteResponseDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar restaurante por ID")
    @ApiResponse(responseCode = "200", description = "Restaurante encontrado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = RestauranteResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    public ResponseEntity<?> buscarPorId(
            @Parameter(description = "ID do restaurante", example = "1")
            @PathVariable Long id) {
        Optional<Restaurante> restaurante = restauranteService.buscarPorId(id);

        if (restaurante.isPresent()) {
            RestauranteResponseDTO responseDTO = modelMapper.map(restaurante.get(), RestauranteResponseDTO.class);
            return ResponseEntity.ok(responseDTO);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("erro", "Restaurante não encontrado"));
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar restaurantes por nome")
    @ApiResponse(responseCode = "200", description = "Lista de restaurantes encontrados")
    public ResponseEntity<List<RestauranteResponseDTO>> buscarPorNome(
            @Parameter(description = "Termo de busca para o nome", example = "Pizza")
            @RequestParam String nome) {
        List<Restaurante> restaurantes = restauranteService.buscarPorNome(nome);
        List<RestauranteResponseDTO> responseDTOs = restaurantes.stream()
                .map(r -> modelMapper.map(r, RestauranteResponseDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/categoria/{categoria}")
    @Operation(summary = "Buscar restaurantes por categoria (ativos)")
    @ApiResponse(responseCode = "200", description = "Lista de restaurantes encontrados")
    public ResponseEntity<List<RestauranteResponseDTO>> buscarPorCategoria(
            @Parameter(description = "Nome da categoria", example = "Fast Food")
            @PathVariable String categoria) {
        List<Restaurante> restaurantes = restauranteService.buscarPorCategoria(categoria);
        List<RestauranteResponseDTO> responseDTOs = restaurantes.stream()
                .map(r -> modelMapper.map(r, RestauranteResponseDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/top-avaliados")
    @Operation(summary = "Listar restaurantes por avaliação (melhores primeiro)")
    @ApiResponse(responseCode = "200", description = "Lista de restaurantes ordenados")
    public ResponseEntity<List<RestauranteResponseDTO>> buscarTopAvaliados() {
        List<Restaurante> restaurantes = restauranteService.buscarOrdenadosPorAvaliacao();
        List<RestauranteResponseDTO> responseDTOs = restaurantes.stream()
                .map(r -> modelMapper.map(r, RestauranteResponseDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/acima-media")
    @Operation(summary = "Listar restaurantes com avaliação acima da média")
    @ApiResponse(responseCode = "200", description = "Lista de restaurantes")
    public ResponseEntity<List<RestauranteResponseDTO>> buscarAcimaMedia() {
        List<Restaurante> restaurantes = restauranteService.buscarAcimaMedia();
        List<RestauranteResponseDTO> responseDTOs = restaurantes.stream()
                .map(r -> modelMapper.map(r, RestauranteResponseDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/avaliacao")
    @Operation(summary = "Buscar restaurantes por avaliação mínima")
    @ApiResponse(responseCode = "200", description = "Lista de restaurantes")
    public ResponseEntity<List<RestauranteResponseDTO>> buscarPorAvaliacao(
            @Parameter(description = "Nota mínima da avaliação", example = "4.5")
            @RequestParam Double min) {
        List<Restaurante> restaurantes = restauranteService.buscarPorAvaliacaoMinima(min);
        List<RestauranteResponseDTO> responseDTOs = restaurantes.stream()
                .map(r -> modelMapper.map(r, RestauranteResponseDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/categorias")
    @Operation(summary = "Listar todas as categorias de restaurantes disponíveis")
    @ApiResponse(responseCode = "200", description = "Lista de nomes de categorias")
    public ResponseEntity<List<String>> listarCategorias() {
        List<String> categorias = restauranteService.buscarCategorias();
        return ResponseEntity.ok(categorias);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar restaurante por ID")
    @ApiResponse(responseCode = "200", description = "Restaurante atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    public ResponseEntity<?> atualizar(
            @Parameter(description = "ID do restaurante a atualizar", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody RestauranteRequestDTO restauranteDTO) {
        try {
            Restaurante restauranteAtualizado = restauranteService.atualizar(id, restauranteDTO);
            RestauranteResponseDTO responseDTO = modelMapper.map(restauranteAtualizado, RestauranteResponseDTO.class);
            return ResponseEntity.ok(responseDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/avaliacao")
    @Operation(summary = "Atualizar apenas a avaliação de um restaurante")
    @ApiResponse(responseCode = "200", description = "Avaliação atualizada com sucesso")
    @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    @ApiResponse(responseCode = "400", description = "Valor de avaliação inválido")
    public ResponseEntity<?> atualizarAvaliacao(
            @Parameter(description = "ID do restaurante", example = "1")
            @PathVariable Long id,
            @RequestBody Map<String, Double> body) {
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

    @DeleteMapping("/{id}")
    @Operation(summary = "Inativar restaurante (Soft Delete)")
    @ApiResponse(responseCode = "200", description = "Restaurante inativado com sucesso")
    @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    public ResponseEntity<?> inativar(
            @Parameter(description = "ID do restaurante a inativar", example = "1")
            @PathVariable Long id) {
        try {
            restauranteService.inativar(id);
            return ResponseEntity.ok(Map.of("mensagem", "Restaurante inativado com sucesso"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/reativar")
    @Operation(summary = "Reativar um restaurante previamente inativado")
    @ApiResponse(responseCode = "200", description = "Restaurante reativado com sucesso")
    @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    public ResponseEntity<?> reativar(
            @Parameter(description = "ID do restaurante a reativar", example = "6")
            @PathVariable Long id) {
        try {
            restauranteService.reativar(id);
            return ResponseEntity.ok(Map.of("mensagem", "Restaurante reativado com sucesso"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    @GetMapping("/{id}/taxa-entrega/{cep}")
    @Operation(summary = "Calcular taxa de entrega (simulado)")
    @ApiResponse(responseCode = "200", description = "Taxa calculada")
    @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    public ResponseEntity<?> calcularTaxaEntrega(
            @Parameter(description = "ID do restaurante", example = "1")
            @PathVariable Long id,
            @Parameter(description = "CEP do cliente", example = "90000000")
            @PathVariable String cep) {
        try {
            BigDecimal taxa = restauranteService.calcularTaxaEntrega(id, cep);
            return ResponseEntity.ok(Map.of("restauranteId", id, "cep", cep, "taxaCalculada", taxa));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Ativar ou desativar um restaurante (método preferido)",
            description = "Este endpoint substitui /inativar e /reativar, centralizando o controle de status.")
    @ApiResponse(responseCode = "200", description = "Status alterado com sucesso")
    @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    @ApiResponse(responseCode = "400", description = "Corpo da requisição inválido (ex: {\"ativo\": true})")
    public ResponseEntity<?> alterarStatus(
            @Parameter(description = "ID do restaurante", example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Objeto JSON para definir o status. Ex: {\"ativo\": true}",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
            @RequestBody Map<String, Boolean> body) {
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

    @GetMapping("/proximos/{cep}")
    @Operation(summary = "Buscar restaurantes próximos (simulado)",
            description = "Simula uma busca de restaurantes próximos com base em um CEP.")
    @ApiResponse(responseCode = "200", description = "Lista de restaurantes próximos")
    public ResponseEntity<List<RestauranteResponseDTO>> buscarProximos(
            @Parameter(description = "CEP do cliente", example = "90000000")
            @PathVariable String cep) {
        List<Restaurante> restaurantes = restauranteService.buscarProximos(cep);
        List<RestauranteResponseDTO> responseDTOs = restaurantes.stream()
                .map(r -> modelMapper.map(r, RestauranteResponseDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }
}