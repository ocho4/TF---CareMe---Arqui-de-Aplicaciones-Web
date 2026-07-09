<div align="center">

<img src="https://webdesignfyp.com/wp-content/uploads/2026/06/CareMe-logo-header.png" alt="CareMe Logo" width="260"/>

# CareMe

**Plataforma web que conecta a familias con cuidadores profesionales, para el cuidado especializado de adultos mayores**

*Startup MediTec · Universidad Peruana de Ciencias Aplicadas · Arquitectura Web*

---

![Java](https://img.shields.io/badge/Java-17-007396?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![Angular](https://img.shields.io/badge/Angular-22-DD0031?style=flat-square&logo=angular&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?style=flat-square&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=flat-square&logo=docker&logoColor=white)
![AWS](https://img.shields.io/badge/Deploy-AWS-FF9900?style=flat-square&logo=amazonaws&logoColor=white)
![JWT](https://img.shields.io/badge/Auth-JWT-black?style=flat-square&logo=jsonwebtokens&logoColor=white)

</div>

---

## Equipo — MediTec

| Integrante | Código | Rol |
|---|---|---|
| Carlos Fabrizio Lara Talla | U202114534 | Ingeniería de Sistemas |
| Mattias Adrián Concha Ochoa | U202318269 | Ingeniería de Software |

---

## Sobre el proyecto

CareMe resuelve un problema real: encontrar y contratar cuidadores confiables para adultos mayores suele depender de referidos informales, sin garantías de calidad, sin transparencia en el precio, y sin un canal claro para coordinar el cuidado día a día.

La plataforma conecta a **familiares** que necesitan contratar cuidado especializado con **cuidadores profesionales** verificados por un equipo administrativo, cubriendo todo el ciclo: búsqueda, cotización, contratación, seguimiento del servicio, pago y calificación mutua.

---

## Arquitectura del repositorio

Este es un **monorepo** con dos proyectos independientes:

```
TF-CareMe/
├── backend/     Spring Boot 3 + PostgreSQL — API REST
└── frontend/    Angular 22 + Angular Material — SPA
```

Cada uno se despliega como un contenedor Docker independiente (`backend/Dockerfile`, `frontend/Dockerfile`), orquestados junto a la base de datos vía `backend/docker-compose.yml`. El frontend sirve los archivos estáticos con Nginx, que además reenvía las peticiones `/api` al backend (mismo origen, sin problemas de CORS).

### Stack tecnológico

**Backend**
- Java 17 + Spring Boot 3.5
- Spring Security con autenticación JWT (roles: familiar, cuidador, admin)
- PostgreSQL + Hibernate/JPA
- ModelMapper, Maven, Swagger/OpenAPI

**Frontend**
- Angular 22 (standalone components) + Angular Material
- TypeScript 6, RxJS, Reactive Forms
- ng2-charts / Chart.js para visualización de datos

**Infraestructura**
- Docker + Docker Compose
- Despliegue en AWS (EC2)

---

## Épicas

| Código | Épica |
|---|---|
| EP-001 | Gestión de contrataciones y seguimiento de cuidadores |
| EP-003 | Comunicación entre familiar y cuidador |
| EP-005 | Notificaciones, calendario y panel personalizado |
| EP-006 | Registro, perfil y seguridad de cuentas |
| EP-007 | Costos variables y pagos |

---

## Historias de usuario implementadas

| HU | Título | Épica |
|---|---|---|
| US-033 | Registro de usuario | EP-006 |
| US-034 | Inicio de sesión (JWT) | EP-006 |
| US-036 | Recuperación de cuenta | EP-006 |
| US-057 | Verificación y aprobación de cuidadores (admin) | EP-006 |
| US-001 | Solicitud, confirmación y ciclo completo del servicio | EP-001 |
| US-011 | Búsqueda filtrada de cuidadores | EP-001 |
| US-043 | Búsqueda por condición médica específica | EP-001 |
| US-023 | Cancelación de servicio contratado | EP-001 |
| US-052 | Gestión de pacientes a cargo del familiar | EP-001 |
| US-053 | Seguimiento y gestión de tareas del servicio | EP-001 |
| US-054 | Calificación bidireccional (familiar ↔ cuidador) | EP-001 |
| US-061 | Cuidadores favoritos | EP-001 |
| US-005 | Chat en tiempo real entre familiar y cuidador | EP-003 |
| US-009 | Recordatorio de servicio programado | EP-005 |
| US-015 | Calendario de servicios | EP-005 |
| US-050 | Dashboard personalizado por rol | EP-005 |
| US-056 | Notificaciones en tiempo real | EP-005 |
| US-016 | Pago seguro (Yape / tarjeta) | EP-007 |
| US-049 | Costos variables por tipo de cuidado / cotización | EP-007 |
| US-060 | Historial de pagos | EP-007 |

**Total: 20 historias de usuario, 5 épicas.**

---

## Base de datos

<div align="center">
<img src="https://webdesignfyp.com/wp-content/uploads/2026/07/Untitled-scaled.png" alt="Diagrama Entidad-Relacion CareMe" width="800"/>
</div>

25 tablas principales, entre ellas:

| Tabla | Descripción |
|---|---|
| `usuarios` | Cuenta base de cualquier persona en el sistema (familiar, cuidador o admin) |
| `familiares` / `cuidadores` / `pacientes` / `administradores` | Perfiles extendidos por rol |
| `servicios` | Núcleo del negocio: ciclo de vida completo de una contratación |
| `pagos` | Transacciones asociadas a cada servicio |
| `calificaciones` / `calificaciones_familiar` | Reseñas bidireccionales |
| `verificaciones_cuidador` | Flujo de aprobación/rechazo de cuidadores por un admin |
| `mensajes` | Historial de chat por servicio |
| `tareas_servicio` | Checklist de tareas realizadas durante el servicio |
| `cuidadores_favoritos` | Cuidadores guardados por un familiar |
| `condiciones_medicas`, `cuidador_condicion`, `paciente_condicion` | Catálogo de especialidades médicas y su relación N:M |

---

## Seguridad

- Autenticación **JWT** (24h de expiración), contraseñas con **BCrypt**.
- Autorización por rol a nivel de Spring Security (`hasRole("ADMIN")` en endpoints administrativos).
- Superficie pública mínima: solo los endpoints de búsqueda (lectura) son accesibles sin sesión.
- Secretos (JWT, credenciales de base de datos) gestionados por variables de entorno, nunca en el código fuente.

---

## Cómo correr el proyecto localmente

### Backend
```bash
cd backend
# Variables de entorno requeridas: DB_PASSWORD, JWT_SECRET
./mvnw spring-boot:run
```

### Frontend
```bash
cd frontend
npm install
ng serve
```

### Con Docker (backend + frontend + base de datos)
```bash
cd backend
cp .env.example .env   # completar con tus valores
docker compose up -d --build
```

---

<div align="center">

*CareMe — MediTec © 2026*

</div>
