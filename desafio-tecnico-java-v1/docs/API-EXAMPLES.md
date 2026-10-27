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

`POST /api/orders`
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
