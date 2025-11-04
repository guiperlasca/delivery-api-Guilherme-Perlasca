package com.deliverytech.delivery.config;

import com.deliverytech.delivery.entity.Cliente;
import com.deliverytech.delivery.entity.Pedido;
import com.deliverytech.delivery.entity.Produto;
import com.deliverytech.delivery.entity.Restaurante;
import com.deliverytech.delivery.repository.ClienteRepository;
import com.deliverytech.delivery.repository.PedidoRepository;
import com.deliverytech.delivery.repository.ProdutoRepository;
import com.deliverytech.delivery.repository.RestauranteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private RestauranteRepository restauranteRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("--- 🚀 INICIANDO VALIDAÇÃO DAS CONSULTAS (ATIVIDADE 2) ---");

        // Cenário 1: Busca de Cliente por Email [cite: 94]
        System.out.println("\n--- 🔎 Cenário 1: Buscando Cliente 'batman@wayneenterprises.com' ---");
        Optional<Cliente> clienteOpt = clienteRepository.findByEmail("batman@wayneenterprises.com");
        if (clienteOpt.isPresent()) {
            System.out.println("Resultado: Cliente encontrado -> " + clienteOpt.get().getNome());
        } else {
            System.out.println("Resultado: Cliente não encontrado.");
        }

        // Cenário 2: Produtos por Restaurante (ID 1L = McDonalds) [cite: 97]
        System.out.println("\n--- Cenário 2: Buscando Produtos do Restaurante ID 1 (McDonalds) ---");
        List<Produto> produtosRest1 = produtoRepository.findByRestauranteId(1L);
        System.out.println("Resultado: Encontrados " + produtosRest1.size() + " produtos.");
        produtosRest1.forEach(p -> System.out.println("  -> " + p.getNome()));

        // Cenário 3: Pedidos Recentes (Top 10) [cite: 100]
        System.out.println("\n--- 📅 Cenário 3: Buscando 10 Pedidos Mais Recentes ---");
        List<Pedido> pedidosRecentes = pedidoRepository.findTop10ByOrderByDataPedidoDesc();
        System.out.println("Resultado: Encontrados " + pedidosRecentes.size() + " pedidos.");
        pedidosRecentes.forEach(p -> System.out.println("  -> ID: " + p.getId() + " | Status: " + p.getStatus() + " | Data: " + p.getDataPedido()));

        // Cenário 4: Restaurantes por Taxa <= R$ 5,00
        System.out.println("\n--- Cenário 4: Buscando Restaurantes com Taxa <= R$ 5,00 ---");
        List<Restaurante> restaurantesTaxa = restauranteRepository.findByTaxaEntregaLessThanEqual(new BigDecimal("5.00"));
        System.out.println("Resultado: Encontrados " + restaurantesTaxa.size() + " restaurantes.");
        restaurantesTaxa.forEach(r -> System.out.println("  -> " + r.getNome() + " | Taxa: " + r.getTaxaEntrega()));
        if (restaurantesTaxa.isEmpty()) {
            System.out.println("  -> (AVISO: Nenhum restaurante encontrado. Verifique se a coluna 'taxa_entrega' foi populada no data.sql)");
        }

        System.out.println("\n--- TESTES ADICIONAIS (ATIVIDADE 1) ---");

        // Teste: RestauranteRepository - findTop5ByOrderByNomeAsc
        System.out.println("\n--- Teste: Top 5 Restaurantes por Nome (ASC) ---");
        List<Restaurante> top5Restaurantes = restauranteRepository.findTop5ByOrderByNomeAsc();
        top5Restaurantes.forEach(r -> System.out.println("  -> " + r.getNome()));

        // Teste: ProdutoRepository - findByDisponivelTrue
        System.out.println("\n--- Teste: Produtos Disponíveis ---");
        List<Produto> produtosDisponiveis = produtoRepository.findByDisponivelTrue();
        System.out.println("Resultado: " + produtosDisponiveis.size() + " produtos disponíveis no total.");

        // Teste: ProdutoRepository - findByPrecoLessThanEqual
        System.out.println("\n--- Teste: Produtos com Preço <= R$ 10,00 ---");
        List<Produto> produtosBaratos = produtoRepository.findByPrecoLessThanEqual(new BigDecimal("10.00"));
        System.out.println("Resultado: " + produtosBaratos.size() + " produtos encontrados.");
        produtosBaratos.forEach(p -> System.out.println("  -> " + p.getNome() + " | Preço: " + p.getPreco()));


        System.out.println("\n--- VALIDAÇÃO DAS CONSULTAS CONCLUÍDA ---");
    }
}