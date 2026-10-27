# Dimensionamento técnico — proposta hipotética de 3 semanas

## Resumo executivo

**Recomendação:** entregar em três semanas um MVP demonstrável dos serviços Pedidos e Catálogo, com persistência PostgreSQL/MongoDB, integração REST, testes essenciais, pipeline CI e documentação. Com um desenvolvedor sênior e um pleno, a janela é viável se o escopo permanecer restrito e os ambientes estiverem disponíveis no primeiro dia.

**Estimativa-base:** 15 dias úteis × 2 pessoas = 30 pessoa-dias nominais. Considerando cerimônias, revisão, integração e contingência, reservar aproximadamente 24–26 pessoa-dias de execução efetiva.

## Decomposição do esforço

| Frente | Sênior (dias) | Pleno (dias) | Total |
|---|---:|---:|---:|
| Refinamento, SDD e contratos de API | 1.5 | 1.0 | 2.5 |
| Esqueleto, arquitetura e ambiente local | 1.0 | 1.5 | 2.5 |
| Catálogo + MongoDB | 1.0 | 3.0 | 4.0 |
| Pedidos + PostgreSQL + regra de desconto | 2.0 | 3.0 | 5.0 |
| Integração, erros e resiliência básica | 1.5 | 1.0 | 2.5 |
| Testes, revisão e correções | 1.5 | 2.0 | 3.5 |
| CI/CD e documentação operacional | 1.0 | 1.5 | 2.5 |
| Guia de governança, ADRs e demo | 1.0 | 1.0 | 2.0 |
| **Total estimado** | **10.5** | **14.0** | **24.5** |

O sênior lidera decisões, contratos, desenho do domínio, revisão e riscos de integração. O pleno implementa principalmente endpoints, persistência, testes e documentação com pairing e revisão frequentes. A distribuição é colaborativa; não representa silos rígidos.

## Plano por semana

**Semana 1 — Especificação e fatias verticais**
- Dia 1: critérios de aceite, riscos, contratos e ADRs.
- Dias 2–3: skeleton dos serviços, Docker Compose, persistência.
- Dias 4–5: endpoints de catálogo e primeiros testes; revisão de contrato.

**Semana 2 — Fluxo ponta a ponta**
- Pedidos, cálculo monetário e persistência.
- Consulta de catálogo em lote e preço histórico no item do pedido.
- Testes de criação, produto inexistente/inativo, desconto e falha de catálogo.
- Revisão de segurança e semântica dos erros.

**Semana 3 — Estabilização e entrega**
- CI obrigatório, testes de integração e ajustes de observabilidade.
- Correções, README, guia de review e runbook.
- Demonstração e aceite com stakeholders.

## Premissas e exclusões

**Premissas**
- Requisitos e responsáveis de negócio disponíveis para tirar dúvidas em até um dia útil.
- Contas, runners, repositório e ambientes de cloud acessíveis desde o início.
- Sem migração de dados legados; catálogo inicial pequeno.
- Sem requisitos regulatórios específicos ou dados pessoais sensíveis além do nome do cliente.
- Uma rodada de feedback por semana.

**Fora do escopo-base**
- Autenticação/autorização corporativa, pagamentos, estoque/reserva, cancelamento, mensageria/eventos, alta disponibilidade multi-região, UI, carga de produção e suporte 24×7.
- Deploy em produção depende de aprovações e acessos externos.

## Riscos e mitigação

| Risco | Impacto | Mitigação |
|---|---|---|
| Indisponibilidade do catálogo durante criação | Pedido não pode validar preço | Timeout curto, erro 502 claro, métricas e política de retry limitada após decisão de negócio |
| Preço alterado durante a jornada | Divergência de expectativa | Persistir preço/unitário aplicado no pedido e comunicar regra de preço no aceite |
| Escopo crescer durante as 3 semanas | Atraso ou qualidade menor | Priorizar fluxo principal, registrar change requests e reestimar |
| Testes apenas unitários | Falhas de integração tardias | Reservar tempo para teste de contrato e integração com bancos |
| Pipeline/ambiente indisponível | Bloqueio de entrega | CI desde a primeira semana e execução local documentada |

## Modelo comercial

Precificação final exige taxa-dia por senioridade, encargos, impostos, margem, despesas de cloud/licenças e condições contratuais. Fórmula transparente:

`Preço = (10,5 × taxa-dia sênior + 14 × taxa-dia pleno + custos diretos) × (1 + margem)`

Recomenda-se apresentar a estimativa como faixa com contingência de 15–20%, e não como compromisso fixo antes de validar premissas. Qualquer item fora do escopo deve passar por controle de mudança com impacto em prazo e custo.

## Critérios de aceite propostos

- Fluxo de criação de pedido funciona ponta a ponta.
- Pedido rejeita SKU inexistente/inativo e não aceita preço enviado pelo consumidor.
- Subtotal, desconto e total são persistidos com precisão decimal.
- Consulta de múltiplos SKUs usa uma operação em lote no repositório.
- Testes passam no CI; instruções de execução são reproduzíveis.
- Documentação, ADRs e limitações conhecidas são entregues.
