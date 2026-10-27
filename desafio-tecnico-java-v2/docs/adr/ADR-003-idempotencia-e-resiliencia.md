# ADR-003 — Idempotência e resiliência na integração com o catálogo

## Contexto

O Orders Service depende do Catalog Service para obter preços vigentes. Clientes, gateways e filas podem repetir uma requisição após timeout, e o catálogo pode ficar temporariamente indisponível.

## Decisão

1. `POST /api/orders` exige o header `Idempotency-Key`.
2. A chave é persistida com restrição única no PostgreSQL.
3. Uma requisição repetida com a mesma chave retorna o pedido já criado sem consultar novamente o catálogo.
4. A chamada ao catálogo possui timeout, retry limitado a falhas de transporte e circuit breaker.
5. Falhas do catálogo são expostas ao cliente como `502 Bad Gateway`, sem criar pedido parcial.
6. A consulta dos pedidos usa `EntityGraph`/query dedicada para carregar itens explicitamente, mantendo `open-in-view=false`.

## Alternativas consideradas

- Confiar apenas em retries do cliente: rejeitado porque pode gerar pedidos duplicados.
- Colocar toda a operação em uma transação distribuída: rejeitado por complexidade e acoplamento.
- Retry ilimitado: rejeitado porque pode ampliar indisponibilidade e latência.
- Manter `open-in-view=true`: rejeitado porque mascara fronteiras transacionais e pode causar consultas inesperadas durante serialização.

## Consequências

### Positivas

- Redução de duplicidades.
- Maior previsibilidade diante de falhas transitórias.
- Menor risco de efeito cascata entre microsserviços.
- Fronteira transacional explícita.

### Negativas

- É necessário armazenar a chave de idempotência.
- Circuit breaker/retry precisam de parâmetros adequados ao ambiente.
- A semântica de idempotência deve ser definida também para futuras operações de alteração/cancelamento.
