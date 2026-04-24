import { Component, inject, signal, OnInit, ChangeDetectionStrategy, DestroyRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ProductService } from '../products/product.service';
import { OrderService } from '../orders/order.service';
import { forkJoin } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  private readonly productService = inject(ProductService);
  private readonly orderService = inject(OrderService);
  private readonly destroyRef = inject(DestroyRef);
  
  isLoading = signal(true);
  errorMessage = signal<string | null>(null);
  
  stats = signal([
    { label: 'Total Productos', value: '0', icon: '📦', color: 'bg-blue-500' },
    { label: 'Órdenes Hoy', value: '0', icon: '🛒', color: 'bg-green-500' },
    { label: 'Órdenes Pendientes', value: '0', icon: '⏳', color: 'bg-yellow-500' },
    { label: 'Ingresos del Mes', value: '$0', icon: '💰', color: 'bg-purple-500' }
  ]);

  recentActivities = signal([
    { action: 'Sistema inicializado', time: 'Hace un momento', icon: '🚀' }
  ]);

  ngOnInit() {
    this.loadStats();
  }

  private loadStats() {
    this.isLoading.set(true);
    this.errorMessage.set(null);
    
    forkJoin({
      products: this.productService.getProducts(),
      orders: this.orderService.getMyOrders()
    }).pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: ({ products, orders }) => {
        const totalProducts = Array.isArray(products) ? products.length : 0;
        const allOrders = Array.isArray(orders) ? orders : [];
        
        const today = new Date();
        const todayOrders = allOrders.filter(order => {
          const orderDate = new Date(order.createdAt);
          return orderDate.toDateString() === today.toDateString();
        });
        
        const pendingOrders = allOrders.filter(order => order.status === 'PENDIENTE');
        
        const monthIncome = allOrders
          .filter(order => {
            const orderDate = new Date(order.createdAt);
            return orderDate.getMonth() === today.getMonth() && 
                   orderDate.getFullYear() === today.getFullYear() &&
                   order.status === 'PAGADA';
          })
          .reduce((sum, order) => sum + (order.total || 0), 0);

        this.stats.set([
          { label: 'Total Productos', value: totalProducts.toString(), icon: '📦', color: 'bg-blue-500' },
          { label: 'Órdenes Hoy', value: todayOrders.length.toString(), icon: '🛒', color: 'bg-green-500' },
          { label: 'Órdenes Pendientes', value: pendingOrders.length.toString(), icon: '⏳', color: 'bg-yellow-500' },
          { label: 'Ingresos del Mes', value: `$${monthIncome.toFixed(2)}`, icon: '💰', color: 'bg-purple-500' }
        ]);
        
        this.isLoading.set(false);
      },
      error: (error) => {
        console.error('Error loading dashboard stats:', error);
        this.errorMessage.set('Error al cargar los datos del dashboard. Por favor, verifica tu conexión y que estés autenticado.');
        this.isLoading.set(false);
      }
    });
  }
}
