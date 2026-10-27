# Uso de IA no ciclo de desenvolvimento

## Ferramenta e motivação

Ferramenta usada nesta elaboração: **ChatGPT**, como assistente de especificação, scaffolding de código, revisão de decisões e produção inicial da documentação. A escolha permite trabalhar iterativamente sobre requisitos longos, pedir explicações e revisar artefatos em linguagem natural.

**Link compartilhável da conversa:** [ADICIONAR LINK REAL DE COMPARTILHAMENTO AQUI]. O candidato deve gerar o link pela interface da ferramenta usada. Não é possível fabricar um link de compartilhamento válido a partir do ZIP.

## Aplicação por etapa

1. **Especificação:** requisitos foram decompostos em serviços, endpoints, persistência, critérios de aceite, documentação e pipeline.
2. **Arquitetura:** proposta de dois serviços Java Spring Boot, PostgreSQL para pedidos e MongoDB para catálogo.
3. **Implementação:** geração inicial de classes de domínio, DTOs, controllers, serviços e configurações.
4. **Testes:** sugestão de testes para regra de desconto, preço consultado no catálogo e consulta em lote.
5. **DevOps:** esqueleto de GitHub Actions para executar `mvn verify` em pull requests.
6. **Governança:** primeira versão do guia de code review, ADRs e dimensionamento de proposta.

## Prompts representativos

- “Decomponha estes requisitos em dois microsserviços Java com Clean Architecture pragmática, contratos REST explícitos e critérios de aceite testáveis.”
- “Revise o fluxo de criação de pedido para garantir que o preço venha do catálogo, a consulta seja em lote e os valores monetários usem BigDecimal.”
- “Proponha testes unitários para desconto configurável, produto ausente e chamada única de consulta em lote.”
- “Revise as mensagens de erro para que sejam claras ao consumidor da API e não exponham detalhes internos.”
- “Produza um guia de code review multi-stack com bloqueadores objetivos e uma estimativa de três semanas para um sênior e um pleno.”

## Validação humana aplicada / obrigatória

A saída de IA não deve ser considerada evidência de correção por si só. Antes da entrega:
- Rodar `mvn verify` nos dois serviços e registrar os resultados.
- Subir Docker Compose e executar o fluxo de ponta a ponta com os exemplos do README.
- Confirmar respostas HTTP, persistência e comportamento em falha de catálogo.
- Revisar dependências, configuração de segurança, logs e tratamento de exceções.
- Conferir o código gerado contra os requisitos e corrigir inconsistências encontradas.
- Registrar limitações e decisões de arquitetura; não alegar que testes foram executados se não foram.

## Limitações conhecidas desta versão

- O código é um ponto de partida gerado por IA e precisa ser compilado e validado no ambiente de entrega.
- Persistência usa `ddl-auto: update` para facilitar demonstração; em produção, preferir migrações versionadas.
- Não foram implementados autenticação, autorização, rate limiting, circuit breaker, tracing distribuído nem deploy real em cloud.
- O link de conversa e o histórico Git real devem ser adicionados pelo candidato.
