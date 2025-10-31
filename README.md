# 🚚 Delivery Tech API

Sistema completo de delivery desenvolvido com **Spring Boot 3.2.x** e **Java 21 LTS** para competir com iFood e Uber Eats.

## 📊 Status do Projeto

🎉 **ROTEIRO 2 COMPLETO** - Sistema funcional com dados reais!

### ✅ Funcionalidades Implementadas
- [x] **Entidades JPA** (Cliente, Restaurante, Produto, Pedido)
- [x] **Repositories** com 25+ queries personalizadas
- [x] **Services** com regras de negócio e validações
- [x] **Controllers REST** com 30+ endpoints
- [x] **Dados de teste** com super-heróis e restaurantes famosos
- [x] **Soft delete** e controle de status
- [x] **Máquina de estados** para pedidos
- [x] **Relatórios** e estatísticas

## 🛠️ Tecnologias Utilizadas

- **Java 21 LTS** (features modernas: Records, Text Blocks, Virtual Threads)
- **Spring Boot 3.2.x**
- **Spring Web** (REST Controllers)
- **Spring Data JPA** (Persistência)
- **H2 Database** (desenvolvimento)
- **Maven** (gerenciamento de dependências)

## 🚀 Como Executar

### Pré-requisitos
- JDK 21 instalado  
- Git configurado

### Passos

1. **Clone o repositório:**
   ```bash
   git clone https://github.com/seuusuario/delivery-api.git
   cd delivery-api
   ```

2. **Execute a aplicação:**
   ```bash
   ./mvnw spring-boot:run
   ```

3. **Acesse:**
   - API: [http://localhost:8080/api/](http://localhost:8080/api/)
   - Console H2: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
   - Health Check: [http://localhost:8080/health](http://localhost:8080/health)

---

## 📡 Endpoints da API

### 🧑‍💼 Clientes (`/api/clientes`)
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/api/clientes` | Lista todos os clientes ativos |
| `GET` | `/api/clientes/{id}` | Busca cliente por ID |
| `GET` | `/api/clientes/buscar?nome=João` | Busca por nome |
| `GET` | `/api/clientes/email/{email}` | Busca por email |
| `POST` | `/api/clientes` | Cadastra novo cliente |
| `PUT` | `/api/clientes/{id}` | Atualiza cliente |
| `DELETE` | `/api/clientes/{id}` | Inativa cliente (soft delete) |
| `PATCH` | `/api/clientes/{id}/reativar` | Reativa cliente |
| `GET` | `/api/clientes/estatisticas` | Estatísticas de clientes |

### 🏪 Restaurantes (`/api/restaurantes`)
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/api/restaurantes` | Lista restaurantes ativos |
| `GET` | `/api/restaurantes/{id}` | Busca por ID |
| `GET` | `/api/restaurantes/buscar?nome=Pizza` | Busca por nome |
| `GET` | `/api/restaurantes/categoria/{categoria}` | Filtra por categoria |
| `GET` | `/api/restaurantes/top-avaliados` | Ordenados por avaliação |
| `GET` | `/api/restaurantes/acima-media` | Acima da média de avaliação |
| `GET` | `/api/restaurantes/avaliacao?min=4.0` | Por avaliação mínima |
| `GET` | `/api/restaurantes/categorias` | Lista todas as categorias |
| `POST` | `/api/restaurantes` | Cadastra restaurante |
| `PUT` | `/api/restaurantes/{id}` | Atualiza restaurante |
| `PATCH` | `/api/restaurantes/{id}/avaliacao` | Atualiza avaliação |
| `DELETE` | `/api/restaurantes/{id}` | Inativa restaurante |
| `PATCH` | `/api/restaurantes/{id}/reativar` | Reativa restaurante |

### 🍕 Produtos (`/api/produtos`)
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/api/produtos` | Lista todos os produtos |
| `GET` | `/api/produtos/{id}` | Busca por ID |
| `GET` | `/api/produtos/restaurante/{id}` | Produtos por restaurante (disponíveis) |
| `GET` | `/api/produtos/restaurante/{id}/todos` | Todos os produtos (incluindo indisponíveis) |
| `GET` | `/api/produtos/categoria/{categoria}` | Filtra por categoria |
| `GET` | `/api/produtos/buscar?nome=Pizza` | Busca por nome |
| `GET` | `/api/produtos/preco?min=10&max=50` | Por faixa de preço |
| `GET` | `/api/produtos/restaurante/{id}/categoria/{cat}` | Por restaurante e categoria |
| `GET` | `/api/produtos/restaurante/{id}/ordenado-preco` | Ordenado por preço crescente |
| `GET` | `/api/produtos/restaurante/{id}/preco-max/{max}` | Por preço máximo |
| `GET` | `/api/produtos/categorias` | Lista todas as categorias |
| `POST` | `/api/produtos` | Cadastra produto |
| `PUT` | `/api/produtos/{id}` | Atualiza produto |
| `PATCH` | `/api/produtos/{id}/disponibilidade` | Altera disponibilidade |
| `PATCH` | `/api/produtos/{id}/disponivel` | Marca como disponível |
| `PATCH` | `/api/produtos/{id}/indisponivel` | Marca como indisponível |
| `DELETE` | `/api/produtos/{id}` | Deleta produto (hard delete) |

### 📦 Pedidos (`/api/pedidos`)
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/api/pedidos` | Lista todos os pedidos |
| `GET` | `/api/pedidos/{id}` | Busca por ID |
| `GET` | `/api/pedidos/cliente/{id}` | Pedidos por cliente |
| `GET` | `/api/pedidos/restaurante/{id}` | Pedidos por restaurante |
| `GET` | `/api/pedidos/status/{status}` | Por status (PENDENTE, CONFIRMADO, etc) |
| `GET` | `/api/pedidos/em-andamento` | Para a cozinha (PENDENTE + CONFIRMADO + PREPARANDO) |
| `GET` | `/api/pedidos/hoje` | Pedidos de hoje |
| `GET` | `/api/pedidos/periodo?inicio=2023-10-01T00:00:00&fim=2023-10-31T23:59:59` | Por período |
| `POST` | `/api/pedidos` | Cria novo pedido |
| `PATCH` | `/api/pedidos/{id}/status` | Atualiza status manualmente |
| `PATCH` | `/api/pedidos/{id}/confirmar` | Confirma pedido (PENDENTE → CONFIRMADO) |
| `PATCH` | `/api/pedidos/{id}/preparar` | Inicia preparação (CONFIRMADO → PREPARANDO) |
| `PATCH` | `/api/pedidos/{id}/entregar` | Marca como entregue (PREPARANDO → ENTREGUE) |
| `PATCH` | `/api/pedidos/{id}/cancelar` | Cancela pedido (qualquer status → CANCELADO) |
| `GET` | `/api/pedidos/estatisticas` | Dashboard com contadores |
| `GET` | `/api/pedidos/restaurante/{id}/total-vendido` | Total vendido por restaurante |

---

## 💾 Banco de Dados H2

- **URL JDBC**: `jdbc:h2:mem:deliverydb`
- **Usuário**: `sa`
- **Senha**: *(vazio)*
- **Console**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
---

## 🎯 Regras de Negócio Implementadas

### ✅ Validações
- Email único  
- Preço > 0  
- Restaurante ativo para aceitar pedidos  
- Cliente ativo para pedir  
- Produto deve pertencer ao restaurante ativo  

### 🔄 Fluxo de Estados
```
PENDENTE → CONFIRMADO → PREPARANDO → ENTREGUE
           ↘ CANCELADO
```

---

## 🏗️ Arquitetura do Projeto

```
📁 src/main/java/com/deliverytech/delivery/
├── DeliveryApiApplication.java
├── controller/
│   ├── ClienteController.java
│   ├── RestauranteController.java
│   ├── ProdutoController.java
│   └── PedidoController.java
├── service/
│   ├── ClienteService.java
│   ├── RestauranteService.java
│   ├── ProdutoService.java
│   └── PedidoService.java
├── repository/
│   ├── ClienteRepository.java
│   ├── RestauranteRepository.java
│   ├── ProdutoRepository.java
│   └── PedidoRepository.java
└── entity/
    ├── Cliente.java
    ├── Restaurante.java
    ├── Produto.java
    └── Pedido.java

📁 src/main/resources/
├── application.properties
└── data.sql
```

---

## 📈 Próximas Etapas
- [ ] JWT Auth  
- [ ] PostgreSQL  
- [ ] Frontend React  
- [ ] WebSocket notifications  
- [ ] JUnit + Mockito tests  
- [ ] Upload de imagens  
- [ ] Avaliação de pedidos  
- [ ] Pagamentos  
- [ ] Deploy cloud  

---

## 👨‍💻 Desenvolvedor

Guilherme Perlasca
📧 perlasca47@gmail.com 
💼 LinkedIn: /in/guiperlasca
🐙 GitHub: /guiperlasca
