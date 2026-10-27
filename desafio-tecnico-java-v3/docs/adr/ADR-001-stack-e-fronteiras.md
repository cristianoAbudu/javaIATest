# ADR-001 — Stack e fronteiras dos serviços

- **Status:** Aceita para o MVP
- **Contexto:** O desafio pede uma API de pedidos com banco relacional e catálogo com banco não relacional, consumido por HTTP.
- **Decisão:** Java 21 + Spring Boot nos dois serviços; PostgreSQL + Spring Data JPA em Pedidos; MongoDB + Spring Data MongoDB em Catálogo. Cada serviço é dono de seus dados. Pedidos consulta o catálogo por endpoint REST em lote.
- **Alternativas consideradas:** um monólito modular; Node.js/TypeScript; .NET; armazenamento relacional nos dois contextos.
- **Consequências positivas:** stack consistente, tipagem estática, ecossistema maduro, separação explícita de responsabilidades e consulta em lote.
- **Consequências negativas/riscos:** operação de dois bancos e dois serviços aumenta custo; HTTP cria dependência de disponibilidade; é necessário timeout e monitoramento.
- **Revisitar quando:** volume, latência, disponibilidade ou necessidade de transações distribuídas mudarem.
