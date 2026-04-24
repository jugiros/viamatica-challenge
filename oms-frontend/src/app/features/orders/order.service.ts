import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { BaseHttpService } from '../../core/services/base-http.service';
import { OrderModel, CreateOrderRequest, UpdateOrderRequest } from '../../core/models';

@Injectable({
  providedIn: 'root'
})
export class OrderService extends BaseHttpService {
  getMyOrders(): Observable<OrderModel[]> {
    return this.get<any>('/orders?page=0&size=100').pipe(
      map(response => {
        if (response && response.content && Array.isArray(response.content)) {
          return response.content;
        }
        if (Array.isArray(response)) {
          return response;
        }
        return [];
      })
    );
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
