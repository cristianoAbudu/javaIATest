# Production Readiness — correções aplicadas

Esta versão do desafio incorpora os principais pontos identificados na revisão técnica.

## Correções implementadas

- Correção do risco de `LazyInitializationException` na listagem/detalhamento de pedidos com `open-in-view=false`.
- `GET /orders` e `GET /orders/{id}` carregam itens explicitamente.
- Idempotência no `POST /orders` por meio de `Idempotency-Key` e constraint única no PostgreSQL.
- Testes unitários para idempotência.
- Teste de integração com PostgreSQL/Testcontainers cobrindo o endpoint de listagem com itens.
- Timeout + retry limitado + circuit breaker na integração com catálogo.
- Spring Boot Actuator para health e métricas.
- Correlation ID no Orders Service.
- Correção dos `EXPOSE` dos Dockerfiles para 8081/8082.
- README e exemplos atualizados para refletir o comportamento real.
- ADR específico documentando idempotência e resiliência.

## Pontos ainda deliberadamente fora do escopo

- Autenticação/autorização OAuth2/JWT.
- Tracing distribuído com OpenTelemetry.
- SAST/SCA/SBOM/container scanning no CI.
- Deploy efetivo em AWS/Azure/GCP.
- Testes de contrato entre serviços.

Esses itens continuam sendo temas para a defesa técnica e para uma evolução posterior do projeto.
