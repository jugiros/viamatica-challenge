# OMS Frontend

Frontend del sistema de gestión de órdenes (OMS) construido con Angular 21, Tailwind CSS 3 y las últimas APIs de Angular.

## Tecnologías

- **Angular 21.2.7** - Framework principal
- **Tailwind CSS 3.x** - Framework de estilos
- **Angular Signals** - Gestión de estado reactivo
- **ChangeDetectionStrategy.OnPush** - Estrategia de detección de cambios global
- **Standalone Components** - Componentes independientes sin NgModule
- **Functional Guards & Interceptors** - Guards e interceptores funcionales
- **TypeScript** - Lenguaje de tipado

## Requisitos Previos

- Node.js 18+ 
- npm 9+

## Instalación

```bash
npm install
```

## Servidor de Desarrollo

Para iniciar el servidor de desarrollo:

```bash
npm start
# o
ng serve
```

La aplicación estará disponible en `http://localhost:4200/`. La aplicación se recargará automáticamente al modificar cualquier archivo fuente.

## Estructura del Proyecto

```
oms-frontend/src/app/
├── core/
│   ├── auth/                    # Autenticación y autorización
│   │   ├── auth.service.ts      # Servicio de autenticación con signals
│   │   ├── auth.guard.ts        # Guard de autenticación funcional
│   │   ├── role.guard.ts        # Guard de roles funcional
│   │   ├── jwt.interceptor.ts   # Interceptor JWT funcional
│   │   └── error.interceptor.ts # Interceptor de errores funcional
│   ├── models/                  # Interfaces TypeScript
│   │   ├── user.model.ts
│   │   ├── product.model.ts
│   │   ├── order.model.ts
│   │   └── audit.model.ts
│   └── services/                # Servicios HTTP base
│       └── base-http.service.ts
├── features/                    # Funcionalidades por dominio
│   ├── auth/
│   │   ├── login/
│   │   │   ├── login.component.ts
│   │   │   ├── login.component.html
│   │   │   └── login.component.scss
│   │   └── register/
│   │       ├── register.component.ts
│   │       ├── register.component.html
│   │       └── register.component.scss
│   ├── products/
│   │   ├── product.service.ts
│   │   ├── product-list.component.ts
│   │   ├── product-list.component.html
│   │   └── product-list.component.scss
│   └── orders/
│       ├── order.service.ts
│       ├── order-form.component.ts
│       ├── order-form.component.html
│       └── order-form.component.scss
├── shared/
│   ├── components/              # Componentes compartidos
│   └── pipes/                   # Pipes standalone
│       ├── order-status.pipe.ts
│       └── currency-locale.pipe.ts
├── app.config.ts                # Configuración standalone
├── app.routes.ts                # Rutas de la aplicación
├── app.ts                       # Componente raíz
└── styles.scss                  # Estilos globales
```

## Características Principales

- **ChangeDetectionStrategy.OnPush** en todos los componentes
- **Signals** para gestión de estado reactivo
- **computed()** para estado derivado
- **effect()** para side effects
- **Nueva sintaxis Angular 17+**: `@for`, `@if`, `@switch`
- **Functional Guards**: Guards usando `inject()` y funciones
- **Functional Interceptors**: Interceptores usando `HttpInterceptorFn`
- **Standalone Components**: Sin NgModule
- **Tailwind CSS 4.x**: Todos los estilos con Tailwind
- **Pipes Standalone**: Pipes reutilizables

## Scripts Disponibles

```bash
# Iniciar servidor de desarrollo
npm start

# Compilar para producción
npm run build

# Ejecutar pruebas unitarias
npm test

# Ejecutar pruebas e2e
npm run e2e

# Lint del código
npm run lint
```

## Configuración de Backend

El frontend se conecta al backend en `http://localhost:8080/api/v1`. Asegúrate de que el backend esté corriendo antes de iniciar el frontend.

## Rutas de la Aplicación

- `/` - Redirige a `/products`
- `/login` - Página de inicio de sesión
- `/register` - Página de registro
- `/products` - Lista de productos (requiere autenticación)
- `/orders/new` - Formulario de creación de órdenes (requiere autenticación)

## Generación de Código

Angular CLI incluye herramientas poderosas para generar código:

```bash
# Generar componente
ng generate component component-name

# Generar servicio
ng generate service service-name

# Generar pipe
ng generate pipe pipe-name

# Ver todas las opciones disponibles
ng generate --help
```

## Recursos Adicionales

Para más información sobre Angular CLI, visita la [Documentación de Angular CLI](https://angular.dev/tools/cli).
