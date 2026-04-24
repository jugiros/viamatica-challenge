import { Routes } from '@angular/router';
import { LoginComponent } from './features/auth/login/login.component';
import { RegisterComponent } from './features/auth/register/register.component';
import { ProductListComponent } from './features/products/product-list.component';
import { ProductFormComponent } from './features/products/product-form.component';
import { OrderFormComponent } from './features/orders/order-form.component';
import { DashboardComponent } from './features/dashboard/dashboard.component';
import { MainLayoutComponent } from './shared/layouts/main-layout/main-layout.component';
import { authGuard } from './core/auth/auth.guard';
import { CategoryListComponent } from './features/categories/category-list.component';
import { CategoryFormComponent } from './features/categories/category-form.component';
import { OrderListComponent } from './features/orders/order-list.component';
import { OrderDetailComponent } from './features/orders/order-detail.component';
import { PaymentListComponent } from './features/payments/payment-list.component';
import { PaymentFormComponent } from './features/payments/payment-form.component';
import { UserListComponent } from './features/users/user-list.component';
import { UserFormComponent } from './features/users/user-form.component';
import { AuditListComponent } from './features/audit/audit-list.component';
import { NotificationListComponent } from './features/notifications/notification-list.component';

export const routes: Routes = [
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  {
    path: '',
    component: MainLayoutComponent,
    canActivate: [authGuard],
    children: [
      { path: 'dashboard', component: DashboardComponent, data: { breadcrumb: 'Dashboard' } },
      { path: 'products', component: ProductListComponent, data: { breadcrumb: 'Productos' } },
      { path: 'products/new', component: ProductFormComponent, data: { breadcrumb: 'Nuevo Producto' } },
      { path: 'products/:id/edit', component: ProductFormComponent, data: { breadcrumb: 'Editar Producto' } },
      { path: 'categories', component: CategoryListComponent, data: { breadcrumb: 'Categorías' } },
      { path: 'categories/new', component: CategoryFormComponent, data: { breadcrumb: 'Nueva Categoría' } },
      { path: 'categories/:id/edit', component: CategoryFormComponent, data: { breadcrumb: 'Editar Categoría' } },
      { path: 'orders', component: OrderListComponent, data: { breadcrumb: 'Órdenes' } },
      { path: 'orders/new', component: OrderFormComponent, data: { breadcrumb: 'Nueva Orden' } },
      { path: 'orders/:id', component: OrderDetailComponent, data: { breadcrumb: 'Detalle de Orden' } },
      { path: 'payments', component: PaymentListComponent, data: { breadcrumb: 'Pagos' } },
      { path: 'payments/new/:orderId', component: PaymentFormComponent, data: { breadcrumb: 'Procesar Pago' } },
      { path: 'users', component: UserListComponent, data: { breadcrumb: 'Usuarios' } },
      { path: 'users/new', component: UserFormComponent, data: { breadcrumb: 'Nuevo Usuario' } },
      { path: 'users/:id/edit', component: UserFormComponent, data: { breadcrumb: 'Editar Usuario' } },
      { path: 'audit', component: AuditListComponent, data: { breadcrumb: 'Auditoría' } },
      { path: 'notifications', component: NotificationListComponent, data: { breadcrumb: 'Notificaciones' } }
    ]
  },
  { path: '**', redirectTo: '/dashboard' }
];
