
# Desafio Técnico — Pedidos + Catálogo (Java)

Implementação de referência para o desafio técnico, com dois microsserviços independentes:
- **orders-service**: Java 21, Spring Boot, Spring Web, Spring Data JPA e PostgreSQL.
- **catalog-service**: Java 21, Spring Boot, Spring Web e Spring Data MongoDB.
- **Docker Compose**: sobe os dois bancos e os serviços.
- **Testes**: testes unitários de domínio/serviço e testes de controller onde aplicável.
- **Documentação de liderança**: guia de code review, estimativa de proposta, ADRs e registro de uso de IA.

> **Nota de transparência:** este pacote foi gerado com assistência de IA. Revise, execute e valide localmente antes de apresentar como entrega própria. O link de conversa compartilhável precisa ser criado pelo candidato na interface do ChatGPT; este arquivo não inventa um link de compartilhamento.

## Arquitetura

```text
Cliente
  |
  v
orders-service :8081 ---- HTTP (consulta em lote) ----> catalog-service :8082
  |                                                    |
  v                                                    v
PostgreSQL                                           MongoDB
```

O serviço de Pedidos nunca confia em preço enviado pelo cliente: recebe IDs e quantidades, consulta o Catálogo em lote e calcula subtotais e totais com `BigDecimal`. O preço consultado é copiado para cada item do pedido, preservando o preço histórico mesmo que o catálogo mude depois.

## Requisitos

- Java 21+
- Docker e Docker Compose
- Maven 3.9+ (opcional se usar `./mvnw`; o wrapper não está incluído neste pacote)

## Executar com Docker Compose

Na raiz:

```bash
docker compose up --build
```

Serviços:
- Pedidos: `http://localhost:8081`
- Catálogo: `http://localhost:8082`
- PostgreSQL: `localhost:5432`
- MongoDB: `localhost:27017`

O compose carrega produtos de exemplo no MongoDB por meio de `catalog-service`? **Não**: para manter o seed explícito e reproduzível, crie produtos com a API conforme abaixo.

### Criar produtos no catálogo

```bash
curl -X POST http://localhost:8082/api/products \
  -H 'Content-Type: application/json' \
  -d '{"sku":"SKU-001","name":"Teclado","description":"Teclado mecânico","price":199.90,"active":true}'
```

```bash
curl -X POST http://localhost:8082/api/products \
  -H 'Content-Type: application/json' \
  -d '{"sku":"SKU-002","name":"Mouse","description":"Mouse sem fio","price":89.90,"active":true}'
```

Consultar:
```bash
curl 'http://localhost:8082/api/products?skus=SKU-001,SKU-002'
```

### Criar pedido

```bash
curl -X POST http://localhost:8081/api/orders \
  -H 'Content-Type: application/json' \
  -d '{
    "customerName":"Maria Silva",
    "items":[
      {"sku":"SKU-001","quantity":1},
      {"sku":"SKU-002","quantity":2}
    ]
  }'
```

Consultar pedidos:
```bash
curl http://localhost:8081/api/orders
curl http://localhost:8081/api/orders/1
```

## API

### Catálogo — `:8082`
- `POST /api/products`: cria produto.
- `GET /api/products/{sku}`: consulta um produto ativo.
- `GET /api/products?skus=SKU-001,SKU-002`: consulta em lote. Retorna apenas produtos ativos encontrados.

### Pedidos — `:8081`
- `POST /api/orders`: cria pedido com preços vigentes consultados no catálogo.
- `GET /api/orders`: lista pedidos.
- `GET /api/orders/{id}`: consulta pedido por ID.

O status inicial é `CREATED`. O desconto é configurado por `orders.discount.percent` (0 a 100) e não está fixo na regra de negócio. Por padrão, é 5%.

## Configuração

Variáveis importantes:
- `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`
- `CATALOG_BASE_URL`
- `SPRING_DATA_MONGODB_URI`
- `ORDERS_DISCOUNT_PERCENT` (ex.: `10`)

O desconto é aplicado sobre o subtotal agregado. Valores monetários usam duas casas decimais e arredondamento `HALF_UP`.

## Testes

Na raiz de cada serviço:

```bash
cd orders-service && mvn test
cd ../catalog-service && mvn test
```

Ou execute ambos no CI. Os testes unitários não exigem bancos externos; testes de integração com containers podem ser adicionados para a pipeline do ambiente-alvo.

## Qualidade e segurança

- Persistência relacional via Spring Data JPA, que gera consultas parametrizadas.
- Não há concatenação de strings SQL.
- Consulta de catálogo em lote, evitando uma chamada ao banco por item.
- Validação de entrada com Bean Validation.
- Respostas de erro padronizadas com `ProblemDetail`.
- Configuração por variáveis de ambiente.
- A chamada entre serviços possui timeout.
- Não inclui autenticação/autorização, observabilidade completa ou retry/circuit breaker: são itens a discutir conforme contexto e requisitos de produção.

## GitFlow sugerido

- `main`: versões prontas para entrega.
- `develop`: integração contínua.
- `feature/<id>-<descricao>`: novas funcionalidades.
- `release/<versao>`: estabilização.
- `hotfix/<id>-<descricao>`: correções urgentes.
- Pull request obrigatório para `develop`/`main`, revisão por outra pessoa e CI verde.

Como este pacote é entregue em ZIP, o histórico real de commits e branches não pode ser pré-criado aqui. Ao importar para Git, crie o repositório e registre commits pequenos, coerentes e verificáveis.

## Documentos

- [`docs/AI-ASSISTED-DEVELOPMENT.md`](docs/AI-ASSISTED-DEVELOPMENT.md)
- [`docs/CODE-REVIEW-GUIDE.md`](docs/CODE-REVIEW-GUIDE.md)
- [`docs/ESTIMATIVA-COMERCIAL.md`](docs/ESTIMATIVA-COMERCIAL.md)
- [`docs/adr/ADR-001-stack-e-fronteiras.md`](docs/adr/ADR-001-stack-e-fronteiras.md)
- [`docs/adr/ADR-002-preco-e-desconto.md`](docs/adr/ADR-002-preco-e-desconto.md)

## Próximos passos antes da entrega

1. Executar `docker compose up --build` e os testes em ambiente local.
2. Validar endpoints e ajustar o que for necessário.
3. Criar repositório Git, aplicar GitFlow e publicar no GitHub.
4. Gerar o link compartilhável real da conversa de IA e adicioná-lo ao documento de IA.
5. Gravar uma demonstração curta (fluxo de criação de produto e pedido, consulta e testes).
