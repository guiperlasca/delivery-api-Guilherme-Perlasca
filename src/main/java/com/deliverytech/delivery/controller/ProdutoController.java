package com.deliverytech.delivery.controller;

import com.deliverytech.delivery.dto.ProdutoRequestDTO;
import com.deliverytech.delivery.dto.ProdutoResponseDTO;
import com.deliverytech.delivery.entity.Produto;
import com.deliverytech.delivery.service.ProdutoService;
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
import java.util.stream.Collectors; // Import

@RestController
@RequestMapping("/api") // Modificado para suportar o endpoint do Roteiro 4
@CrossOrigin(origins = "*")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private ModelMapper modelMapper; // Injetar ModelMapper

    // Mapeamento auxiliar para converter Entidade -> DTO Response
    private ProdutoResponseDTO convertToResponseDTO(Produto produto) {
        ProdutoResponseDTO dto = modelMapper.map(produto, ProdutoResponseDTO.class);
        dto.setRestauranteId(produto.getRestaurante().getId()); // Mapeamento manual do ID
        return dto;
    }

    // POST /api/produtos - Cadastrar produto (Usa DTOs e @Valid)
    @PostMapping("/produtos")
    public ResponseEntity<?> cadastrar(@Valid @RequestBody ProdutoRequestDTO produtoDTO) {
        try {
            Produto produtoSalvo = produtoService.cadastrar(produtoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToResponseDTO(produtoSalvo));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // GET /api/produtos - Listar todos os produtos (Retorna DTO)
    @GetMapping("/produtos")
    public ResponseEntity<List<ProdutoResponseDTO>> listarTodos() {
        List<Produto> produtos = produtoService.buscarTodos();
        List<ProdutoResponseDTO> dtos = produtos.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // GET /api/produtos/{id} - Buscar por ID (Retorna DTO)
    @GetMapping("/produtos/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Optional<Produto> produto = produtoService.buscarPorId(id);

        if (produto.isPresent()) {
            return ResponseEntity.ok(convertToResponseDTO(produto.get()));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("erro", "Produto não encontrado"));
    }

    // GET /api/restaurantes/{restauranteId}/produtos - (Endpoint ATUALIZADO Roteiro 4)
    @GetMapping("/restaurantes/{restauranteId}/produtos")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarPorRestaurante(@PathVariable Long restauranteId) {
        List<Produto> produtos = produtoService.buscarPorRestaurante(restauranteId);
        List<ProdutoResponseDTO> dtos = produtos.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // GET /api/produtos/restaurante/{restauranteId}/todos - Incluindo indisponíveis
    @GetMapping("/produtos/restaurante/{restauranteId}/todos")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarTodosPorRestaurante(@PathVariable Long restauranteId) {
        List<Produto> produtos = produtoService.buscarTodosPorRestaurante(restauranteId);
        List<ProdutoResponseDTO> dtos = produtos.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // GET /api/produtos/categoria/{categoria} - Buscar por categoria (Retorna DTO)
    @GetMapping("/produtos/categoria/{categoria}")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarPorCategoria(@PathVariable String categoria) {
        List<Produto> produtos = produtoService.buscarPorCategoria(categoria);
        List<ProdutoResponseDTO> dtos = produtos.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // GET /api/produtos/buscar?nome=Pizza - Buscar por nome (Retorna DTO)
    @GetMapping("/produtos/buscar")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarPorNome(@RequestParam String nome) {
        List<Produto> produtos = produtoService.buscarPorNome(nome);
        List<ProdutoResponseDTO> dtos = produtos.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // GET /api/produtos/preco?min=10&max=50 - Faixa de preço (Retorna DTO)
    @GetMapping("/produtos/preco")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarPorFaixaPreco(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {
        List<Produto> produtos = produtoService.buscarPorFaixaPreco(min, max);
        List<ProdutoResponseDTO> dtos = produtos.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // ... (outros endpoints de busca inalterados, mas retornando DTO) ...
    @GetMapping("/produtos/restaurante/{restauranteId}/categoria/{categoria}")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarPorRestauranteECategoria(
            @PathVariable Long restauranteId,
            @PathVariable String categoria) {
        List<Produto> produtos = produtoService.buscarPorRestauranteECategoria(restauranteId, categoria);
        List<ProdutoResponseDTO> dtos = produtos.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/produtos/restaurante/{restauranteId}/ordenado-preco")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarOrdenadoPorPreco(@PathVariable Long restauranteId) {
        List<Produto> produtos = produtoService.buscarPorRestauranteOrdenadoPorPreco(restauranteId);
        List<ProdutoResponseDTO> dtos = produtos.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/produtos/restaurante/{restauranteId}/preco-max/{precoMax}")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarPorPrecoMaximo(
            @PathVariable Long restauranteId,
            @PathVariable BigDecimal precoMax) {
        List<Produto> produtos = produtoService.buscarPorPrecoMaximo(restauranteId, precoMax);
        List<ProdutoResponseDTO> dtos = produtos.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/produtos/categorias")
    public ResponseEntity<List<String>> listarCategorias() {
        List<String> categorias = produtoService.buscarCategorias();
        return ResponseEntity.ok(categorias);
    }

    // PUT /api/produtos/{id} - Atualizar produto (Usa DTOs e @Valid)
    @PutMapping("/produtos/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @Valid @RequestBody ProdutoRequestDTO produtoDTO) {
        try {
            Produto produtoAtualizado = produtoService.atualizar(id, produtoDTO);
            return ResponseEntity.ok(convertToResponseDTO(produtoAtualizado));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // PATCH /api/produtos/{id}/disponibilidade - Alterar disponibilidade
    @PatchMapping("/produtos/{id}/disponibilidade")
    public ResponseEntity<?> alterarDisponibilidade(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        try {
            Boolean disponivel = body.get("disponivel");
            produtoService.alterarDisponibilidade(id, disponivel);
            return ResponseEntity.ok(Map.of("mensagem", "Disponibilidade alterada com sucesso"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // ... (endpoints indisponivel, disponivel, deletar inalterados, mantendo /produtos) ...
    @PatchMapping("/produtos/{id}/indisponivel")
    public ResponseEntity<?> tornarIndisponivel(@PathVariable Long id) {
        try {
            produtoService.tornarIndisponivel(id);
            return ResponseEntity.ok(Map.of("mensagem", "Produto marcado como indisponível"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    @PatchMapping("/produtos/{id}/disponivel")
    public ResponseEntity<?> tornarDisponivel(@PathVariable Long id) {
        try {
            produtoService.tornarDisponivel(id);
            return ResponseEntity.ok(Map.of("mensagem", "Produto marcado como disponível"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    @DeleteMapping("/produtos/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        try {
            produtoService.deletar(id);
            return ResponseEntity.ok(Map.of("mensagem", "Produto deletado com sucesso"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", e.getMessage()));
        }
    }
}