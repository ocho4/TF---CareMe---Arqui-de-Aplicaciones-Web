# 🏥 CareMe — Sistema de Conexión de Cuidado Especializado

> **Plataforma web integral orientada a enlazar familias con cuidadores profesionales validados, garantizando un acompañamiento óptimo y especializado para adultos mayores.**

<div align="center">
  <img src="https://webdesignfyp.com/wp-content/uploads/2026/06/CareMe-logo-header.png" alt="CareMe Logo Header" width="280"/>

  <br/>

[![Java 17](https://img.shields.io/badge/Java-17-007396?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot 3.5](https://img.shields.io/badge/Spring_Boot-3.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Angular 22](https://img.shields.io/badge/Angular-22-DD0031?style=for-the-badge&logo=angular&logoColor=white)](https://angular.dev/)
[![PostgreSQL 16](https://img.shields.io/badge/PostgreSQL-16-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)
[![AWS Deploy](https://img.shields.io/badge/Deploy-AWS_EC2-FF9900?style=for-the-badge&logo=amazonaws&logoColor=white)](https://aws.amazon.com/)
[![Auth JWT](https://img.shields.io/badge/Auth-JWT_Tokens-black?style=for-the-badge&logo=jsonwebtokens&logoColor=white)](https://jwt.io/)

**Startup MediTec** · *Universidad Peruana de Ciencias Aplicadas (UPC)* · *Curso: Arquitectura de Aplicaciones Web*
</div>

---

## 👥 Equipo de Desarrollo — MediTec

A continuación, se detalla el equipo de ingeniería a cargo de la arquitectura, diseño y construcción del ecosistema CareMe:

| Desarrollador / Integrante | Código de Estudiante | Especialidad / Rol Asignado |
| :--- | :---: | :--- |
| **Mattias Adrián Concha Ochoa** | `U202318269` | Ingeniería de Software / Backend & Systems Architecture |
| **Carlos Fabrizio Lara Talla** | `U202114534` | Ingeniería de Software / Fullstack & Deployment DevOps |

---

## 📝 Visión General del Proyecto

CareMe mitiga una problemática crítica en el entorno familiar: la búsqueda y contratación de personal idóneo para la asistencia diaria de adultos mayores. En la actualidad, este proceso se gestiona predominantemente mediante recomendaciones informales y canales no estructurados, careciendo de garantías de aptitud técnica, transparencia arancelaria y herramientas de monitoreo en tiempo real[cite: 1, 2].

Nuestra solución tecnológica establece un canal robusto, seguro y auditado que interconecta a **miembros familiares** con **cuidadores de la salud certificados**. La plataforma gesiona de extremo a extremo el flujo operativo de la prestación:
1. 🔍 **Descubrimiento y Búsqueda:** Filtros dinámicos basados en especializaciones médicas del paciente[cite: 1, 2].
2. 📄 **Propuesta y Cotización:** Modelado de tarifas variables acordes al tipo y complejidad del servicio[cite: 1, 2].
3. 🤝 **Formalización Contractual:** Gestión formal del ciclo operativo y checklist de tareas diarias[cite: 1, 2].
4. 💳 **Procesamiento de Pagos:** Integración segura transaccional e histórico financiero[cite: 1, 2].
5. ⭐️ **Garantía de Calidad:** Flujo bidireccional de valoraciones y auditorías administrativas de credenciales[cite: 1, 2].

---

## 🏗️ Arquitectura de Software y Repositorio

El proyecto adopta la estrategia de un **Monorepo**, aislando limpiamente las capas de presentación y lógica de negocio mediante contenedores independientes de despliegue[cite: 1, 2]:
