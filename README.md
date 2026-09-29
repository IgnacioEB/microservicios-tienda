# microservicios-tienda

Proyecto de práctica para explorar una arquitectura de microservicios simple: tres servicios Spring Boot independientes, cada uno con su propia base de datos, comunicándose entre sí por REST.

## Arquitectura

```
                ┌─────────────────────┐
                │   pedido-service     │
                │  (orquesta el pedido)│
                └──────────┬───────────┘
                   ┌────────┴────────┐
                   ▼                 ▼
        ┌──────────────────┐  ┌──────────────────┐
        │  usuario-service  │  │  producto-service │
        │  CRUD de usuarios │  │ Catálogo y stock  │
        └──────────────────┘  └──────────────────┘
                   │                 │
                   ▼                 ▼
              usuarios DB       productos DB

              pedido-service tiene su propia base: pedidos DB
```

Cada servicio es un proyecto Spring Boot independiente, con su propia base de datos Postgres. `pedido-service` es el único que conoce a los otros dos: antes de confirmar un pedido, valida por HTTP que el usuario y el producto existan, y que haya stock suficiente.

## Servicios

| Servicio | Puerto (host) | Responsabilidad |
|---|---|---|
| `usuario-service` | 8081 | CRUD de usuarios |
| `producto-service` | 8082 | CRUD de productos, control de stock |
| `pedido-service` | 8083 | Crea pedidos, orquesta la validación con los otros dos |

Internamente, dentro de la red de Docker, los tres escuchan en el puerto `8080` y se resuelven por nombre de servicio (`usuario-service`, `producto-service`, `pedido-service`).

## Cómo levantarlo

Requiere Docker y Docker Compose.

1. Cloná el repo
2. Creá un archivo `.env` en la raíz con:
   ```env
   DB_USER=postgres
   DB_PASSWORD=tu_contraseña
   ```
3. Levantá todo:
   ```bash
   docker-compose up --build
   ```

Esto levanta un único contenedor de Postgres con tres bases (`usuarios`, `productos`, `pedidos`, creadas automáticamente vía `init-databases.sql`) más los tres servicios.

## Endpoints

### `usuario-service` (puerto 8081)

```
POST   /usuario               → crear usuario
GET    /usuario/{id}          → obtener por id
GET    /usuarios              → listar todos
PUT    /usuario/{id}          → modifica usuario
DELETE /usuario/{id}          → elimina usuario
```

### `producto-service` (puerto 8082)

```
POST   /producto                     → crear producto
GET    /producto/{id}                → obtener por id
GET    /productos                    → listar todos
PATCH  /productos/{id}/stock         → descontar stock
PATCH  /productos/{id}/desactivar    → soft-delete('desactiva' el producto)
```

### `pedido-service` (puerto 8083)

```
POST   /pedido                → crear un pedido
GET    /pedido/{id}           → obtener por id
GET    /pedidos               → listar todos(y con parámetro opcional para listar los pedidos de un usuario específico).    
```

## Probarlo de punta a punta

```bash
# 1. Crear un usuario
POST "http://localhost:8081/usuarios?nombre=Juan&email=juan@mail.com"

# 2. Crear un producto
"http://localhost:8082/producto"
{
"nombre":"JugoBaggio",
"precio":2100,
"stock":7
}

# 3. Crear un pedido (usando los ids devueltos arriba)
POST "http://localhost:8083/pedido"
{
"productoId":1,
"usuarioId":1,
"cantidad":2
}

# 4. Ver el historial de ese usuario
GET "http://localhost:8083/pedidos?usuario=1"
```

## Stack

- Java 21
- Spring Boot (Web, JPA)
- PostgreSQL
- Docker / Docker Compose
- RestTemplate (con Apache HttpClient, para soportar `PATCH` entre servicios)

## Estado del proyecto

- [x] CRUD de los 3 servicios
- [x] Comunicación REST entre `pedido-service` y los otros dos
- [x] Dockerizado completo con docker-compose
- [ ] Tests unitarios
