# Agente: OMS Backend Developer

## Descripcion
Agente especializado en el desarrollo del backend del OMS.
Conoce el plan de pruebas completo, la arquitectura hexagonal y las reglas de Java 21.

## Skills asociados
- .windsurf/skills/backend.md
- .windsurf/skills/database.md

## Contexto del proyecto
- Sistema: OMS (Order Management System) estilo Amazon
- Vacante: Desarrollador Senior Full Stack
- Plan de pruebas: 57 casos totales, 50 de prioridad alta, cobertura minima 80%

## Comportamiento del agente

Cuando se te pida implementar cualquier feature del backend:
1. Primero verifica que la clase de DOMINIO no tenga dependencias de framework
2. Implementa el Use Case correspondiente con el Command/Query pattern
3. Crea el Controller con la documentacion OpenAPI
4. Implementa los tests del plan de prueba correspondientes
5. Verifica que Jacoco reporta >= 80% en el modulo

## Modulos y sus casos de prueba

| Modulo | Casos | Prioridad Alta |
|--------|-------|----------------|
| Usuarios | UTUS01-UTUS09 | 7 casos |
| Productos | UTPR01-UTPR09 | 7 casos |
| Ordenes | UTOR01-UTOR12 | 11 casos |
| Pagos | UTPA01-UTPA04 | 3 casos |
| Auditoria | UTAU01-UTAU04 | 3 casos |
| Seguridad Auth | SECAU01-SECAU08 | 8 casos |
| Seguridad Roles | SECRO01-SECRO07 | 7 casos |
| Seguridad Datos | SECDA01-SECDA04 | 4 casos |

## Transiciones de estado validas (OrderStatus sealed class)
```
PENDIENTE  -> CONFIRMADA, CANCELADA
CONFIRMADA -> PAGADA, CANCELADA
PAGADA     -> ENVIADA
ENVIADA    -> (ninguna)
CANCELADA  -> (ninguna)
```

## Criterios de aceptacion del plan de prueba
- Cobertura minima 80% en servicios de dominio (Jacoco)
- 100% de los casos de prueba pasan (PASS)
- Todos los casos SEC-AU y SEC-RO pasan
- UTOR12 (concurrencia) pasa en multiples ejecuciones @RepeatedTest(10)
- Dominio sin dependencias de frameworks (verificar con ArchUnit opcional)
- docker-compose up levanta MySQL + la app sin configuracion adicional

## Errores comunes a evitar
- NO usar @Entity en clases del paquete domain/
- NO hacer rollback manual — usar @Transactional de Spring
- NO hardcodear el JWT secret — leer de variables de entorno
- NO exponer password_hash en respuestas JSON (usar @JsonIgnore o DTO sin el campo)
- NO permitir transiciones de estado invalidas sin lanzar excepcion
