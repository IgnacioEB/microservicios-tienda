# microservicios-tienda

Proyecto de práctica para explorar una arquitectura de microservicios: tres servicios Spring Boot independientes (usuarios, productos y pedidos), cada uno con su propia base de datos Postgres, detrás de un API Gateway que valida la autenticación con JWT.

## Arquitectura

```
                      Cliente (Postman / curl)
                                  │
                                  ▼  :8080
                     ┌─────────────────────────┐
                     │     gateway-service     │
                     │   valida JWT y enruta   │
                     └────────────┬────────────┘
                                  │
             ┌────────────────────┼────────────────────┐
             ▼                    ▼                    ▼
    ┌──────────────────┐ ┌──────────────────┐ ┌──────────────────┐
    │ usuario-service  │ │ producto-service │ │  pedido-service  │
    │ usuarios + login │ │ catálogo y stock │ │ orquesta pedidos │
    └────────┬─────────┘ └────────┬─────────┘ └────────┬─────────┘
             ▼                    ▼                    ▼
        usuarios DB         productos DB          pedidos DB

    pedido-service ──REST──► usuario-service y producto-service
```

- Todo el tráfico entra por `gateway-service`, el único puerto expuesto (`8080`). Los otros tres servicios solo son accesibles dentro de la red de Docker.
- Cada servicio es un proyecto Spring Boot independiente, con su propia base de datos.
- `pedido-service` es el que orquesta: antes de confirmar un pedido valida por HTTP que el usuario exista, que el producto exista y esté activo, y que haya stock suficiente. Después descuenta el stock en `producto-service`.

## Autenticación y autorización

**Autenticación (quién sos)**

1. `POST /api/auth/registro` crea un usuario con rol `USER` (la contraseña se guarda hasheada con BCrypt).
2. `POST /api/auth/login` devuelve un JWT con el email, el rol y el id del usuario.
3. El resto de los endpoints requieren el header `Authorization: Bearer <token>`. El Gateway valida la firma y el vencimiento; si el token falta o es inválido, responde `401`.
4. Si el token es válido, el Gateway reenvía el pedido con los headers `X-User-Id`, `X-User-Email` y `X-User-Rol`, y descarta cualquier header con esos nombres que mande el cliente.

**Autorización (qué podés hacer)**

| Recurso | `USER` | `ADMIN` |
|---|---|---|
| Usuarios (`/api/usuario/**`, `/api/usuarios`) | `403` | acceso total |
| Productos: consultar (`GET`) | sí | sí |
| Productos: crear, descontar stock, desactivar | `403` | sí |
| Pedidos | crea pedidos a su nombre y ve solo los suyos | ve todos |

- El Gateway aplica las reglas por rol y responde `403` si no corresponde.
- `pedido-service` usa `X-User-Id` para saber de quién es cada pedido: el usuario sale del token, no se manda en el body.
- El primer `ADMIN` se crea al arrancar `usuario-service` con `ADMIN_EMAIL` y `ADMIN_PASSWORD` (variables de entorno). El registro público siempre crea `USER`.

`usuario-service` firma los tokens y `gateway-service` los verifica, ambos con el mismo `JWT_SECRET`. Los servicios internos confían en el Gateway, por eso no publican puertos hacia afuera.

## Servicios

| Servicio | Acceso | Responsabilidad |
|---|---|---|
| `gateway-service` | `localhost:8080` | Punto de entrada: enruta `/api/**`, valida el JWT y aplica los permisos por rol |
| `usuario-service` | solo interno | Registro, login (emite el JWT) y CRUD de usuarios |
| `producto-service` | solo interno | CRUD de productos y control de stock |
| `pedido-service` | solo interno | Crea pedidos y orquesta la validación con los otros dos |

Dentro de la red de Docker, los servicios escuchan en el puerto `8080` y se resuelven por nombre (`usuario-service`, `producto-service`, `pedido-service`).

Rutas del Gateway (el prefijo `/api` se elimina antes de reenviar):

| Ruta | Destino |
|---|---|
| `/api/auth/**` | `usuario-service` |
| `/api/usuario/**`, `/api/usuarios/**` | `usuario-service` |
| `/api/producto/**`, `/api/productos/**` | `producto-service` |
| `/api/pedido/**`, `/api/pedidos/**` | `pedido-service` |

## Cómo levantarlo

Requiere Docker y Docker Compose.

1. Cloná el repo.
2. Creá un archivo `.env` en la raíz (podés copiar `.env.example`):
   ```env
   DB_USER=postgres
   DB_PASSWORD=tu_contraseña
   JWT_SECRET=una_clave_en_base64_de_al_menos_32_bytes
   ADMIN_EMAIL=admin@mail.com
   ADMIN_PASSWORD=la_contraseña_del_admin
   ```
   Para generar el `JWT_SECRET`:
   ```bash
   openssl rand -base64 32
   ```
3. Levantá todo:
   ```bash
   docker-compose up --build
   ```

Esto levanta un único contenedor de Postgres con tres bases (`usuarios`, `productos`, `pedidos`, creadas automáticamente con `init-databases.sql`), los tres servicios y el Gateway.

## Endpoints

Todos se consumen por el Gateway (`http://localhost:8080`).

### Públicos

```
POST   /api/auth/registro              → registrar usuario (body: email, password)
POST   /api/auth/login                 → devuelve el JWT (body: email, password)
```

### Con token (`Authorization: Bearer <token>`)

**usuarios** (solo `ADMIN`)
```
GET    /api/usuario/{id}               → obtener por id
GET    /api/usuarios                   → listar todos
PUT    /api/usuario/{id}               → modificar el email
DELETE /api/usuario/{id}               → eliminar usuario
```

**productos** (consultar: cualquier usuario; crear, stock y desactivar: solo `ADMIN`)
```
POST   /api/producto                   → crear producto
GET    /api/producto/{id}              → obtener por id
GET    /api/productos                  → listar todos
PATCH  /api/productos/{id}/stock       → descontar stock (lo usa pedido-service)
PATCH  /api/productos/{id}/desactivar  → soft-delete (desactiva el producto)
```

**pedidos**
```
POST   /api/pedido                     → crear un pedido a nombre del usuario del token (body: productoId, cantidad)
GET    /api/pedido/{id}                → obtener por id (un USER solo ve los suyos)
GET    /api/pedidos                    → USER: solo los suyos; ADMIN: todos (con ?usuario={id} para filtrar)
```

## Probarlo de punta a punta

```
# 1. Iniciar sesión como admin (ADMIN_EMAIL / ADMIN_PASSWORD del .env)
POST "http://localhost:8080/api/auth/login"
{
  "email": "admin@mail.com",
  "password": "la_contraseña_del_admin"
}

# En los pasos con token, agregar el header:
# Authorization: Bearer <token devuelto por el login>

# 2. Crear un producto (token de admin)
POST "http://localhost:8080/api/producto"
{
  "nombre": "JugoBaggio",
  "precio": 2100,
  "stock": 7
}

# 3. Registrar un usuario común
POST "http://localhost:8080/api/auth/registro"
{
  "email": "juan@mail.com",
  "password": "1234"
}

# 4. Iniciar sesión como ese usuario (devuelve su token)
POST "http://localhost:8080/api/auth/login"
{
  "email": "juan@mail.com",
  "password": "1234"
}

# 5. Crear un pedido (token del usuario; el usuario sale del token)
POST "http://localhost:8080/api/pedido"
{
  "productoId": 1,
  "cantidad": 2
}

# 6. Ver sus pedidos (token del usuario: solo ve los suyos)
GET "http://localhost:8080/api/pedidos"

# Con el token del usuario común:
#   POST /api/producto   -> 403
#   GET  /api/usuarios   -> 403
# Sin el header Authorization, cualquier endpoint (salvo registro y login) responde 401
```

## Stack

- Java 21
- Spring Boot (Web, JPA)
- Spring Cloud Gateway (WebFlux)
- JWT (jjwt) y BCrypt (spring-security-crypto)
- PostgreSQL
- Docker / Docker Compose
- RestTemplate (con Apache HttpClient, para soportar `PATCH` entre servicios)

## Estado del proyecto

- [x] CRUD de los 3 servicios
- [x] Comunicación REST entre `pedido-service` y los otros dos
- [x] Dockerizado completo con docker-compose
- [x] API Gateway como único punto de entrada
- [x] Autenticación con JWT (registro, login y validación en el Gateway)
- [x] Autorización por rol (`USER` / `ADMIN`) y pedidos asociados al usuario del token
- [ ] Tests unitarios
