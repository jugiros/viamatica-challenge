import { Component, inject, signal, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PaymentService } from './payment.service';
import { PaymentModel } from '../../core/models';
import { ToastService } from '../../shared/services/toast.service';

@Component({
  selector: 'app-payment-list',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule, FormsModule],
  templateUrl: './payment-list.component.html',
  styleUrl: './payment-list.component.scss'
})
export class PaymentListComponent implements OnInit {
  private readonly paymentService = inject(PaymentService);
  private readonly toastService = inject(ToastService);
  
  payments = signal<PaymentModel[]>([]);
  isLoading = signal(false);
  errorMessage = signal<string | null>(null);
  orderId = signal<number | null>(null);
  
  ngOnInit() {
    this.loadPayments();
  }
  
  private loadPayments() {
    this.isLoading.set(true);
    this.errorMessage.set(null);
    
    // Since there's no getAllPayments endpoint, we need to load payments by order
    // This component would typically be used within an order detail context
    this.isLoading.set(false);
  }

  getPaymentById(id: number) {
    this.paymentService.getPaymentById(id).subscribe({
      next: (payment) => {
        this.toastService.showSuccess('Pago cargado exitosamente.');
      },
      error: (error) => {
        this.toastService.showError('Error al cargar el pago.');
        this.errorMessage.set('Error al cargar el pago.');
      }
    });
  }

  getPaymentByOrderId(orderId: number) {
    this.paymentService.getPaymentByOrderId(orderId).subscribe({
      next: (payment) => {
        this.payments.set([payment]);
        this.toastService.showSuccess('Pago de orden cargado exitosamente.');
      },
      error: (error) => {
        this.toastService.showError('Error al cargar el pago de la orden.');
        this.errorMessage.set('Error al cargar el pago de la orden.');
      }
    });
  }
}
