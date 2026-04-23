# OMS - Order Management System

## Requisitos Previos

- Java 21 JDK
- Maven 3.9+
- MySQL 8.0 (XAMPP o Docker)

## Configuración de Base de Datos

### Opción 1: XAMPP
1. Iniciar MySQL desde XAMPP Control Panel
2. Acceder a http://localhost/phpmyadmin
3. Crear base de datos `oms_db` con cotejamiento `utf8mb4_general_ci`

### Opción 2: Docker
```bash
docker-compose up -d
```

## Levantar Backend

1. Navegar al directorio `oms-backend`
2. Ejecutar:
```bash
mvn clean install
mvn spring-boot:run
```

El backend iniciará en `http://localhost:8080`

## Probar APIs con Swagger UI

1. Acceder a Swagger UI: `http://localhost:8080/swagger-ui.html`

2. **Opción A: Usar usuarios de seeds (dev/test)**
   - Liquibase crea automáticamente los siguientes usuarios:
     - **admin@oms.com** (rol: ADMIN)
     - **user@oms.com** (rol: USER)
   - Iniciar sesión con estos usuarios para obtener token

3. **Opción B: Registrar un nuevo usuario**
   - Expandir `POST /api/v1/auth/register`
   - Ejecutar con:
     ```json
     {
       "name": "Admin User",
       "email": "admin@example.com",
       "password": "password123",
       "role": "ADMIN"
     }
     ```

4. Iniciar sesión para obtener token:
   - Expandir `POST /api/v1/auth/login`
   - Ejecutar con:
     ```json
     {
       "email": "admin@example.com",
       "password": "password123"
     }
     ```
   - Copiar el `accessToken` de la respuesta

4. Autorizar en Swagger:
   - Clic en botón "Authorize" (ícono de candillo 🔒)
   - Ingresar: `Bearer <accessToken>`
   - Clic en "Authorize" y "Close"

5. Probar endpoints protegidos:
   - Authentication: login, register, refresh token
   - User Management (ADMIN): CRUD de usuarios
   - Product Management: CRUD de productos (GET público, POST/PUT/DELETE ADMIN)
   - Order Management: CRUD de órdenes con autenticación
   - Audit Logs (ADMIN): Consulta de logs de auditoría

## Ejecutar Tests

Navegar al directorio `oms-backend` y ejecutar:

```bash
# Ejecutar todos los tests
.\mvnw test

# Ejecutar tests específicos de seguridad
.\mvnw test -Dtest=AuthControllerTest
```

## Notas

- El token JWT expira después de 1 hora
- Use `POST /api/v1/auth/refresh` para renovar el token
- Liquibase aplicará las migraciones automáticamente al iniciar el backend