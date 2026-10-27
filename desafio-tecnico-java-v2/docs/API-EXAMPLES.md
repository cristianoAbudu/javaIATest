# Exemplos de API

## Produto

`POST /api/products`
```json
{
  "sku": "SKU-001",
  "name": "Teclado",
  "description": "Teclado mecânico",
  "price": 199.90,
  "active": true
}
```

`GET /api/products?skus=SKU-001,SKU-002`

## Pedido

`POST /api/orders` (requer header `Idempotency-Key`)

```bash
curl -X POST http://localhost:8081/api/orders \
  -H 'Idempotency-Key: demo-001' \
  -H 'Content-Type: application/json' \
  -d '{
    "customerName": "Maria Silva",
    "items": [
      {"sku": "SKU-001", "quantity": 1},
      {"sku": "SKU-002", "quantity": 2}
    ]
  }'
```
```json
{
  "customerName": "Maria Silva",
  "items": [
    {"sku": "SKU-001", "quantity": 1},
    {"sku": "SKU-002", "quantity": 2}
  ]
}
```

O consumidor não envia subtotal, desconto, total, preço unitário nem status; o serviço calcula/define esses campos.


## Listagem paginada de pedidos

```bash
curl "http://localhost:8081/api/orders?page=0&size=20&sort=createdAt,desc"
```

A API retorna o envelope padrão do Spring Data `Page`; use `page` começando em zero e `size` até 100.
