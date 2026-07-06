<div align="center">

<img src="https://raw.githubusercontent.com/Fabriziolara17/CareMe-Prueba-commits/main/docs/Logo_CareMe.png" alt="CareMe Logo" width="220"/>

# CareMe — Backend API

**Plataforma digital que conecta cuidadores profesionales con familias que necesitan atención especializada**

*Startup MediTec · Universidad Peruana de Ciencias Aplicadas · SI385 · 2025-01*

---

![Java](https://img.shields.io/badge/Java-17-007396?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.13-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-4169E1?style=flat-square&logo=postgresql&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.9-C71A36?style=flat-square&logo=apachemaven&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger-OpenAPI_3-85EA2D?style=flat-square&logo=swagger&logoColor=black)
![AWS](https://img.shields.io/badge/Deploy-AWS-FF9900?style=flat-square&logo=amazonaws&logoColor=white)

</div>

---

## Tabla de Contenidos

1. [Equipo de Desarrollo](#1-equipo-de-desarrollo)
2. [Descripcion del Proyecto](#2-descripcion-del-proyecto)
3. [Epicas](#3-epicas)
4. [Historias de Usuario](#4-historias-de-usuario)
5. [Modelo de Base de Datos](#5-modelo-de-base-de-datos)
6. [Arquitectura y Tech Stack](#6-arquitectura-y-tech-stack)
7. [Instalacion y Configuracion](#7-instalacion-y-configuracion)
8. [Documentacion de la API](#8-documentacion-de-la-api)

---

## 1. Equipo de Desarrollo

<div align="center">

**Startup:** MediTec &nbsp;|&nbsp; **Producto:** CareMe &nbsp;|&nbsp; **Universidad:** Universidad Peruana de Ciencias Aplicadas (UPC)

| # | Nombre completo | Codigo |
|:---:|---|:---:|
| 1 | Luis Andres Arce Huaman | U20201a300 |
| 2 | Mattias Adrian Concha Ochoa | U202318269 |
| 3 | Pedro Antero Figueroa Chacon | U20191e516 |
| 4 | Carlos Fabrizio Lara Talla | U202114534 |
| 5 | Renzo Fabrizio Villanueva Ramirez | U202210239 |
| 6 | Luis Mauricio Zavalaga Rios | U20221e978 |

</div>

---

## 2. Descripcion del Proyecto

**CareMe** es el producto principal de la startup **MediTec**. Es una plataforma digital que conecta a **familias** que necesitan cuidado especializado para sus pacientes con **cuidadores profesionales** verificados, resolviendo la dificultad que enfrentan las familias peruanas para encontrar cuidadores de confianza.

El sistema cubre el ciclo completo: busqueda y contratacion del cuidador, seguimiento del servicio, gestion de pagos, recordatorios de medicacion y comunicacion en tiempo real.

| Actor | Rol en la plataforma |
|---|---|
| **Familiar** | Busca, contrata y califica cuidadores para sus pacientes |
| **Cuidador** | Ofrece servicios, gestiona su agenda y atiende pacientes |
| **Paciente** | Adulto mayor o persona que recibe el servicio de cuidado |
| **Administrador** | Supervisa la plataforma, verifica cuidadores y gestiona reportes |

---

## 3. Epicas

| ID | Epica | Descripcion |
|:---:|---|---|
| **EP-001** | Gestion de contrataciones y seguimiento de cuidadores | Como familiar que necesita contratar un cuidador, quiero poder gestionar la contratacion, seguimiento y calificacion de los cuidadores para asegurarme de contar con atencion adecuada y de calidad. |
| **EP-003** | Comunicacion entre usuarios y cuidadores | Como usuario, quiero poder comunicarme de forma directa con el cuidador para coordinar detalles del servicio. |
| **EP-005** | Experiencia de Usuario personalizable y accesibilidad | Como usuario, quiero que la experiencia sea accesible y facil de usar, con opciones de personalizacion que me permitan adaptarla a mis necesidades. |
| **EP-006** | Seguridad y control de acceso | Como usuario, quiero tener control total sobre el acceso a mi cuenta y la seguridad de mis datos personales en la plataforma. |
| **EP-007** | Integracion con servicios de pago y transacciones | Como usuario, quiero realizar o recibir pagos seguros a traves de la app y tener acceso a un historial de transacciones. |

---

## 4. Historias de Usuario

Se implementaron **13 historias de usuario** distribuidas en las 5 epicas del producto.

---

### EP-001 — Gestion de contrataciones y seguimiento de cuidadores

<details>
<summary><strong>US-001</strong> · Solicitud y confirmacion de servicio de cuidado</summary>

**Como** familiar que necesita contratar un cuidador,  
**Quiero** poder solicitar un servicio indicando fechas y horas especificas,  
**Para** confirmar la disponibilidad del cuidador y asegurarme de contar con atencion adecuada en el momento requerido.

| Escenario | Dado que... | Cuando... | Entonces... |
|---|---|---|---|
| 1 — Solicitud enviada | el usuario ha iniciado sesion y seleccionado un cuidador disponible | indica fecha/hora y envia la solicitud | el sistema registra la solicitud con estado `SOLICITADO` |
| 2 — Cuidador acepta | el cuidador recibio la notificacion | selecciona "Aceptar" | el sistema confirma el servicio y notifica a ambas partes |
| 3 — Cuidador rechaza | el cuidador recibio la solicitud | selecciona "Rechazar" | el sistema notifica al usuario que debe elegir otro cuidador |

**Endpoints:** `POST /api/servicios` · `PUT /api/servicios/{id}/confirmar` · `PUT /api/servicios/{id}/rechazar`
</details>

<details>
<summary><strong>US-011</strong> · Busqueda filtrada de cuidadores</summary>

**Como** familiar que necesita contratar un cuidador,  
**Quiero** poder buscar cuidadores por ubicacion, disponibilidad y especializacion,  
**Para** encontrar al mas adecuado segun mis necesidades.

| Escenario | Dado que... | Cuando... | Entonces... |
|---|---|---|---|
| 1 — Filtros aplicados | el usuario accede al buscador | selecciona filtros (zona, horarios, tipo de cuidado) | el sistema muestra solo cuidadores que cumplan los criterios |
| 2 — Orden por calificacion | se han aplicado filtros | el usuario opta por ordenar por calificacion | los cuidadores se muestran del mas al menos valorado |

**Endpoint:** `GET /api/cuidadores/buscar?ubicacion=X&especialidad=Y`
</details>

<details>
<summary><strong>US-023</strong> · Cancelacion de servicio contratado</summary>

**Como** usuario que ha contratado un servicio,  
**Quiero** poder cancelarlo con anticipacion,  
**Para** no generar cobros innecesarios si ya no lo necesito.

| Escenario | Dado que... | Cuando... | Entonces... |
|---|---|---|---|
| 1 — Cancelacion a tiempo | el usuario accede al historial | cancela con al menos 24h de anticipacion | no se genera ningun cargo |
| 2 — Cancelacion tardia | la cancelacion es tardia | se hace con menos de 24h | se aplica penalizacion del 20% del costo total |

**Endpoint:** `PUT /api/servicios/{id}/cancelar`
</details>

<details>
<summary><strong>US-043</strong> · Busqueda de cuidadores por condicion medica especifica</summary>

**Como** familiar de un adulto mayor con una condicion medica especifica,  
**Quiero** poder filtrar cuidadores segun su experiencia con dicha condicion (Alzheimer, Parkinson, diabetes),  
**Para** encontrar al profesional con conocimientos especializados.

| Escenario | Dado que... | Cuando... | Entonces... |
|---|---|---|---|
| 1 — Filtrado por condicion | el usuario busca un cuidador para un paciente con Alzheimer | selecciona esta condicion en los filtros avanzados | el sistema muestra solo perfiles con experiencia certificada |
| 2 — Credenciales visibles | existen cuidadores con formacion en la condicion buscada | el sistema muestra resultados | resalta certificaciones y experiencia especifica |
| 3 — Sin resultados | no hay cuidadores disponibles con esa especializacion | el sistema realiza la busqueda | muestra mensaje informativo y sugiere especialidades alternativas |

**Endpoint:** `GET /api/cuidadores/condicion-medica?condicion=Alzheimer`
</details>

---

### EP-003 — Comunicacion entre usuarios y cuidadores

<details>
<summary><strong>US-005</strong> · Chat en tiempo real entre familiar y cuidador</summary>

**Como** familiar que ha contratado un servicio,  
**Quiero** poder comunicarme por chat con el cuidador,  
**Para** coordinar detalles o resolver dudas durante el servicio.

| Escenario | Dado que... | Cuando... | Entonces... |
|---|---|---|---|
| 1 — Iniciar conversacion | el servicio ha sido confirmado | el usuario accede al perfil del cuidador | podra iniciar un chat y enviar mensajes |
| 2 — Notificacion de mensajes | hay mensajes sin leer | el usuario abre la app | vera una notificacion y podra acceder al chat |

**Endpoints:** `POST /api/mensajes/enviar` · `GET /api/mensajes/servicio/{idServicio}`
</details>

---

### EP-005 — Experiencia de Usuario personalizable y accesibilidad

<details>
<summary><strong>US-009</strong> · Recordatorio de servicio programado</summary>

**Como** usuario cuidador de un servicio,  
**Quiero** recibir un recordatorio previo a la cita,  
**Para** no olvidar la atencion pactada.

| Escenario | Dado que... | Cuando... | Entonces... |
|---|---|---|---|
| 1 — Recordatorio automatico | se acerca la fecha del servicio | faltan 24 horas | el sistema envia una notificacion automatica |
| 2 — Confirmacion | el usuario recibio la notificacion | abre la app | puede confirmar asistencia o cancelar |

**Endpoint:** `GET /api/servicios/proximos/{idUsuario}` + tarea `@Scheduled` cada hora
</details>

<details>
<summary><strong>US-015</strong> · Gestion de calendario de servicios</summary>

**Como** usuario,  
**Quiero** tener un calendario con mis servicios programados,  
**Para** organizar mis horarios y evitar cruces de citas.

| Escenario | Dado que... | Cuando... | Entonces... |
|---|---|---|---|
| 1 — Agenda semanal | el usuario accede a su calendario | consulta la semana actual | vera todas las reservas confirmadas |

**Endpoint:** `GET /api/servicios/agenda/{idUsuario}?fechaInicio=YYYY-MM-DD&fechaFin=YYYY-MM-DD`
</details>

<details>
<summary><strong>US-032</strong> · Recordatorio y seguimiento de medicacion para adultos mayores</summary>

**Como** cuidador o familiar,  
**Quiero** que la app me recuerde las horas de medicacion del adulto mayor y registre su cumplimiento,  
**Para** evitar errores y mejorar la adherencia al tratamiento.

| Escenario | Dado que... | Cuando... | Entonces... |
|---|---|---|---|
| 1 — Configuracion | el usuario ingresa datos de medicacion | establece horarios y dosis | el sistema guarda la info y programa alertas |
| 2 — Notificacion y registro | es hora de tomar la medicacion | se activa la alerta | el cuidador recibe notificacion y puede registrar si la dosis fue tomada |

**Endpoints:** `POST /api/recordatorios-medicacion/programar` · `PUT /api/recordatorios-medicacion/{id}/tomar`
</details>

---

### EP-006 — Seguridad y control de acceso

<details>
<summary><strong>US-033</strong> · Registro de usuario</summary>

**Como** nuevo usuario de la aplicacion,  
**Quiero** registrarme en la plataforma indicando mi rol,  
**Para** acceder a las funcionalidades de CareMe.

| Escenario | Resultado |
|---|---|
| Registro exitoso con rol asignado | Sistema guarda usuario con password hasheado (BCrypt) y asigna rol |
| Datos incompletos | Mensaje de error indicando campos obligatorios faltantes |
| Correo ya registrado | Mensaje de error con opcion de iniciar sesion o recuperar cuenta |
| Sin confirmacion de correo | Acceso bloqueado hasta confirmar cuenta |
| Rol no seleccionado | Mensaje de error solicitando seleccion de rol |

**Endpoint:** `POST /api/auth/registro`
</details>

<details>
<summary><strong>US-034</strong> · Inicio de sesion con credenciales</summary>

**Como** usuario registrado,  
**Quiero** poder iniciar sesion utilizando mis credenciales,  
**Para** acceder a la plataforma segun mi rol.

| Escenario | Resultado |
|---|---|
| Login exitoso | Sistema autentica y redirige segun rol (familiar / cuidador / admin) |
| Login con redes sociales | Autenticacion via Google o Facebook |
| Credenciales incorrectas | Error 400: "Correo o contrasena incorrectos" |
| Campos vacios | Mensaje solicitando completar las credenciales |

**Endpoint:** `POST /api/auth/login`
</details>

<details>
<summary><strong>US-036</strong> · Recuperacion de cuenta</summary>

**Como** usuario que ha olvidado su contrasena,  
**Quiero** poder recuperar el acceso a mi cuenta,  
**Para** restablecer mi contrasena y volver a usar la plataforma.

| Escenario | Resultado |
|---|---|
| Recuperacion por correo | Sistema genera token temporal y simula envio al correo |
| Recuperacion por telefono | Sistema envia codigo de verificacion al numero asociado |
| Correo/telefono no registrado | Mensaje informativo con opcion de registrarse |
| Token caducado | Mensaje de error con opcion de solicitar nuevo enlace |

**Endpoints:** `POST /api/auth/recuperar-password` · `PUT /api/auth/reset-password`
</details>

---

### EP-007 — Integracion con servicios de pago y transacciones

<details>
<summary><strong>US-016</strong> · Confirmacion de pago seguro</summary>

**Como** usuario que contrato un servicio,  
**Quiero** poder pagar dentro de la app de forma segura,  
**Para** evitar riesgos o confusiones con el cuidador.

| Escenario | Dado que... | Cuando... | Entonces... |
|---|---|---|---|
| 1 — Pago por Yape | el servicio esta confirmado | el usuario paga mediante Yape | se genera un comprobante y se registra el pago en el sistema |

**Endpoint:** `POST /api/pagos/procesar`
</details>

<details>
<summary><strong>US-049</strong> · Sistema de costos variables por tipo de cuidado</summary>

**Como** usuario cuidador,  
**Quiero** que el sistema calcule automaticamente los costos segun el tipo de cuidado requerido,  
**Para** tener transparencia en la facturacion.

| Escenario | Resultado |
|---|---|
| Calculo por especializacion | Tarifas diferenciadas segun nivel de especializacion |
| Horarios especiales | Recargo automatico para servicios nocturnos o fines de semana |
| Descuentos por duracion | Descuentos progresivos al superar cierto numero de horas |
| Costos adicionales | Desglose claro de conceptos extras |
| Comparativa de costos | Vista comparativa entre diferentes cuidadores para el mismo servicio |

**Endpoint:** `POST /api/servicios/cotizar`
</details>

---

## 5. Modelo de Base de Datos

### Diagrama Entidad-Relacion (ERD)

![ERD CareMe](https://raw.githubusercontent.com/Fabriziolara17/CareMe-Prueba-commits/main/docs/ERD_CareMe.png)

### Listado de Tablas

| # | Tabla | Descripcion |
|:---:|---|---|
| 1 | `tipos_usuario` | Catalogo maestro de roles del sistema (familiar, cuidador, admin) |
| 2 | `usuarios` | Entidad central de autenticacion y datos personales de todos los actores |
| 3 | `administradores` | Perfil extendido de usuarios con privilegios de administracion |
| 4 | `cuidadores` | Perfil extendido del personal que brinda el cuidado (especialidad, tarifa, calificacion) |
| 5 | `familiares` | Perfil extendido de los responsables que contratan servicios |
| 6 | `pacientes` | Perfil extendido de quienes reciben el cuidado, con necesidades especificas |
| 7 | `familiar_paciente` | Relacion muchos-a-muchos entre familiares y los pacientes bajo su cargo |
| 8 | `condiciones_medicas` | Catalogo normalizado de condiciones o enfermedades (Alzheimer, Parkinson, etc.) |
| 9 | `cuidador_condicion` | Especialidades medicas certificadas del cuidador (N:M) |
| 10 | `paciente_condicion` | Historial de condiciones medicas del paciente (N:M) |
| 11 | `certificados_cuidador` | Documentos y certificados subidos por el cuidador para su validacion |
| 12 | `evaluaciones_cuidado` | Auditoria y evaluacion continua de la calidad del servicio prestado |
| 13 | `horarios_cuidador` | Gestion estructurada de la disponibilidad horaria semanal del cuidador |
| 14 | `servicios` | **Tabla central** — transaccion de contrato de cuidado entre familiar, cuidador y paciente |
| 15 | `tareas_servicio` | Desglose de tareas u ocupaciones especificas por cada servicio contratado |
| 16 | `recordatorios_medicacion` | Control de medicamentos programados y su cumplimiento para el paciente |
| 17 | `pagos` | Registro de transacciones economicas asociadas a un servicio (Yape, tarjeta) |
| 18 | `calificaciones` | Resenas y puntaje (1-5 estrellas) otorgado por el familiar al cuidador |
| 19 | `mensajes` | Historial del chat interno entre familiar y cuidador por servicio |
| 20 | `consejos` | Publicaciones de salud y cuidado emitidas por el administrador |
| 21 | `reclamos` | Gestion de incidencias, quejas o soporte tecnico de los usuarios |
| 22 | `reportes_admin` | Historial analitico de la actividad gerencial del administrador |

### Relaciones Clave

| Origen | FK | Destino | Cardinalidad |
|---|---|---|:---:|
| `administradores` | `id_usuario` | `usuarios` | 1 a 0..1 |
| `cuidadores` | `id_usuario` | `usuarios` | 1 a 0..1 |
| `familiares` | `id_usuario` | `usuarios` | 1 a 0..1 |
| `pacientes` | `id_familiar` | `familiares` | 1 a 0..N |
| `cuidador_condicion` | `id_cuidador / id_condicion` | `cuidadores / condiciones_medicas` | N:M |
| `paciente_condicion` | `id_paciente / id_condicion` | `pacientes / condiciones_medicas` | N:M |
| `servicios` | `id_familiar / id_cuidador / id_paciente` | `familiares / cuidadores / pacientes` | 1 a 0..N |
| `pagos` | `id_servicio` | `servicios` | 1 a 0..1 |
| `calificaciones` | `id_servicio` | `servicios` | 1 a 0..1 |
| `mensajes` | `id_servicio` | `servicios` | 1 a 0..N |
| `recordatorios_medicacion` | `id_paciente` | `pacientes` | 1 a 0..N |

---

## 6. Arquitectura y Tech Stack

### Arquitectura en Capas

```
src/main/java/com/upc/careme/
│
├── config/          # Configuracion global (CORS, Security, ModelMapper)
├── controllers/     # Capa de presentacion — REST endpoints
├── services/        # Capa de negocio — logica de la aplicacion
├── repositorios/    # Capa de datos — Spring Data JPA
├── entidades/       # Modelos JPA — tablas de la base de datos
└── dtos/            # Data Transfer Objects — contratos de la API
```

Cada recurso sigue el patron: `Controller → Service → Repository → Entity`

### Tech Stack

| Categoria | Tecnologia | Version |
|---|---|:---:|
| Lenguaje | Java | 17 |
| Framework | Spring Boot | 3.5.13 |
| Persistencia | Spring Data JPA + Hibernate | — |
| Base de datos | PostgreSQL | 15+ |
| Seguridad | Spring Security + BCrypt | — |
| Documentacion API | SpringDoc OpenAPI (Swagger UI) | 2.7.0 |
| Mapeo de objetos | ModelMapper | 3.1.1 |
| Utilidades | Lombok | — |
| Build | Apache Maven | 3.9+ |
| Despliegue | Amazon Web Services (AWS) | — |

---

## 7. Instalacion y Configuracion

### Prerequisitos

- Java 17+
- Maven 3.9+
- PostgreSQL 15+
- Git

### Pasos

**1. Clonar el repositorio**
```bash
git clone https://github.com/Fabriziolara17/CareMe-Prueba-commits.git
cd CareMe-Prueba-commits
```

**2. Crear la base de datos**
```sql
CREATE DATABASE careme_db;
```

**3. Configurar credenciales** en `src/main/resources/application.properties`
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/careme_db
spring.datasource.username=TU_USUARIO
spring.datasource.password=TU_CONTRASENA
```

> En produccion (AWS) estas credenciales deben gestionarse como variables de entorno o mediante AWS Secrets Manager. Nunca subir credenciales reales al repositorio.

**4. Compilar y ejecutar**
```bash
mvn clean install
mvn spring-boot:run
```

La aplicacion estara disponible en `http://localhost:8080`

---

## 8. Documentacion de la API

Una vez levantada la aplicacion, la documentacion interactiva generada por **Swagger UI** esta disponible en:

```
http://localhost:8080/swagger-ui/index.html
```

Especificacion OpenAPI en formato JSON:

```
http://localhost:8080/v3/api-docs
```

### Endpoints Principales

| Modulo | Metodo | Endpoint | HU |
|---|:---:|---|:---:|
| Auth | `POST` | `/api/auth/registro` | US-033 |
| Auth | `POST` | `/api/auth/login` | US-034 |
| Auth | `POST` | `/api/auth/recuperar-password` | US-036 |
| Auth | `PUT` | `/api/auth/reset-password` | US-036 |
| Cuidadores | `GET` | `/api/cuidadores/buscar` | US-011 |
| Cuidadores | `GET` | `/api/cuidadores/condicion-medica` | US-043 |
| Servicios | `POST` | `/api/servicios` | US-001 |
| Servicios | `PUT` | `/api/servicios/{id}/confirmar` | US-001 |
| Servicios | `PUT` | `/api/servicios/{id}/rechazar` | US-001 |
| Servicios | `PUT` | `/api/servicios/{id}/cancelar` | US-023 |
| Servicios | `POST` | `/api/servicios/cotizar` | US-049 |
| Servicios | `GET` | `/api/servicios/proximos/{idUsuario}` | US-009 |
| Servicios | `GET` | `/api/servicios/agenda/{idUsuario}` | US-015 |
| Pagos | `POST` | `/api/pagos/procesar` | US-016 |
| Mensajes | `POST` | `/api/mensajes/enviar` | US-005 |
| Mensajes | `GET` | `/api/mensajes/servicio/{idServicio}` | US-005 |
| Recordatorios | `POST` | `/api/recordatorios-medicacion/programar` | US-032 |
| Recordatorios | `PUT` | `/api/recordatorios-medicacion/{id}/tomar` | US-032 |

---

<div align="center">

*MediTec © 2025 — Todos los derechos reservados*

</div>
