import { Component, inject, signal, OnInit, ChangeDetectionStrategy, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { OrderService } from './order.service';
import { OrderModel } from '../../core/models';
import { ConfirmDialogComponent } from '../../shared/components/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-order-list',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule, RouterLink, ConfirmDialogComponent],
  templateUrl: './order-list.component.html',
  styleUrl: './order-list.component.scss'
})
export class OrderListComponent implements OnInit {
  private readonly orderService = inject(OrderService);
  private readonly router = inject(Router);
  
  @ViewChild(ConfirmDialogComponent) confirmDialog!: ConfirmDialogComponent;
  
  orders = signal<OrderModel[]>([]);
  isLoading = signal(false);
  errorMessage = signal<string | null>(null);
  
  ngOnInit() {
    this.loadOrders();
  }
  
  private loadOrders() {
    this.isLoading.set(true);
    this.errorMessage.set(null);
    
    this.orderService.getMyOrders().subscribe({
      next: (orders) => {
        this.orders.set(orders || []);
        this.isLoading.set(false);
      },
      error: (error) => {
        this.errorMessage.set('Error al cargar órdenes. Por favor, intenta nuevamente.');
        this.isLoading.set(false);
      }
    });
  }

  viewOrderDetail(orderId: number) {
    this.router.navigate(['/orders', orderId]);
  }

  cancelOrder(orderId: number) {
    this.confirmDialog.open({
      title: 'Confirmar Cancelación',
      message: '¿Estás seguro de cancelar esta orden?',
      confirmText: 'Cancelar Orden',
      onConfirm: () => {
        this.orderService.cancelOrder(orderId, 'Cancelado por usuario').subscribe({
          next: () => {
            this.loadOrders();
          },
          error: (error) => {
            this.errorMessage.set('Error al cancelar la orden.');
          }
        });
      }
    });
  }

  confirmOrder(orderId: number) {
    this.orderService.confirmOrder(orderId).subscribe({
      next: () => {
        this.loadOrders();
      },
      error: (error) => {
        this.errorMessage.set('Error al confirmar la orden.');
      }
    });
  }

  getStatusColor(status: string): string {
    switch (status) {
      case 'PENDIENTE':
        return '#ffc107';
      case 'CONFIRMADA':
        return '#17a2b8';
      case 'PAGADA':
        return '#28a745';
      case 'CANCELADA':
        return '#dc3545';
      default:
        return '#6c757d';
    }
  }
}
