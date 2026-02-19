# Xound — Backend

API REST para gestión de eventos musicales, canciones y setlists. Construida con Spring Boot 3 y PostgreSQL.

---

## Tecnologías

| Tecnología | Versión |
|---|---|
| Java | 21 |
| Spring Boot | 3.4.2 |
| Spring Security | — |
| Spring JDBC (JdbcTemplate) | — |
| PostgreSQL | — |
| JWT (jjwt) | 0.12.6 |
| Lombok | — |
| Maven | — |

---

## Requisitos previos

- Java 21+
- Maven 3.8+
- PostgreSQL corriendo en `localhost:5432`

---

## Configuración

Edita `src/main/resources/application.properties` con tus credenciales de base de datos:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/xound_db
spring.datasource.username=postgres
spring.datasource.password=TU_PASSWORD_AQUI
```

Cambia también el secreto JWT en producción:

```properties
jwt.secret=b70eddc7d1604950d5e80e21d4275bd38ee3d28358bef752bf27dbbc719a32e1ccd4955f52672cf4d2955b0828852f902dcd85cbca4dc324db8470dd133d78f4
jwt.expiration=86400000
```

> El schema SQL se ejecuta automáticamente al arrancar (`spring.sql.init.mode=always`), creando las tablas e insertando los roles base.

---

## Ejecutar el proyecto

```bash
mvn spring-boot:run
```

El servidor queda disponible en `http://localhost:8080`.

---

## Base de datos

### Tablas

| Tabla | Descripción |
|---|---|
| `roles` | Roles del sistema (ADMIN, MUSICIAN, GUEST) |
| `users` | Usuarios registrados |
| `songs` | Canciones con tono, letra y notas |
| `events` | Eventos musicales con fecha, venue y código de compartir |
| `setlist_songs` | Relación evento ↔ canción con orden (posición) |

### Roles iniciales

Los siguientes roles se insertan automáticamente:

- `ADMIN`
- `MUSICIAN`
- `GUEST`

---

## Autenticación

La API usa **JWT stateless**. Para acceder a los endpoints protegidos, incluye el token en el header:

```
Authorization: Bearer <token>
```

---

## Endpoints

### Usuarios — `/api/users`

| Método | Ruta | Auth | Descripción |
|---|---|---|---|
| `POST` | `/api/users/register` | Pública | Registrar nuevo usuario |
| `POST` | `/api/users/login` | Pública | Login, devuelve JWT |
| `GET` | `/api/users` | Autenticado | Listar todos los usuarios |

**Body registro:**
```json
{
  "name": "Juan",
  "email": "juan@email.com",
  "password": "1234",
  "roleId": 2
}
```

**Body login:**
```json
{
  "email": "juan@email.com",
  "password": "1234"
}
```

---

### Canciones — `/api/songs`

| Método | Ruta | Auth | Descripción |
|---|---|---|---|
| `GET` | `/api/songs` | Autenticado | Listar todas las canciones |
| `GET` | `/api/songs/{id}` | Autenticado | Obtener canción por ID |
| `GET` | `/api/songs/search?title=` | Autenticado | Buscar por título |
| `POST` | `/api/songs` | ADMIN | Crear canción |
| `PUT` | `/api/songs/{id}` | ADMIN | Actualizar canción |
| `DELETE` | `/api/songs/{id}` | ADMIN | Eliminar canción (soft delete) |

**Body canción:**
```json
{
  "title": "Bohemian Rhapsody",
  "artist": "Queen",
  "tone": "Bb",
  "content": "Is this the real life...",
  "notes": "Entrada con piano"
}
```

---

### Eventos — `/api/events`

| Método | Ruta | Auth | Descripción |
|---|---|---|---|
| `GET` | `/api/events` | Autenticado | Listar todos los eventos |
| `GET` | `/api/events/published` | Autenticado | Listar eventos publicados |
| `GET` | `/api/events/{id}` | Autenticado | Obtener evento por ID |
| `GET` | `/api/events/share/{code}` | Pública | Obtener evento por código de compartir |
| `POST` | `/api/events` | ADMIN | Crear evento |
| `PUT` | `/api/events/{id}` | ADMIN | Actualizar evento |
| `PUT` | `/api/events/{id}/publish` | ADMIN | Publicar / despublicar evento |
| `DELETE` | `/api/events/{id}` | ADMIN | Eliminar evento |

**Body evento:**
```json
{
  "title": "Concierto de Navidad",
  "eventDate": "2025-12-24T20:00:00",
  "venue": "Teatro Municipal"
}
```

---

### Setlist — `/api/events/{eventId}/setlist`

| Método | Ruta | Auth | Descripción |
|---|---|---|---|
| `GET` | `/api/events/{eventId}/setlist` | Autenticado | Ver setlist del evento |
| `POST` | `/api/events/{eventId}/setlist` | ADMIN | Agregar canción al setlist |
| `DELETE` | `/api/events/{eventId}/setlist/{songId}` | ADMIN | Quitar canción del setlist |
| `PUT` | `/api/events/{eventId}/setlist/reorder` | ADMIN | Reordenar setlist |

**Body agregar canción:**
```json
{
  "songId": 3
}
```

**Body reordenar:**
```json
[
  { "songId": 3, "position": 1 },
  { "songId": 7, "position": 2 }
]
```

---

### Roles — `/api/roles`

| Método | Ruta | Auth | Descripción |
|---|---|---|---|
| `GET` | `/api/roles` | Pública | Listar todos los roles |

---

## Estructura del proyecto

```
src/main/java/com/xound/
├── config/
│   ├── SecurityConfig.java     # Reglas de seguridad y rutas protegidas
│   └── CorsConfig.java         # Configuración CORS
├── controller/                 # Endpoints REST
├── model/                      # Entidades (POJOs)
├── repository/                 # Acceso a datos con JdbcTemplate
├── security/
│   ├── JwtUtil.java            # Generación y validación de tokens
│   └── JwtFilter.java          # Filtro que intercepta requests
├── service/                    # Lógica de negocio
└── XoundApplication.java
```

---

## Permisos por rol

| Acción | ADMIN | MUSICIAN / GUEST |
|---|---|---|
| Registrarse / Login | Si | Si |
| Ver canciones y eventos | Si | Si |
| Ver eventos publicados | Si | Si |
| Ver setlist | Si | Si |
| Crear / editar / eliminar canciones | Si | No |
| Crear / editar / eliminar eventos | Si | No |
| Gestionar setlist | Si | No |
| Publicar evento | Si | No |
