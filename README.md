# OMS - Order Management System

## Requisitos Previos

- Java 21 JDK
- Maven 3.9+
- SQL Server 2022 (Docker)

## Configuración de Base de Datos

### Docker (Recomendado)
```bash
docker-compose up -d
```

Esto iniciará SQL Server 2022 en el puerto 1433 con las siguientes credenciales:
- Usuario: sa
- Contraseña: YourStrong@Password123
- Base de datos: oms_db (creada automáticamente por Liquibase)

## Levantar Backend

1. Navegar al directorio `oms-backend`
2. Ejecutar:
```bash
mvn clean install
mvn spring-boot:run
```

**Nota importante:** Si al levantar el backend obtienes un error `NoClassDefFoundError: OrderItemEntity` o similar, ejecuta `mvn clean compile` para regenerar las clases de MapStruct antes de iniciar la aplicación.

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
# Ejecutar todos los tests (recomendado: usar clean para regenerar clases MapStruct)
.\mvnw clean test

# Ejecutar tests específicos de seguridad
.\mvnw clean test -Dtest=AuthControllerTest
```

**Nota:** Se recomienda usar `clean test` en lugar de `test` para evitar errores de `NoClassDefFoundError` con clases generadas por MapStruct (mappers).

## Verificar Cobertura de Código con Jacoco

El proyecto está configurado con Jacoco para verificar la cobertura de código con un mínimo del 80%.

```bash
# Ejecutar tests y generar reporte de cobertura
.\mvnw clean verify

# Ejecutar con perfil de test explícito
.\mvnw clean verify "-Dspring.profiles.active=test"
```

El reporte HTML se genera en: `target/site/jacoco/index.html`

**Configuración de Jacoco:**
- Cobertura mínima: 80% (INSTRUCTION)
- Exclusiones: infrastructure, Application classes, Config classes, DTOs, entities
- El build fallará si la cobertura es menor al 80%

## Notas

- El token JWT expira después de 1 hora
- Use `POST /api/v1/auth/refresh` para renovar el token
- Liquibase aplicará las migraciones automáticamente al iniciar el backend