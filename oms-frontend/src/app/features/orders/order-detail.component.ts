import { Component, inject, signal, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { OrderService } from './order.service';
import { PaymentService } from '../payments/payment.service';
import { OrderModel, PaymentModel } from '../../core/models';

@Component({
  selector: 'app-order-detail',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule],
  templateUrl: './order-detail.component.html',
  styleUrl: './order-detail.component.scss'
})
export class OrderDetailComponent implements OnInit {
  private readonly orderService = inject(OrderService);
  private readonly paymentService = inject(PaymentService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  
  order = signal<OrderModel | null>(null);
  payment = signal<PaymentModel | null>(null);
  isLoading = signal(false);
  errorMessage = signal<string | null>(null);
  
  ngOnInit() {
    const orderId = Number(this.route.snapshot.paramMap.get('id'));
    if (orderId) {
      this.loadOrder(orderId);
    }
  }
  
  private loadOrder(orderId: number) {
    this.isLoading.set(true);
    this.errorMessage.set(null);
    
    this.orderService.getOrderById(orderId).subscribe({
      next: (order) => {
        this.order.set(order);
        this.loadPayment(orderId);
        this.isLoading.set(false);
      },
      error: (error) => {
        this.errorMessage.set('Error al cargar la orden. Por favor, intenta nuevamente.');
        this.isLoading.set(false);
      }
    });
  }

  private loadPayment(orderId: number) {
    this.paymentService.getPaymentByOrderId(orderId).subscribe({
      next: (payment) => {
        this.payment.set(payment);
      },
      error: (error) => {
        // Payment might not exist yet - this is expected for new orders
        // 404 is expected for orders without payment, handle silently
        if (error.status === 404) {
          this.payment.set(null);
        } else {
          // Log other errors but don't show them to user
          console.error('Error loading payment:', error);
          this.payment.set(null);
        }
      }
    });
  }

  cancelOrder() {
    if (this.order() && confirm('¿Estás seguro de cancelar esta orden?')) {
      this.orderService.cancelOrder(this.order()!.id, 'Cancelado por usuario').subscribe({
        next: () => {
          this.loadOrder(this.order()!.id);
        },
        error: (error) => {
          this.errorMessage.set('Error al cancelar la orden.');
        }
      });
    }
  }

  confirmOrder() {
    if (this.order()) {
      this.orderService.confirmOrder(this.order()!.id).subscribe({
        next: () => {
          this.loadOrder(this.order()!.id);
        },
        error: (error) => {
          this.errorMessage.set('Error al confirmar la orden.');
        }
      });
    }
  }

  processPayment() {
    if (this.order()) {
      // Navigate to payment form with order ID
      this.router.navigate(['/payments/new'], { queryParams: { orderId: this.order()!.id } });
    }
  }

  goBack() {
    this.router.navigate(['/orders']);
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
