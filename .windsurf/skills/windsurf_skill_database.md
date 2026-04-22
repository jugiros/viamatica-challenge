# Skill: Base de Datos — MySQL 8 + Liquibase + InnoDB ACID

## Contexto
Base de datos MySQL 8.0 para OMS. Migraciones gestionadas con Liquibase.
Motor InnoDB para garantizar ACID (Atomicidad, Consistencia, Aislamiento, Durabilidad).

## Por que InnoDB + ACID
- **Atomicidad**: las transacciones de ordenes (validar stock + decrementar + crear ORDER_ITEMS)
  se ejecutan como una unidad. Si falla una parte, se hace rollback completo.
- **Consistencia**: FOREIGN KEYS, CHECK constraints y UNIQUE keys garantizados por InnoDB.
- **Aislamiento**: Optimistic Locking con campo `version` evita condiciones de carrera en stock.
- **Durabilidad**: InnoDB escribe en redo logs antes de confirmar — datos sobreviven crashes.

## Reglas de Liquibase OBLIGATORIAS

### Estructura de archivos
```
src/main/resources/db/changelog/
  db.changelog-master.xml     <- orquestador, solo <include> aqui
  migrations/
    V001__create_categories.xml
    V002__create_users.xml
    ...
  seeds/
    V010__seed_initial_data.xml  <- context="dev,test"
```

### Changeset minimo correcto
```xml
<changeSet id="V001" author="oms-dev">
  <preConditions onFail="MARK_RAN">
    <not><tableExists tableName="CATEGORIES"/></not>
  </preConditions>
  <createTable tableName="CATEGORIES"
               remarks="Categorias de productos">
    <column name="id" type="BIGINT" autoIncrement="true">
      <constraints primaryKey="true" nullable="false"/>
    </column>
    <column name="nombre" type="VARCHAR(100)">
      <constraints nullable="false"/>
    </column>
    <!-- ... -->
  </createTable>
  <!-- Al final del createTable agregar ENGINE e InnoDB via modifySql -->
  <modifySql dbms="mysql">
    <append value=" ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci"/>
  </modifySql>
</changeSet>
```

### Convenciones de tablas
- Todas las tablas: ENGINE=InnoDB, CHARSET=utf8mb4, COLLATE=utf8mb4_unicode_ci
- PK: BIGINT AUTO_INCREMENT
- Timestamps: created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
- updated_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
- Soft delete: deleted_at TIMESTAMP NULL DEFAULT NULL
- Optimistic Locking: version BIGINT NOT NULL DEFAULT 0 en ORDERS y PRODUCTS
- ENUM para estados: 'PENDIENTE','CONFIRMADA','PAGADA','ENVIADA','CANCELADA'

### Foreign Keys con InnoDB
```xml
<addForeignKeyConstraint
  baseTableName="PRODUCTS"
  baseColumnNames="category_id"
  constraintName="fk_products_category"
  referencedTableName="CATEGORIES"
  referencedColumnNames="id"
  onDelete="RESTRICT"
  onUpdate="CASCADE"/>
```

### Indices para performance
```xml
<createIndex indexName="idx_orders_user_estado"
             tableName="ORDERS">
  <column name="user_id"/>
  <column name="estado"/>
</createIndex>
```

### CHECK constraints (MySQL 8+)
```xml
<sql>
  ALTER TABLE PRODUCTS
  ADD CONSTRAINT ck_products_precio CHECK (precio > 0),
  ADD CONSTRAINT ck_products_stock CHECK (stock >= 0);
</sql>
```

## Transacciones Spring + InnoDB
```java
// Use cases que modifican stock + orden = UNA transaccion
@Transactional(isolation = Isolation.READ_COMMITTED)
public OrderDomain confirmOrder(Long orderId) {
  // 1. Lock optimista en producto via @Version
  // 2. Validar stock
  // 3. Decrementar stock
  // 4. Cambiar estado orden
  // 5. Guardar audit log
  // Si cualquier paso falla -> rollback automatico (InnoDB)
}
```

## Comandos Liquibase
```
# Ver estado de migraciones
mvn liquibase:status

# Aplicar migraciones pendientes
mvn liquibase:update

# Rollback ultimo changeset
mvn liquibase:rollbackCount -Dliquibase.rollbackCount=1

# Generar SQL sin ejecutar
mvn liquibase:updateSQL
```
