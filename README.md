# XOUND — Servidor

**El backend que hace funcionar XOUND.**

Este es el servicio que gestiona toda la información de la plataforma: usuarios, canciones, eventos, setlists y bandas. Se conecta con la aplicación web y la aplicación móvil.

---

## Qué hace

- **Usuarios y autenticación** — Registro, inicio de sesión y manejo de roles (Administrador, Músico, Super Admin).
- **Canciones** — Crear, editar, buscar y eliminar canciones. Cada admin tiene su propia biblioteca independiente. Integración con APIs externas para obtener letras y acordes automáticamente.
- **Eventos** — Crear eventos con fecha y lugar, armar setlists, publicar y compartir mediante un código único.
- **Bandas** — Crear bandas, generar códigos de invitación, gestionar miembros.
- **Favoritas** — Los músicos pueden marcar canciones como favoritas.
- **Seguridad** — Autenticación con tokens JWT. Cada usuario solo ve la información que le corresponde.

---

## Roles del sistema

| Rol | Qué puede hacer |
|-----|-----------------|
| **Administrador** | Crear canciones, eventos, setlists y gestionar su banda |
| **Músico** | Ver canciones y eventos de su banda, usar modo en vivo, marcar favoritas |
| **Super Admin** | Todo lo anterior + gestionar usuarios y roles del sistema |

---

## Cómo se despliega

El servidor corre dentro de un contenedor Docker en el VPS. Para actualizar:

```
cd ~/service-xound
git pull origin main
docker compose up -d --build
```

El servicio queda disponible en el puerto **8082** y se accede a través de **https://xound.duckdns.org/api/**

---

## Variables de entorno

El archivo `.env` en el servidor contiene las credenciales de la base de datos y el secreto JWT. Nunca se sube al repositorio.

---

Desarrollado por **Zikra Solutions**
