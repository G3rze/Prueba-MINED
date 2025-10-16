# Catálogo de Productos — Prueba Técnica (Java + Spring Boot)

## Descripción 
Este proyecto consiste en una **aplicación backend desarrollada en Java 21 con Spring Boot**, cuyo propósito es **gestionar un catálogo de productos y su inventario disponible**.  
Permite realizar operaciones CRUD (Crear, Leer, Actualizar y Eliminar) sobre los productos, así como mantener la cantidad de stock actualizada.

El enfoque principal de esta implementación es **demostrar una arquitectura limpia, mantenible y escalable**, aplicando buenas prácticas de desarrollo, principios SOLID y separación de capas.

## Puesta en marcha con Docker Compose

> ⚠️ **Requisito:** este comando debe ejecutarse en un entorno **Linux** o **WSL** (Windows Subsystem for Linux).  

Desde la raíz del proyecto ejecuta:

```bash
docker compose down -v
# Opcional: elimina contenedores previos del sistema
# docker ps -aq | xargs -r docker rm -f

docker compose up --build
```

El servicio de la API quedará expuesto en `http://localhost:9988` y PostgreSQL en `localhost:8899`.

## Base de datos para pruebas manuales

Si prefieres levantar solo la base de datos (sin toda la pila de Docker Compose) para hacer pruebas manuales con la API, ejecuta:

```bash
docker run --name some-postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_DB=mydb \
  -v $(pwd)/SCHEMA-CRUD.sql:/docker-entrypoint-initdb.d/SCHEMA-CRUD.sql \
  -p 8899:5432 \
  -d postgres
```

## Endpoints principales

### Productos (`/api/products`)
- `GET /api/products` — Lista todos los productos activos.
- `GET /api/products/{id}` — Obtiene un producto por su identificador.
- `POST /api/products` — Crea un nuevo producto (stock inicial = 1).
- `PUT /api/products/{id}` — Actualiza nombre, descripción y precio.
- `DELETE /api/products/{id}` — Soft delete del producto (marca estado=false y stock=0).

### Stock (`/api/stocks`)
- `GET /api/stocks` — Lista las entradas de stock con producto activo.
- `GET /api/stocks/{id}` — Obtiene el stock por identificador.
- `POST /api/stocks` — Crea un registro de stock para un producto activo.
- `PUT /api/stocks/{id}` — Actualiza cantidad, ubicación o reasigna producto.
- `DELETE /api/stocks/{id}` — Elimina el registro de stock.

## Pruebas automatizadas (JUnit 5)

Las pruebas se implementan con **JUnit 5** y utilizan el contexto completo de Spring Boot (`@SpringBootTest`). Para ejecutarlas es requisito tener disponible una instancia de PostgreSQL con el esquema inicializado. Puedes reutilizar cualquiera de los comandos anteriores (compose o `docker run`) antes de lanzar:

```bash
./mvnw -q test
```

Si necesitas limpiar el estado previo, ejecuta `docker compose down -v` (o elimina el contenedor `some-postgres`) antes de volver a correr las pruebas.

## Notas
- Las variables de conexión a la base de datos se parametrizan mediante variables de entorno (`DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`).
- El puerto de la API es configurable con `SERVER_PORT` (por defecto 9988).

