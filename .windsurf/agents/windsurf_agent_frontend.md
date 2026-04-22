# Agente: OMS Frontend Developer

## Descripcion
Agente especializado en el desarrollo del frontend Angular 19 del OMS.
Conoce las mejoras de Angular 17-19: signals, @for/@if, standalone, OnPush.

## Skills asociados
- .windsurf/skills/frontend.md

## Reglas absolutas (nunca violar)

### 1. ChangeDetection.OnPush en TODO componente
```typescript
@Component({
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
```
Si no se especifica, por defecto Angular usa CheckAlways que recorre TODO el arbol
de componentes en cada ciclo. OnPush es critico para performance en apps con muchos componentes.

### 2. Nueva sintaxis de control flow (Angular 17+)
NO usar directivas antiguas:
- *ngIf     -> @if
- *ngFor    -> @for (con track obligatorio)
- *ngSwitch -> @switch

### 3. Signals en lugar de Subject/BehaviorSubject para estado simple
```typescript
// ANTES (Angular < 17)
private userSubject = new BehaviorSubject<User | null>(null);
user$ = this.userSubject.asObservable();

// AHORA (Angular 17+)
private readonly _user = signal<User | null>(null);
readonly user = this._user.asReadonly();
readonly isAdmin = computed(() => this._user()?.rol === 'ADMIN');
```

### 4. inject() en lugar de constructor
```typescript
// PREFERIDO en standalone
private service = inject(ProductService);
```

### 5. Interceptores funcionales
```typescript
export const jwtInterceptor: HttpInterceptorFn = (req, next) => { ... };
// NO usar clase que implementa HttpInterceptor (legacy)
```

## Estructura de componentes

Cada feature sigue este patron:
```
feature-name/
  feature-name.component.ts    <- OnPush + signals
  feature-name.component.html  <- @for, @if, async pipe
  feature-name.component.scss  <- Tailwind utilities
  feature-name.service.ts      <- HTTP + signals de estado
  feature-name.routes.ts       <- lazy loading
  __tests__/
    feature-name.component.spec.ts
    feature-name.service.spec.ts
```

## Estados de orden — colores Tailwind
```
PENDIENTE:  bg-yellow-100 text-yellow-800 border-yellow-300
CONFIRMADA: bg-blue-100   text-blue-800   border-blue-300
PAGADA:     bg-green-100  text-green-800  border-green-300
ENVIADA:    bg-purple-100 text-purple-800 border-purple-300
CANCELADA:  bg-red-100    text-red-800    border-red-300
```

## Roles y vistas
- USER: ver sus ordenes, crear ordenes, ver productos (solo lectura)
- ADMIN: todo lo anterior + CRUD productos, CRUD usuarios, audit log, reportes

## Testing con OnPush — patron correcto
```typescript
it('debe renderizar productos al actualizar signal', () => {
  // 1. Mutar el signal
  component.products.set(mockProducts);
  // 2. SIEMPRE llamar detectChanges() con OnPush
  fixture.detectChanges();
  // 3. Verificar DOM
  const cards = fixture.debugElement.queryAll(By.css('[data-testid="product-card"]'));
  expect(cards).toHaveSize(mockProducts.length);
});
```

## Pipes standalone obligatorios
- OrderStatusPipe: 'PENDIENTE' -> 'Pendiente' + clase CSS
- CurrencyLocalePipe: number -> '$1,234.56'

## Lazy loading de rutas
```typescript
// app.routes.ts
{
  path: 'products',
  loadComponent: () => import('./features/products/product-list.component')
    .then(m => m.ProductListComponent)
}
```
