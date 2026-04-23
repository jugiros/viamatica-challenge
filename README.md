# OMS - Order Management System

Sistema de Gestión de Órdenes desarrollado bajo estándares de ingeniería de software avanzada, utilizando **Arquitectura Hexagonal** y un stack moderno basado en **Java 21** y **Angular 19**.

## 🚀 Stack Tecnológico

- **Backend:** Java 21 LTS, Spring Boot 3.x, Spring Security 6.x (JWT).
- **Frontend:** Angular 19 (Standalone components, Signals, OnPush).
- **Base de Datos:** MySQL 8.0 con **Liquibase** para control de versiones del esquema.
- **Infraestructura:** Docker & Docker Compose.
- **Calidad:** JUnit 5, Mockito, Jasmine/Karma (57 casos de prueba integrados).

## 🛠️ Requisitos Previos

- **Docker Desktop** (con soporte para WSL 2 en Windows).
- **Java 21 JDK**.
- **Node.js 20+** y Angular CLI.
- **Git** (Configurado para Conventional Commits).

## ⚙️ Configuración del Entorno (Local)

### 1. Infraestructura de Datos
Desde la raíz del proyecto, levanta la base de datos y herramientas de gestión:

## 🛠️ Configuración de Infraestructura (Docker)

**IMPORTANTE:** Antes de ejecutar el Backend o el Frontend, es obligatorio levantar los servicios de base de datos.

### 1. Requisitos previos
* Tener instalado **Docker Desktop**.
* Asegurarse de que el motor de Docker esté iniciado (Icono de la ballena en verde).

### 2. Levantar Base de Datos y Gestión
Ejecuta el siguiente comando en la raíz del proyecto:

```bash
docker-compose up -d

## 🛠️ Configuración del Entorno de Base de Datos (Alternativa XAMPP)

### Pasos para validación:
1. **Iniciar MySQL**: Abrir el Panel de Control de XAMPP y presionar `Start` en el módulo MySQL.
2. **Acceso Administrativo**: Ir a [http://localhost/phpmyadmin](http://localhost/phpmyadmin).
3. **Creación de Esquema**: 
   - Crear una base de datos llamada `oms_db`.
   - Cotejamiento recomendado: `utf8mb4_general_ci`.
4. **Validación de Conexión**:
   - Host: `localhost`
   - Puerto: `3306`
   - Usuario: `root` (por defecto en XAMPP)
   - Contraseña: ` ` (vacío por defecto)

   ## ⚙️ Configuración del Entorno (Local)

El sistema está diseñado para ser agnóstico a la infraestructura de base de datos. Para este entorno de desarrollo, se ha priorizado el uso de **XAMPP** para garantizar la estabilidad del motor MySQL.

### 1. Infraestructura de Datos (XAMPP)

1. **Servicios**: Iniciar **Apache** y **MySQL** desde el XAMPP Control Panel (ejecutar como Administrador).
2. **Base de Datos**: 
   - Acceder a [http://localhost/phpmyadmin](http://localhost/phpmyadmin).
   - Crear el esquema `oms_db` utilizando el cotejamiento `utf8mb4_general_ci`.
3. **Validación de Puerto**: Asegurarse de que el puerto `3306` esté libre de procesos previos (Docker u otras instancias de MySQL).

### 2. Gestión de Persistencia con Liquibase

Este proyecto utiliza **Liquibase** para el control de versiones del esquema, eliminando la necesidad de scripts manuales de SQL.

- **Migración Automática**: Al ejecutar el Backend, Liquibase detectará el esquema y aplicará los *changesets* definidos en `src/main/resources/db/changelog/`.
- **Trazabilidad**: El historial de cambios se registra en la tabla técnica `DATABASECHANGELOG`.
- **Estructura de Cambios**:
  - `001-initial-schema.xml`: Define la base del sistema (Tablas de seguridad y auditoría).

## 🏗️ Guía de Ejecución (Backend)

### Requisitos Técnicos
- **Java SDK**: 21 (Amazon Corretto recomendado).
- **Maven**: 3.9+.

### Pasos para iniciar:
1. Clonar el repositorio.
2. Actualizar el archivo `src/main/resources/application.yml` con tus credenciales locales de MySQL si difieren de las estándar (root/sin contraseña).
3. Ejecutar el comando:
   ```bash
   mvn clean install
   mvn spring-boot:run