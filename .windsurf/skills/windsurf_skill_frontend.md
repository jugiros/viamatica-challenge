# Skill: Frontend OMS — Angular 19 + Signals + OnPush

## Contexto
Frontend del OMS usando Angular 19 standalone, Signals, ChangeDetection.OnPush y Tailwind CSS 3.x.

## Reglas OBLIGATORIAS para todos los componentes

### 1. ChangeDetection.OnPush — SIEMPRE
```typescript
@Component({
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush, // OBLIGATORIO en TODOS
})
```

**Por que OnPush es critico:**
Angular por defecto (CheckAlways) recorre TODO el arbol de componentes en cada evento
(click, timer, HTTP response, etc.). Con OnPush, Angular solo re-renderiza un componente cuando:
  a) Cambia un @Input() por referencia (objeto nuevo, no mutacion)
  b) El componente o hijo emite un evento DOM
  c) Se usa async pipe con Observable/Signal
  d) Se llama manualmente markForCheck() o detectChanges()
  e) Una signal() cambia su valor (Angular 17+)

**En tests con OnPush:** siempre llamar fixture.detectChanges() despues de cambiar signals o inputs.

### 2. Signals para estado local y global
```typescript
// Estado local
const count = signal(0);
const double = computed(() => count() * 2);
effect(() => console.log('count:', count()));

// En servicio (estado global sin NgRx)
private readonly _user = signal<UserModel | null>(null);
readonly user = this._user.asReadonly();
readonly isAdmin = computed(() => this._user()?.rol === 'ADMIN');
```

### 3. Nueva sintaxis de templates Angular 17+
```html
<!-- @for con track obligatorio para performance -->
@for (item of items(); track item.id) {
  <app-item [data]="item" />
} @empty {
  <p>Sin resultados</p>
}

<!-- @if en lugar de *ngIf -->
@if (isLoading()) {
  <app-spinner />
} @else if (error()) {
  <app-alert [message]="error()!" />
}

<!-- @switch para estados -->
@switch (order.estado) {
  @case ('PENDIENTE') { <span class="badge-yellow">Pendiente</span> }
  @case ('CONFIRMADA') { <span class="badge-blue">Confirmada</span> }
  @case ('PAGADA') { <span class="badge-green">Pagada</span> }
}
```

### 4. Standalone — NO NgModule
```typescript
// app.config.ts
export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes, withComponentInputBinding()),
    provideHttpClient(withInterceptors([jwtInterceptor, errorInterceptor])),
    provideAnimationsAsync(),
  ]
};

// main.ts
bootstrapApplication(AppComponent, appConfig);
```

### 5. Interceptores funcionales (no basados en clase)
```typescript
export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const token = inject(AuthService).token();
  if (token) {
    req = req.clone({ setHeaders: { Authorization: `Bearer ${token}` } });
  }
  return next(req);
};
```

### 6. Guards funcionales
```typescript
export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  return auth.isAuthenticated() ? true : router.createUrlTree(['/login']);
};
```

### 7. inject() en lugar de constructor injection
```typescript
// Preferir inject() sobre constructor para componentes standalone
export class ProductListComponent {
  private productService = inject(ProductService);
  private router = inject(Router);
}
```

### 8. async pipe + OnPush (patron observable)
```html
<!-- orders$ es Observable — async pipe maneja subscribe/unsubscribe -->
@for (order of orders$ | async; track order.id) {
  <app-order-card [order]="order" />
}
```

## Estructura de carpetas
```
src/app/
  core/
    auth/           ← AuthService (signals), guards, interceptors
    models/         ← interfaces TypeScript
    services/       ← HTTP services base
  features/
    auth/           ← login, register (standalone components)
    products/       ← list, detail, form (solo ADMIN para form)
    orders/         ← list, detail, create
    users/          ← ADMIN only
    audit/          ← ADMIN only
  shared/
    components/     ← spinner, alert, badge, pagination
    pipes/          ← order-status.pipe.ts, currency-locale.pipe.ts
  app.config.ts
  app.routes.ts
```

## Tailwind CSS — Convenciones
- Clases utilitarias directamente en templates
- Colores de estado de orden:
  PENDIENTE:  bg-yellow-100 text-yellow-800
  CONFIRMADA: bg-blue-100 text-blue-800
  PAGADA:     bg-green-100 text-green-800
  ENVIADA:    bg-purple-100 text-purple-800
  CANCELADA:  bg-red-100 text-red-800

## Tests con OnPush
```typescript
// SIEMPRE forzar deteccion de cambios en tests con OnPush
component.products.set(mockProducts);
fixture.detectChanges(); // obligatorio con OnPush
expect(fixture.debugElement.queryAll(By.css('.product-card'))).toHaveSize(3);
```

## Comandos Angular
```
ng new oms-frontend --standalone --routing --style=scss
ng g component features/products/product-list --standalone
ng g service core/auth/auth
ng g guard core/auth/auth --functional
ng test --code-coverage
ng build --configuration=production
```
