# ☕ Coffee API

API REST completa con frontend en Angular para gestionar información de cafés,
basada en el dataset [Coffee Quality Institute](https://github.com/jldbc/coffee-quality-database).

Proyecto desarrollado como práctica de backend con Spring Boot y arquitectura por capas,
incluyendo autenticación OIDC con GitHub y un frontend Angular separado.

---

## 🛠️ Stack tecnológico

**Backend**
- Java 21
- Spring Boot 3.3
- Spring Data JPA + Hibernate
- Spring Security + OAuth2 Client
- PostgreSQL 16
- Lombok
- SpringDoc OpenAPI (Swagger UI)
- Maven

**Frontend**
- Angular 17 (standalone components)
- TypeScript
- HttpClient con sesión OAuth

**Infraestructura**
- Docker + Docker Compose

---

## 📋 Funcionalidades

- ✅ CRUD completo de cafés (crear, leer, actualizar, borrar)
- ✅ Búsqueda parcial por país (`LIKE`, case insensitive)
- ✅ Paginación con `Page<T>` de Spring Data
- ✅ Autenticación con GitHub vía OpenID Connect
- ✅ Sistema de roles: `USER` (solo lectura) y `ADMIN` (lectura + escritura)
- ✅ Documentación interactiva con Swagger UI
- ✅ Frontend Angular con vistas adaptativas según rol
- ✅ Botones de editar/borrar visibles solo para admins
- ✅ CORS configurado para entorno de desarrollo

---

## 🏗️ Arquitectura

```
┌────────────────┐    HTTP / JSON     ┌───────────────────┐
│  Angular SPA   │ ────────────────▶  │   Spring Boot     │
│  (port 4200)   │ ◀───────────────── │   (port 8080)     │
└────────────────┘    Session Cookie   └───────────────────┘
                                                 │
                                                 ▼
                                        ┌────────────────┐
                                        │  PostgreSQL    │
                                        │  (port 5432)   │
                                        └────────────────┘
```

Backend organizado por capas:

```
src/main/java/com/example/coffeeapi/
├── controller/       # Endpoints HTTP (CoffeeController, UserController)
├── service/          # Lógica de negocio
├── repository/       # Acceso a BD (Spring Data JPA)
├── entity/           # Modelo JPA
├── dto/              # Objetos expuestos por la API
└── config/           # SecurityConfig, OpenApiConfig
```

---

## 🚀 Cómo ejecutar

### Requisitos previos

- Docker Desktop
- Node.js LTS (para el frontend)
- Cuenta de GitHub (para configurar OAuth)

### 1. Configurar OAuth en GitHub

1. Ve a [GitHub → Settings → Developer Settings → OAuth Apps](https://github.com/settings/developers).
2. Pulsa **"New OAuth App"**.
3. Rellena:
   - **Homepage URL**: `http://localhost:8080`
   - **Authorization callback URL**: `http://localhost:8080/login/oauth2/code/github`
4. Anota el **Client ID** y genera un **Client Secret**.

### 2. Crear el archivo `.env`

En la raíz del proyecto, crea un archivo llamado `.env`:

```
GITHUB_CLIENT_ID=tu-client-id-aqui
GITHUB_CLIENT_SECRET=tu-client-secret-aqui
```

### 3. Configurar admins

Edita `src/main/resources/application.properties` y añade los usernames de GitHub
que deben tener rol admin:

```properties
coffee.admin-users=tu-username,otro-admin
```

### 4. Arrancar el backend

```bash
docker-compose up --build
```

Esto levanta:
- `coffee-app` en `http://localhost:8080` (la API)
- `coffee-postgres` en `localhost:5432` (la BD)

### 5. Arrancar el frontend

En otra terminal:

```bash
cd frontend
npm install     # solo la primera vez
npm start
```

El frontend queda disponible en `http://localhost:4200`.

---

## 🔌 Endpoints principales

| Método | Ruta                                | Auth      | Descripción                            |
|--------|-------------------------------------|-----------|----------------------------------------|
| GET    | `/coffees/search`                   | Público   | Lista paginada (acepta `country`, `page`, `size`, `sort`) |
| GET    | `/coffees/{id}`                     | Público   | Obtiene un café por id                 |
| POST   | `/coffees`                          | ADMIN     | Crea un café                           |
| PUT    | `/coffees/{id}`                     | ADMIN     | Actualiza un café                      |
| DELETE | `/coffees/{id}`                     | ADMIN     | Borra un café                          |
| GET    | `/me`                               | Logueado  | Datos del usuario actual + rol         |
| GET    | `/oauth2/authorization/github`      | -         | Inicia el flujo de login con GitHub    |
| POST   | `/logout`                           | Logueado  | Cierra la sesión                       |

Documentación interactiva: **`http://localhost:8080/swagger-ui.html`**

---

## 🎨 Frontend

El frontend en Angular se adapta al rol del usuario:

- **Sin login**: lista de cafés + búsqueda + paginación.
- **USER**: lo mismo, con su perfil de GitHub visible.
- **ADMIN**: lo anterior + formulario para crear cafés + botones de editar/borrar por fila.

---

## 🔒 Seguridad

- **OAuth2 / OIDC**: la autenticación se delega en GitHub, no se gestionan contraseñas localmente.
- **Roles**: asignados según una lista configurable en `application.properties`.
- **Doble validación**: la UI oculta acciones según rol, pero el backend también valida con
  `hasRole("ADMIN")` en cada endpoint protegido.
- **Secretos fuera del código**: las credenciales OAuth se inyectan vía variables de entorno
  desde `.env` (excluido del repo).

---

## 📦 Estructura del repositorio

```
api-cafe/
├── src/                          # Código Java del backend
├── frontend/                     # Proyecto Angular
├── docker-compose.yml            # Orquestación de contenedores
├── Dockerfile                    # Imagen del backend
├── pom.xml                       # Dependencias de Maven
└── README.md
```

---

## 📝 Notas

Este proyecto se desarrolló como práctica académica, priorizando la claridad del código
y el aprendizaje de los conceptos sobre la optimización para producción.
