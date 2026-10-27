# ADR-002 — Fonte de preço, preço histórico e desconto

- **Status:** Aceita para o MVP
- **Contexto:** O pedido deve validar produtos e preços vigentes no momento da criação; desconto deve ser configurável.
- **Decisão:** O cliente envia SKU e quantidade, nunca preço confiável. Pedidos consulta SKUs distintos em uma única chamada e persiste o preço unitário retornado. O desconto percentual vem de `ORDERS_DISCOUNT_PERCENT`; os cálculos usam `BigDecimal`, duas casas decimais e `HALF_UP`.
- **Consequências positivas:** reduz manipulação de preço, evita N+1 chamadas e preserva o valor aplicado no pedido.
- **Consequências negativas/riscos:** a chamada HTTP não garante consistência transacional com futuras alterações do catálogo; o contrato assume que o preço retornado é vigente no momento da consulta.
- **Alternativas:** eventos de catálogo, snapshot versionado, reserva de preço com validade, ou mecanismo de cotação separado.
- **Próximo passo para produção:** definir se preço precisa de cotação com TTL, trilha de auditoria e regras de arredondamento fiscal.
