# Skill: Backend OMS — Java 21 + Spring Boot

## Contexto
Sistema OMS (Order Management System) estilo Amazon.
Stack: Java 21 LTS, Spring Boot 3.x, Spring Security 6.x + JWT, Spring Data JPA, MySQL 8, Liquibase, Maven 3.9.x.

## Reglas de arquitectura OBLIGATORIAS

### Arquitectura Hexagonal (Ports & Adapters)
```
domain/          ← Java puro. CERO imports de Spring, JPA, Lombok aqui
application/     ← Use Cases. Solo @Service de Spring, inyeccion por constructor
infrastructure/
  web/           ← Controllers, DTOs, ExceptionHandler
  persistence/   ← @Entity, JpaRepository, Mappers
  security/      ← JWT, SecurityConfig, Filters
```

### Java 21 — Features obligatorias
- Records para DTOs, Commands, Queries, Value Objects
- Sealed Classes para OrderStatus (maquina de estados)
- Pattern Matching en switch expressions para transiciones de estado
- Virtual Threads: spring.threads.virtual.enabled=true
- Text Blocks para JSON en tests

### Reglas de dominio
- Dominio NUNCA importa: org.springframework.*, jakarta.persistence.*, lombok.*
- Value Objects con Records: Email, Money, ProductName
- Excepciones de dominio extienden DomainException (unchecked)
- Puertos (interfaces) en domain, implementaciones en infrastructure

### Persistencia
- Liquibase maneja el schema — spring.jpa.hibernate.ddl-auto=validate
- @Version en ProductEntity y OrderEntity para Optimistic Locking
- LAZY loading en todas las relaciones por defecto
- @BatchSize(size=20) en colecciones para evitar N+1
- equals/hashCode basado en id, nunca en colecciones

### Seguridad JWT
- jjwt 0.12.x
- Payload JWT: solo sub, roles, iat, exp
- BCryptPasswordEncoder strength 12
- Headers: X-Content-Type-Options, X-Frame-Options, X-XSS-Protection
- Ownership check: order.userId == currentUser.id OR isAdmin

### Tests
- @ExtendWith(MockitoExtension.class) — NO Spring context en unit tests
- Estructura AAA (Arrange/Act/Assert)
- @DisplayName descriptivo en cada test
- AssertJ para assertions, Mockito para mocks
- @Captor para verificar objetos de auditoria
- @WebMvcTest para tests de seguridad

### Comandos Maven
```
mvn clean compile
mvn clean test -Dspring.profiles.active=test
mvn clean verify -Dspring.profiles.active=test
mvn spring-boot:run -Dspring-boot.run.profiles=local
```
