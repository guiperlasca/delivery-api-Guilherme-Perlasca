<div align="center">

# Delivery Tech API

Sistema completo de delivery desenvolvido com **Spring Boot 3.2.x** e **Java 21 LTS**, inspirado em plataformas como **iFood** e **Uber Eats**.

![Java](https://img.shields.io/badge/Java-21-007396?style=for-the-badge)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.x-6DB33F?style=for-the-badge)
![License](https://img.shields.io/badge/Licença-MIT-blue?style=for-the-badge)
![Status](https://img.shields.io/badge/Status-Em%20Desenvolvimento-yellow?style=for-the-badge)

</div>

---

## 📑 Sumário
- [Status do Projeto](#-status-do-projeto)
- [Tecnologias Utilizadas](#-tecnologias-utilizadas)
- [Como Executar](#-como-executar)
- [Endpoints da API](#-endpoints-da-api)
- [Banco de Dados H2](#-banco-de-dados-h2)
- [Regras de Negócio](#-regras-de-negócio)
- [Arquitetura do Projeto](#-arquitetura-do-projeto)
- [Próximas Etapas](#-próximas-etapas)
- [Desenvolvedor](#-desenvolvedor)

---

## 📊 Status do Projeto

### ✅ Funcionalidades Implementadas
- Entidades JPA (Cliente, Restaurante, Produto, Pedido)
- Repositories com 25+ queries personalizadas
- Services com regras de negócio e validações
- DTOs e validação com Spring Validation
- Tratamento Global de Exceções (`@ControllerAdvice`) com respostas de erro padronizadas**
- Documentação de API completa com Swagger (OpenAPI)**
- Controllers REST com 40+ endpoints (incluindo Clientes, Pedidos, Produtos e Relatórios)
- Soft delete e controle de disponibilidade
- Máquina de estados para pedidos
- Dados de teste (super-heróis, restaurantes famosos)
- Relatórios e estatísticas

---

## 🛠️ Tecnologias Utilizadas

| Tecnologia | Função |
|-----------|-------|
| Java 21 | Linguagem |
| Spring Boot 3.2.x | Framework principal |
| Spring Web | Exposição REST |
| Spring Data JPA | Persistência |
| H2 Database | Banco de desenvolvimento |
| Spring Validation | Validação de dados |
| ModelMapper | Conversão DTO ↔ Entity |
| Maven | Gerenciamento de dependências |
| SpringDoc (OpenAPI) | Documentação de API (Swagger) |

---

## 🚀 Como Executar

### Pré-requisitos
- JDK 21 instalado
- Git instalado

### Passos

```bash
git clone https://github.com/seuusuario/delivery-api.git
cd delivery-api
./mvnw spring-boot:run
```

### Acesse:
| Recurso | URL |
|--------|-----|
| API Base | http://localhost:8080/api/ |
| H2 Console | http://localhost:8080/h2-console |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| API Docs (JSON) | http://localhost:8080/api-docs |
| Health Check | http://localhost:8080/health |
---

## 📡 Endpoints da API

### 🧑‍💼 Clientes (`/api/clientes`)
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/clientes` | Lista todos os clientes ativos |
| GET | `/api/clientes/{id}` | Busca cliente por ID |
| GET | `/api/clientes/buscar?nome=João` | Busca por nome |
| GET | `/api/clientes/email/{email}` | Busca por email |
| POST | `/api/clientes` | Cadastra novo cliente |
| PUT | `/api/clientes/{id}` | Atualiza cliente |
| DELETE | `/api/clientes/{id}` | Inativa cliente (soft delete) |
| PATCH | `/api/clientes/{id}/reativar` | Reativa cliente |
| GET | `/api/clientes/estatisticas` | Estatísticas de clientes |

### 🏪 Restaurantes (`/api/restaurantes`)
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/restaurantes` | Lista restaurantes (filtros: `categoria`, `ativo`) |
| GET | `/api/restaurantes/{id}` | Busca por ID |
| GET | `/api/restaurantes/buscar?nome=Pizza` | Busca por nome |
| GET | `/api/restaurantes/categoria/{categoria}` | Filtra por categoria |
| GET | `/api/restaurantes/top-avaliados` | Ordenados por avaliação |
| GET | `/api/restaurantes/acima-media` | Acima da média de avaliação |
| GET | `/api/restaurantes/avaliacao?min=4.0` | Por avaliação mínima |
| GET | `/api/restaurantes/categorias` | Lista todas as categorias |
| GET | `/api/restaurantes/{id}/taxa-entrega/{cep}` | Calcula taxa de entrega |
| GET | `/api/restaurantes/proximos/{cep}` | Busca restaurantes próximos (simulado) |
| POST | `/api/restaurantes` | Cadastra restaurante |
| PUT | `/api/restaurantes/{id}` | Atualiza restaurante |
| PATCH | `/api/restaurantes/{id}/avaliacao` | Atualiza avaliação |
| PATCH | `/api/restaurantes/{id}/status` | Ativa ou desativa um restaurante |
| DELETE | `/api/restaurantes/{id}` | Inativa restaurante |
| PATCH | `/api/restaurantes/{id}/reativar` | Reativa restaurante |

### 🍕 Produtos(`/api/produtos`)
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/produtos` | Lista todos os produtos |
| GET | `/api/produtos/{id}` | Busca por ID |
| GET | `/api/restaurantes/{restauranteId}/produtos` | Produtos por restaurante (disponíveis) |
| GET | `/api/produtos/restaurante/{id}/todos` | Todos os produtos (incluindo indisponíveis) |
| GET | `/api/produtos/categoria/{categoria}` | Filtra por categoria |
| GET | `/api/produtos/buscar?nome=Pizza` | Busca por nome |
| GET | `/api/produtos/preco?min=10&max=50` | Por faixa de preço |
| GET | `/api/produtos/restaurante/{id}/categoria/{cat}` | Por restaurante e categoria |
| GET | `/api/produtos/restaurante/{id}/ordenado-preco` | Ordenado por preço crescente |
| GET | `/api/produtos/restaurante/{id}/preco-max/{max}` | Por preço máximo |
| GET | `/api/produtos/categorias` | Lista todas as categorias |
| POST | `/api/produtos` | Cadastra produto |
| PUT | `/api/produtos/{id}` | Atualiza produto |
| PATCH | `/api/produtos/{id}/disponibilidade` | Altera disponibilidade |
| PATCH | `/api/produtos/{id}/disponivel` | Marca como disponível |
| PATCH | `/api/produtos/{id}/indisponivel` | Marca como indisponível |
| DELETE | `/api/produtos/{id}` | Deleta produto (hard delete) |

### 📦 Pedidos (`/api/pedidos`)
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/pedidos` | Lista todos os pedidos (filtros: `status`, `data`) |
| GET | `/api/pedidos/{id}` | Busca por ID |
| GET | `/api/clientes/{clienteId}/pedidos` | Histórico de pedidos do cliente |
| GET | `/api/restaurantes/{restauranteId}/pedidos` | Pedidos por restaurante (Alias) |
| GET | `/api/pedidos/status/{status}` | Por status |
| GET | `/api/pedidos/em-andamento` | Para a cozinha |
| POST | `/api/pedidos` | Cria novo pedido (com itens) |
| POST | `/api/pedidos/calcular` | Calcula total do pedido (sem salvar) |
| PATCH | `/api/pedidos/{id}/status` | Atualiza status |
| PATCH | `/api/pedidos/{id}/confirmar` | Confirma pedido |
| PATCH | `/api/pedidos/{id}/preparar` | Inicia preparação |
| PATCH | `/api/pedidos/{id}/entregar` | Marca como entregue |
| PATCH | `/api/pedidos/{id}/cancelar` | Cancela pedido (com motivo) |
| DELETE | `/api/pedidos/{id}` | Cancela pedido (sem motivo) |

### 📊 Relatórios (`/api/relatorios`)
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/relatorios/vendas-por-restaurante` | Total vendido por restaurante |
| GET | `/api/relatorios/produtos-mais-vendidos` | Top produtos (simulado) |
| GET | `/api/relatorios/clientes-ativos` | Contagem de clientes ativos |
| GET | `/api/relatorios/pedidos-por-periodo` | Filtra pedidos por data/hora e status |
| GET | `/api/relatorios/pedidos/estatisticas` | Dashboard de status de pedidos |

---

## 💾 Banco de Dados H2

```
URL JDBC: jdbc:h2:mem:deliverydb
Usuário: sa
Senha: (vazio)
Console: http://localhost:8080/h2-console
```

---

## 🎯 Regras de Negócio

- Cliente e restaurante devem estar ativos
- Produto precisa pertencer ao restaurante
- Preço deve ser maior que zero
- Controle de estado de pedido via máquina de estados

### Fluxo
```
PENDENTE → CONFIRMADO → PREPARANDO → ENTREGUE
           ↘ CANCELADO
```

---

## 🏗️ Arquitetura do Projeto

```
src/main/java/com/deliverytech/delivery/
├── config/
├── controller/
├── dto/
├── entity/
├── exception/
├── repository/
└── service/
```

---

## 📈 Próximas Etapas

- [✅] Tratamento Global de Exceções (@ControllerAdvice)
- [✅] Documentação de API com Swagger (OpenAPI)
- [ ] Autenticação JWT + Refresh Token
- [ ] Migrar banco para PostgreSQL
- [ ] Sistema de avaliação + reputação
- [ ] Upload de imagens (S3 / Firebase)
- [ ] WebSockets para pedidos em tempo real
- [ ] Pagamentos (Pix / Cartão)
- [ ] Deploy (Railway, Render, AWS ou Azure)

---

## 👨‍💻 Desenvolvedor

| Nome | Contato |
|------|---------|
| **Guilherme Perlasca** | perlasca47@gmail.com |
| **LinkedIn** | https://linkedin.com/in/guiperlasca |
| **GitHub** | https://github.com/guiperlasca |

---
