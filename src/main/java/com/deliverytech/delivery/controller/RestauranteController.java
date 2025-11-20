package com.deliverytech.delivery.controller;

import com.deliverytech.delivery.dto.RestauranteRequestDTO;
import com.deliverytech.delivery.dto.RestauranteResponseDTO;
import com.deliverytech.delivery.entity.Restaurante;
import com.deliverytech.delivery.exception.BusinessException;
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
import org.springframework.security.access.prepost.PreAuthorize; // Importado
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
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
    @PreAuthorize("hasRole('ADMIN')") // Apenas ADMIN pode cadastrar restaurantes
    @Operation(summary = "Cadastrar novo restaurante")
    @ApiResponse(responseCode = "201", description = "Restaurante criado com sucesso",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = RestauranteResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "422", description = "Regra de negócio violada")
    public ResponseEntity<RestauranteResponseDTO> cadastrar(@Valid @RequestBody RestauranteRequestDTO restauranteDTO) {
        Restaurante restauranteSalvo = restauranteService.cadastrar(restauranteDTO);
        RestauranteResponseDTO responseDTO = modelMapper.map(restauranteSalvo, RestauranteResponseDTO.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping
    // Público (Acesso liberado no SecurityConfig)
    @Operation(summary = "Listar restaurantes")
    @ApiResponse(responseCode = "200", description = "Lista de restaurantes")
    public ResponseEntity<List<RestauranteResponseDTO>> listarTodos(
            @Parameter(description = "Filtrar por categoria", example = "Pizza")
            @RequestParam(required = false) String categoria,
            @Parameter(description = "Filtrar por status (true=ativos, false=inativos)", example = "true")
            @RequestParam(required = false, defaultValue = "true") Boolean ativo) {

        List<Restaurante> restaurantes;
        List<Restaurante> baseList;

        if (categoria != null) {
            baseList = restauranteService.buscarPorCategoria(categoria);
        } else {
            baseList = restauranteService.buscarTodos();
        }

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
    // Público (Acesso liberado no SecurityConfig)
    @Operation(summary = "Buscar restaurante por ID")
    @ApiResponse(responseCode = "200", description = "Restaurante encontrado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = RestauranteResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    public ResponseEntity<RestauranteResponseDTO> buscarPorId(
            @Parameter(description = "ID do restaurante", example = "1")
            @PathVariable Long id) {

        Restaurante restaurante = restauranteService.buscarPorId(id);
        RestauranteResponseDTO responseDTO = modelMapper.map(restaurante, RestauranteResponseDTO.class);
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @restauranteService.isOwner(#id)") // Admin ou Dono do restaurante
    @Operation(summary = "Atualizar restaurante por ID")
    @ApiResponse(responseCode = "200", description = "Restaurante atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    public ResponseEntity<RestauranteResponseDTO> atualizar(
            @Parameter(description = "ID do restaurante a atualizar", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody RestauranteRequestDTO restauranteDTO) {

        Restaurante restauranteAtualizado = restauranteService.atualizar(id, restauranteDTO);
        RestauranteResponseDTO responseDTO = modelMapper.map(restauranteAtualizado, RestauranteResponseDTO.class);
        return ResponseEntity.ok(responseDTO);
    }

    @PatchMapping("/{id}/avaliacao")
    @Operation(summary = "Atualizar apenas a avaliação de um restaurante")
    @ApiResponse(responseCode = "200", description = "Avaliação atualizada com sucesso")
    @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    @ApiResponse(responseCode = "422", description = "Valor de avaliação inválido")
    public ResponseEntity<RestauranteResponseDTO> atualizarAvaliacao(
            @Parameter(description = "ID do restaurante", example = "1")
            @PathVariable Long id,
            @RequestBody Map<String, Double> body) {

        Double novaAvaliacao = body.get("avaliacao");
        Restaurante restauranteAtualizado = restauranteService.atualizarAvaliacao(id, novaAvaliacao);
        RestauranteResponseDTO responseDTO = modelMapper.map(restauranteAtualizado, RestauranteResponseDTO.class);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Apenas ADMIN pode inativar
    @Operation(summary = "Inativar restaurante (Soft Delete)")
    @ApiResponse(responseCode = "200", description = "Restaurante inativado com sucesso")
    @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    public ResponseEntity<Map<String, String>> inativar(
            @Parameter(description = "ID do restaurante a inativar", example = "1")
            @PathVariable Long id) {

        restauranteService.inativar(id);
        return ResponseEntity.ok(Map.of("mensagem", "Restaurante inativado com sucesso"));
    }

    @PatchMapping("/{id}/reativar")
    @PreAuthorize("hasRole('ADMIN')") // Apenas ADMIN pode reativar
    @Operation(summary = "Reativar um restaurante previamente inativado")
    @ApiResponse(responseCode = "200", description = "Restaurante reativado com sucesso")
    @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    public ResponseEntity<Map<String, String>> reativar(
            @Parameter(description = "ID do restaurante a reativar", example = "6")
            @PathVariable Long id) {

        restauranteService.reativar(id);
        return ResponseEntity.ok(Map.of("mensagem", "Restaurante reativado com sucesso"));
    }

    @GetMapping("/{id}/taxa-entrega/{cep}")
    @Operation(summary = "Calcular taxa de entrega (simulado)")
    @ApiResponse(responseCode = "200", description = "Taxa calculada")
    @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    public ResponseEntity<Map<String, Object>> calcularTaxaEntrega(
            @Parameter(description = "ID do restaurante", example = "1")
            @PathVariable Long id,
            @Parameter(description = "CEP do cliente", example = "90000000")
            @PathVariable String cep) {

        BigDecimal taxa = restauranteService.calcularTaxaEntrega(id, cep);
        return ResponseEntity.ok(Map.of("restauranteId", id, "cep", cep, "taxaCalculada", taxa));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or @restauranteService.isOwner(#id)") // Admin ou Dono
    @Operation(summary = "Ativar ou desativar um restaurante (método preferido)")
    @ApiResponse(responseCode = "200", description = "Status alterado com sucesso")
    @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    @ApiResponse(responseCode = "400", description = "Corpo da requisição inválido")
    public ResponseEntity<RestauranteResponseDTO> alterarStatus(
            @Parameter(description = "ID do restaurante", example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Objeto JSON para definir o status. Ex: {\"ativo\": true}",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
            @RequestBody Map<String, Boolean> body) {

        Boolean novoStatus = body.get("ativo");
        if (novoStatus == null) {
            throw new BusinessException("Corpo da requisição deve conter 'ativo: true|false'");
        }
        Restaurante restauranteAtualizado = restauranteService.alterarStatus(id, novoStatus);
        RestauranteResponseDTO responseDTO = modelMapper.map(restauranteAtualizado, RestauranteResponseDTO.class);
        return ResponseEntity.ok(responseDTO);
    }
}