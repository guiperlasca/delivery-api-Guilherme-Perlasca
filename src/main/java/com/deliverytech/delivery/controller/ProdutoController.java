package com.deliverytech.delivery.controller;

import com.deliverytech.delivery.entity.Produto;
import com.deliverytech.delivery.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/produtos")
@CrossOrigin(origins = "*")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    // POST /api/produtos - Cadastrar produto
    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody Produto produto) {
        try {
            Produto produtoSalvo = produtoService.cadastrar(produto);
            return ResponseEntity.status(HttpStatus.CREATED).body(produtoSalvo);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // GET /api/produtos - Listar todos os produtos
    @GetMapping
    public ResponseEntity<List<Produto>> listarTodos() {
        List<Produto> produtos = produtoService.buscarTodos();
        return ResponseEntity.ok(produtos);
    }

    // GET /api/produtos/{id} - Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Optional<Produto> produto = produtoService.buscarPorId(id);

        if (produto.isPresent()) {
            return ResponseEntity.ok(produto.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("erro", "Produto não encontrado"));
    }

    // GET /api/produtos/restaurante/{restauranteId} - Produtos por restaurante
    @GetMapping("/restaurante/{restauranteId}")
    public ResponseEntity<List<Produto>> buscarPorRestaurante(@PathVariable Long restauranteId) {
        List<Produto> produtos = produtoService.buscarPorRestaurante(restauranteId);
        return ResponseEntity.ok(produtos);
    }

    // GET /api/produtos/restaurante/{restauranteId}/todos - Incluindo indisponíveis
    @GetMapping("/restaurante/{restauranteId}/todos")
    public ResponseEntity<List<Produto>> buscarTodosPorRestaurante(@PathVariable Long restauranteId) {
        List<Produto> produtos = produtoService.buscarTodosPorRestaurante(restauranteId);
        return ResponseEntity.ok(produtos);
    }

    // GET /api/produtos/categoria/{categoria} - Buscar por categoria
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Produto>> buscarPorCategoria(@PathVariable String categoria) {
        List<Produto> produtos = produtoService.buscarPorCategoria(categoria);
        return ResponseEntity.ok(produtos);
    }

    // GET /api/produtos/buscar?nome=Pizza - Buscar por nome
    @GetMapping("/buscar")
    public ResponseEntity<List<Produto>> buscarPorNome(@RequestParam String nome) {
        List<Produto> produtos = produtoService.buscarPorNome(nome);
        return ResponseEntity.ok(produtos);
    }

    // GET /api/produtos/preco?min=10&max=50 - Faixa de preço
    @GetMapping("/preco")
    public ResponseEntity<List<Produto>> buscarPorFaixaPreco(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {
        List<Produto> produtos = produtoService.buscarPorFaixaPreco(min, max);
        return ResponseEntity.ok(produtos);
    }

    // GET /api/produtos/restaurante/{restauranteId}/categoria/{categoria}
    @GetMapping("/restaurante/{restauranteId}/categoria/{categoria}")
    public ResponseEntity<List<Produto>> buscarPorRestauranteECategoria(
            @PathVariable Long restauranteId,
            @PathVariable String categoria) {
        List<Produto> produtos = produtoService.buscarPorRestauranteECategoria(restauranteId, categoria);
        return ResponseEntity.ok(produtos);
    }

    // GET /api/produtos/restaurante/{restauranteId}/ordenado-preco - Ordenado por preço
    @GetMapping("/restaurante/{restauranteId}/ordenado-preco")
    public ResponseEntity<List<Produto>> buscarOrdenadoPorPreco(@PathVariable Long restauranteId) {
        List<Produto> produtos = produtoService.buscarPorRestauranteOrdenadoPorPreco(restauranteId);
        return ResponseEntity.ok(produtos);
    }

    // GET /api/produtos/restaurante/{restauranteId}/preco-max/{precoMax}
    @GetMapping("/restaurante/{restauranteId}/preco-max/{precoMax}")
    public ResponseEntity<List<Produto>> buscarPorPrecoMaximo(
            @PathVariable Long restauranteId,
            @PathVariable BigDecimal precoMax) {
        List<Produto> produtos = produtoService.buscarPorPrecoMaximo(restauranteId, precoMax);
        return ResponseEntity.ok(produtos);
    }

    // GET /api/produtos/categorias - Listar categorias disponíveis
    @GetMapping("/categorias")
    public ResponseEntity<List<String>> listarCategorias() {
        List<String> categorias = produtoService.buscarCategorias();
        return ResponseEntity.ok(categorias);
    }

    // PUT /api/produtos/{id} - Atualizar produto
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Produto produto) {
        try {
            Produto produtoAtualizado = produtoService.atualizar(id, produto);
            return ResponseEntity.ok(produtoAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // PATCH /api/produtos/{id}/disponibilidade - Alterar disponibilidade
    @PatchMapping("/{id}/disponibilidade")
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

    // PATCH /api/produtos/{id}/indisponivel - Tornar indisponível
    @PatchMapping("/{id}/indisponivel")
    public ResponseEntity<?> tornarIndisponivel(@PathVariable Long id) {
        try {
            produtoService.tornarIndisponivel(id);
            return ResponseEntity.ok(Map.of("mensagem", "Produto marcado como indisponível"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // PATCH /api/produtos/{id}/disponivel - Tornar disponível
    @PatchMapping("/{id}/disponivel")
    public ResponseEntity<?> tornarDisponivel(@PathVariable Long id) {
        try {
            produtoService.tornarDisponivel(id);
            return ResponseEntity.ok(Map.of("mensagem", "Produto marcado como disponível"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // DELETE /api/produtos/{id} - Deletar produto (hard delete)
    @DeleteMapping("/{id}")
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
