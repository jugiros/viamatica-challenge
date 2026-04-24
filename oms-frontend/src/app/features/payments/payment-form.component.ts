import { Component, inject, signal, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { PaymentService } from './payment.service';
import { ProcessPaymentRequest, PaymentModel } from '../../core/models';

@Component({
  selector: 'app-payment-form',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule, FormsModule],
  templateUrl: './payment-form.component.html',
  styleUrl: './payment-form.component.scss'
})
export class PaymentFormComponent implements OnInit {
  private readonly paymentService = inject(PaymentService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  
  orderId = signal<number | null>(null);
  payment = signal<PaymentModel | null>(null);
  isLoading = signal(false);
  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);
  
  paymentRequest = signal<ProcessPaymentRequest>({
    orderId: 0,
    method: 'CREDIT_CARD',
    amount: 0,
    externalReference: ''
  });
  
  ngOnInit() {
    const orderIdParam = this.route.snapshot.queryParamMap.get('orderId');
    if (orderIdParam) {
      this.orderId.set(Number(orderIdParam));
      this.paymentRequest.update(req => ({ ...req, orderId: Number(orderIdParam) }));
    }
  }
  
  processPayment() {
    if (!this.paymentRequest().orderId || this.paymentRequest().amount <= 0) {
      this.errorMessage.set('Por favor, complete todos los campos correctamente.');
      return;
    }
    
    this.isLoading.set(true);
    this.errorMessage.set(null);
    this.successMessage.set(null);
    
    this.paymentService.processPayment(this.paymentRequest()).subscribe({
      next: (payment) => {
        this.payment.set(payment);
        this.successMessage.set('Pago procesado exitosamente.');
        this.isLoading.set(false);
        
        setTimeout(() => {
          this.router.navigate(['/orders']);
        }, 2000);
      },
      error: (error) => {
        this.errorMessage.set('Error al procesar el pago. Por favor, intenta nuevamente.');
        this.isLoading.set(false);
      }
    });
  }
  
  cancel() {
    this.router.navigate(['/orders']);
  }
}
