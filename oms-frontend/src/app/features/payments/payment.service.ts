import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BaseHttpService } from '../../core/services/base-http.service';
import { PaymentModel, ProcessPaymentRequest } from '../../core/models';

@Injectable({
  providedIn: 'root'
})
export class PaymentService extends BaseHttpService {
  processPayment(request: ProcessPaymentRequest): Observable<PaymentModel> {
    return this.post<PaymentModel>('/payments', request);
  }

  getPaymentById(id: number): Observable<PaymentModel> {
    return this.get<PaymentModel>(`/payments/${id}`);
  }

  getPaymentByOrderId(orderId: number): Observable<PaymentModel> {
    return this.get<PaymentModel>(`/payments/order/${orderId}`);
  }
}
