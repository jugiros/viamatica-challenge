import { Component, inject, signal, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PaymentService } from './payment.service';
import { PaymentModel } from '../../core/models';
import { CurrencyLocalePipe } from '../../shared/pipes/currency-locale.pipe';

@Component({
  selector: 'app-payment-list',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule, FormsModule, CurrencyLocalePipe],
  templateUrl: './payment-list.component.html',
  styleUrl: './payment-list.component.scss'
})
export class PaymentListComponent implements OnInit {
  private readonly paymentService = inject(PaymentService);
  
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
        console.log('Payment loaded:', payment);
      },
      error: (error) => {
        this.errorMessage.set('Error al cargar el pago.');
      }
    });
  }

  getPaymentByOrderId(orderId: number) {
    this.paymentService.getPaymentByOrderId(orderId).subscribe({
      next: (payment) => {
        this.payments.set([payment]);
      },
      error: (error) => {
        this.errorMessage.set('Error al cargar el pago de la orden.');
      }
    });
  }
}
