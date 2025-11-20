package com.deliverytech.delivery.controller;

import com.deliverytech.delivery.dto.ProdutoRequestDTO;
import com.deliverytech.delivery.dto.ProdutoResponseDTO;
import com.deliverytech.delivery.entity.Produto;
import com.deliverytech.delivery.service.ProdutoService;
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
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@Tag(name = "Produtos", description = "Operações para gerenciamento de produtos (cardápio)")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private ModelMapper modelMapper;

    private ProdutoResponseDTO convertToResponseDTO(Produto produto) {
        ProdutoResponseDTO dto = modelMapper.map(produto, ProdutoResponseDTO.class);
        dto.setRestauranteId(produto.getRestaurante().getId());
        return dto;
    }

    @PostMapping("/produtos")
    @PreAuthorize("hasRole('RESTAURANTE') or hasRole('ADMIN')") // Apenas Restaurantes ou Admin criam produtos
    @Operation(summary = "Cadastrar novo produto")
    @ApiResponse(responseCode = "201", description = "Produto criado com sucesso",
            content = @Content(schema = @Schema(implementation = ProdutoResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    @ApiResponse(responseCode = "422", description = "Restaurante inativo")
    public ResponseEntity<ProdutoResponseDTO> cadastrar(@Valid @RequestBody ProdutoRequestDTO produtoDTO) {
        Produto produtoSalvo = produtoService.cadastrar(produtoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToResponseDTO(produtoSalvo));
    }

    @GetMapping("/produtos")
    // Público
    @Operation(summary = "Listar todos os produtos de todos os restaurantes")
    @ApiResponse(responseCode = "200", description = "Lista de produtos")
    public ResponseEntity<List<ProdutoResponseDTO>> listarTodos() {
        List<Produto> produtos = produtoService.buscarTodos();
        List<ProdutoResponseDTO> dtos = produtos.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/produtos/{id}")
    // Público
    @Operation(summary = "Buscar produto por ID")
    @ApiResponse(responseCode = "200", description = "Produto encontrado",
            content = @Content(schema = @Schema(implementation = ProdutoResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    public ResponseEntity<ProdutoResponseDTO> buscarPorId(@Parameter(description = "ID do produto") @PathVariable Long id) {
        Produto produto = produtoService.buscarPorId(id);
        return ResponseEntity.ok(convertToResponseDTO(produto));
    }

    @GetMapping("/restaurantes/{restauranteId}/produtos")
    // Público
    @Operation(summary = "Buscar produtos disponíveis de um restaurante")
    @ApiResponse(responseCode = "200", description = "Lista de produtos")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarPorRestaurante(
            @Parameter(description = "ID do restaurante") @PathVariable Long restauranteId) {
        List<Produto> produtos = produtoService.buscarPorRestaurante(restauranteId);
        List<ProdutoResponseDTO> dtos = produtos.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/produtos/restaurante/{restauranteId}/todos")
    // Público
    @Operation(summary = "Buscar todos os produtos (incluindo indisponíveis) de um restaurante")
    @ApiResponse(responseCode = "200", description = "Lista de produtos")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarTodosPorRestaurante(
            @Parameter(description = "ID do restaurante") @PathVariable Long restauranteId) {
        List<Produto> produtos = produtoService.buscarTodosPorRestaurante(restauranteId);
        List<ProdutoResponseDTO> dtos = produtos.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/produtos/categoria/{categoria}")
    // Público
    @Operation(summary = "Buscar produtos disponíveis por categoria (geral)")
    @ApiResponse(responseCode = "200", description = "Lista de produtos")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarPorCategoria(
            @Parameter(description = "Nome da categoria") @PathVariable String categoria) {
        List<Produto> produtos = produtoService.buscarPorCategoria(categoria);
        List<ProdutoResponseDTO> dtos = produtos.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/produtos/buscar")
    // Público
    @Operation(summary = "Buscar produtos por nome (geral)")
    @ApiResponse(responseCode = "200", description = "Lista de produtos")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarPorNome(
            @Parameter(description = "Termo de busca para o nome") @RequestParam String nome) {
        List<Produto> produtos = produtoService.buscarPorNome(nome);
        List<ProdutoResponseDTO> dtos = produtos.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/produtos/{id}")
    @PreAuthorize("hasRole('ADMIN') or @produtoService.isOwner(#id)") // Admin ou Dono do restaurante do produto
    @Operation(summary = "Atualizar dados de um produto")
    @ApiResponse(responseCode = "200", description = "Produto atualizado",
            content = @Content(schema = @Schema(implementation = ProdutoResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "422", description = "Não é permitido alterar o restaurante")
    public ResponseEntity<ProdutoResponseDTO> atualizar(
            @Parameter(description = "ID do produto") @PathVariable Long id,
            @Valid @RequestBody ProdutoRequestDTO produtoDTO) {

        Produto produtoAtualizado = produtoService.atualizar(id, produtoDTO);
        return ResponseEntity.ok(convertToResponseDTO(produtoAtualizado));
    }

    @PatchMapping("/produtos/{id}/disponibilidade")
    @PreAuthorize("hasRole('ADMIN') or @produtoService.isOwner(#id)") // Admin ou Dono
    @Operation(summary = "Alterar disponibilidade (disponível/indisponível) de um produto")
    @ApiResponse(responseCode = "200", description = "Disponibilidade alterada")
    @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    @ApiResponse(responseCode = "422", description = "Status de disponibilidade não informado")
    public ResponseEntity<Map<String, String>> alterarDisponibilidade(
            @Parameter(description = "ID do produto") @PathVariable Long id,
            @RequestBody Map<String, Boolean> body) {

        Boolean disponivel = body.get("disponivel");
        produtoService.alterarDisponibilidade(id, disponivel);
        String status = disponivel ? "disponível" : "indisponível";
        return ResponseEntity.ok(Map.of("mensagem", "Produto marcado como " + status));
    }

    @DeleteMapping("/produtos/{id}")
    @PreAuthorize("hasRole('ADMIN') or @produtoService.isOwner(#id)") // Admin ou Dono
    @Operation(summary = "Excluir um produto (Hard Delete)")
    @ApiResponse(responseCode = "200", description = "Produto deletado com sucesso")
    @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    public ResponseEntity<Map<String, String>> deletar(@Parameter(description = "ID do produto") @PathVariable Long id) {
        produtoService.deletar(id);
        return ResponseEntity.ok(Map.of("mensagem", "Produto deletado com sucesso"));
    }
}