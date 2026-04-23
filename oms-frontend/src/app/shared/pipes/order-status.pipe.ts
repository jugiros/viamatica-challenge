import { Pipe, PipeTransform } from '@angular/core';
import { OrderStatus } from '../../core/models';

@Pipe({
  name: 'orderStatus',
  standalone: true
})
export class OrderStatusPipe implements PipeTransform {
  transform(status: OrderStatus): { label: string; class: string } {
    const statusMap: Record<OrderStatus, { label: string; class: string }> = {
      [OrderStatus.PENDING]: { label: 'Pendiente', class: 'bg-yellow-100 text-yellow-800 border-yellow-300' },
      [OrderStatus.CONFIRMED]: { label: 'Confirmada', class: 'bg-blue-100 text-blue-800 border-blue-300' },
      [OrderStatus.PAID]: { label: 'Pagada', class: 'bg-green-100 text-green-800 border-green-300' },
      [OrderStatus.SHIPPED]: { label: 'Enviada', class: 'bg-purple-100 text-purple-800 border-purple-300' },
      [OrderStatus.CANCELLED]: { label: 'Cancelada', class: 'bg-red-100 text-red-800 border-red-300' }
    };

    return statusMap[status] || { label: status, class: 'bg-gray-100 text-gray-800 border-gray-300' };
  }
}
