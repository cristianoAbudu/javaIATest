# Guia de Code Review e Clean Code — squads multi-stack

## Objetivo e princípios

Code review é uma prática de redução de risco, compartilhamento de conhecimento e consistência técnica — não uma avaliação pessoal. Os mesmos resultados esperados devem valer para Java, TypeScript/Node.js e .NET, permitindo que cada linguagem aplique seus idiomatismos.

### Checklist de revisão

**1. Comportamento e domínio**
- O PR atende aos critérios de aceite e trata os casos de sucesso, ausência de dados e falhas de dependências?
- Regras de negócio estão explícitas, testáveis e fora de controllers/adapters?
- Valores monetários usam tipo decimal e regra de arredondamento documentada?
- Contratos de API mantêm compatibilidade ou registram uma estratégia de versionamento?

**2. Legibilidade e design**
- Nomes descrevem intenção; métodos têm responsabilidade clara; duplicação relevante foi evitada?
- Dependências apontam para abstrações adequadas e fronteiras entre domínios estão preservadas?
- Não há lógica de negócio em controllers, acesso a dados espalhado ou abstrações sem benefício concreto?
- Configurações variáveis (timeouts, limites, descontos) não estão escondidas em constantes de negócio.

**3. Segurança e privacidade**
- Persistência usa parâmetros/ORM; nunca concatenar entrada de usuário em SQL.
- Validação de entrada ocorre na fronteira; autorização é aplicada quando o sistema exige identidade.
- Segredos não aparecem no código, logs, imagens ou arquivos de configuração versionados.
- Erros não vazam stack traces, credenciais, dados pessoais ou detalhes internos.
- Dependências novas têm origem, manutenção e risco avaliados.

**4. Testes**
- Testes cobrem comportamento e limites relevantes, sem depender de relógio/rede reais desnecessariamente.
- Bugs corrigidos têm teste de regressão.
- Testes são determinísticos e não compartilham estado.
- Testes de integração cobrem contratos com bancos e serviços externos quando o risco justifica.

**5. Operação e entrega**
- Logs e métricas são suficientes para diagnosticar falhas sem registrar dados sensíveis.
- Timeouts e comportamento de falha de dependências são explícitos.
- Migrações são reversíveis ou há plano de recuperação.
- Documentação/configuração foi atualizada; pipeline está verde.

## Bloqueadores de aprovação

Bloquear o PR se houver qualquer um destes pontos:
1. Falha de build, teste ou análise estática obrigatória.
2. Vulnerabilidade conhecida de severidade alta/crítica ou segredo exposto.
3. Possibilidade de corrupção/perda de dados ou quebra de contrato não planejada.
4. Regra central sem teste, ou teste que falha de forma determinística.
5. SQL construído por concatenação de entrada do usuário.
6. Credenciais, tokens ou dados pessoais em logs/versionamento.
7. Alteração incompatível sem plano de migração/rollback.
8. Desvio material dos critérios de aceite sem alinhamento com produto.

Questões de estilo não bloqueantes devem ser comentários/sugestões, preferencialmente automatizadas por formatter e linter. Distinguir claramente `must fix` de `nit`.

## Fluxo recomendado

1. Autor abre PR pequeno, descreve contexto, solução, riscos e como testar.
2. CI roda formatter/linter, testes unitários, testes de integração relevantes e verificação de dependências.
3. Revisor verifica comportamento, fronteiras, segurança e operabilidade antes de detalhes de estilo.
4. Autor responde a cada thread; decisão técnica controversa pode gerar ADR.
5. Merge exige CI verde e ao menos uma aprovação independente. Mudanças críticas exigem revisão de especialista.
6. Após merge, monitorar implantação e manter plano de rollback.

## Governança multi-squad

- Manter um catálogo enxuto de padrões versionados, com dono e data de revisão.
- Definir guardrails automatizados comuns; permitir exceções documentadas com prazo e responsável.
- ADRs registram contexto, decisão, alternativas e consequências, sem transformar cada preferência em política.
- Reunir representantes das squads periodicamente para revisar padrões e métricas: lead time, taxa de falha de mudança, retrabalho e incidentes.
- Mentoria ocorre via pairing, revisão explicada e rotação de revisores, não apenas por aprovação formal.
