import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BaseHttpService } from '../../core/services/base-http.service';
import { OrderModel, CreateOrderRequest, UpdateOrderRequest } from '../../core/models';

@Injectable({
  providedIn: 'root'
})
export class OrderService extends BaseHttpService {
  getMyOrders(): Observable<OrderModel[]> {
    return this.get<OrderModel[]>('/orders');
  }

  getOrderById(id: number): Observable<OrderModel> {
    return this.get<OrderModel>(`/orders/${id}`);
  }

  createOrder(request: CreateOrderRequest): Observable<OrderModel> {
    return this.post<OrderModel>('/orders', request);
  }

  updateOrder(id: number, request: UpdateOrderRequest): Observable<OrderModel> {
    return this.put<OrderModel>(`/orders/${id}`, request);
  }

  cancelOrder(id: number, reason: string): Observable<OrderModel> {
    return this.put<OrderModel>(`/orders/${id}/cancel`, { reason });
  }

  confirmOrder(id: number): Observable<OrderModel> {
    return this.put<OrderModel>(`/orders/${id}/confirm`, {});
  }

  getReports(): Observable<string> {
    return this.get<string>('/orders/reports');
  }
}
