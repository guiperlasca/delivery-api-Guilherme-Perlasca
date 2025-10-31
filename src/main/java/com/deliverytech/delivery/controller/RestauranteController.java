package com.deliverytech.delivery.controller;

import com.deliverytech.delivery.entity.Restaurante;
import com.deliverytech.delivery.service.RestauranteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/restaurantes")
@CrossOrigin(origins = "*")
public class RestauranteController {

    @Autowired
    private RestauranteService restauranteService;

    // POST /api/restaurantes - Cadastrar restaurante
    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody Restaurante restaurante) {
        try {
            Restaurante restauranteSalvo = restauranteService.cadastrar(restaurante);
            return ResponseEntity.status(HttpStatus.CREATED).body(restauranteSalvo);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // GET /api/restaurantes - Listar todos os restaurantes ativos
    @GetMapping
    public ResponseEntity<List<Restaurante>> listarTodos() {
        List<Restaurante> restaurantes = restauranteService.buscarTodos();
        return ResponseEntity.ok(restaurantes);
    }

    // GET /api/restaurantes/{id} - Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Optional<Restaurante> restaurante = restauranteService.buscarPorId(id);

        if (restaurante.isPresent()) {
            return ResponseEntity.ok(restaurante.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("erro", "Restaurante não encontrado"));
    }

    // GET /api/restaurantes/buscar?nome=Pizza - Buscar por nome
    @GetMapping("/buscar")
    public ResponseEntity<List<Restaurante>> buscarPorNome(@RequestParam String nome) {
        List<Restaurante> restaurantes = restauranteService.buscarPorNome(nome);
        return ResponseEntity.ok(restaurantes);
    }

    // GET /api/restaurantes/categoria/{categoria} - Buscar por categoria
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Restaurante>> buscarPorCategoria(@PathVariable String categoria) {
        List<Restaurante> restaurantes = restauranteService.buscarPorCategoria(categoria);
        return ResponseEntity.ok(restaurantes);
    }

    // GET /api/restaurantes/top-avaliados - Ordenados por avaliação
    @GetMapping("/top-avaliados")
    public ResponseEntity<List<Restaurante>> buscarTopAvaliados() {
        List<Restaurante> restaurantes = restauranteService.buscarOrdenadosPorAvaliacao();
        return ResponseEntity.ok(restaurantes);
    }

    // GET /api/restaurantes/acima-media - Acima da média
    @GetMapping("/acima-media")
    public ResponseEntity<List<Restaurante>> buscarAcimaMedia() {
        List<Restaurante> restaurantes = restauranteService.buscarAcimaMedia();
        return ResponseEntity.ok(restaurantes);
    }

    // GET /api/restaurantes/avaliacao?min=4.0 - Por avaliação mínima
    @GetMapping("/avaliacao")
    public ResponseEntity<List<Restaurante>> buscarPorAvaliacao(@RequestParam Double min) {
        List<Restaurante> restaurantes = restauranteService.buscarPorAvaliacaoMinima(min);
        return ResponseEntity.ok(restaurantes);
    }

    // GET /api/restaurantes/categorias - Listar categorias disponíveis
    @GetMapping("/categorias")
    public ResponseEntity<List<String>> listarCategorias() {
        List<String> categorias = restauranteService.buscarCategorias();
        return ResponseEntity.ok(categorias);
    }

    // PUT /api/restaurantes/{id} - Atualizar restaurante
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Restaurante restaurante) {
        try {
            Restaurante restauranteAtualizado = restauranteService.atualizar(id, restaurante);
            return ResponseEntity.ok(restauranteAtualizado);
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
            return ResponseEntity.ok(restauranteAtualizado);
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
}
