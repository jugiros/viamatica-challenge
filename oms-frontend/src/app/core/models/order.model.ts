export enum OrderStatus {
  PENDING = 'PENDIENTE',
  CONFIRMED = 'CONFIRMADA',
  PAID = 'PAGADA',
  SHIPPED = 'ENVIADA',
  CANCELLED = 'CANCELADA'
}

export interface OrderItemModel {
  productId: number;
  productName: string;
  quantity: number;
  price: number;
  subtotal: number;
}

export interface OrderModel {
  id: number;
  userId: number;
  status: OrderStatus;
  items: OrderItemModel[];
  total: number;
  createdAt: string;
  updatedAt: string;
}

export interface CreateOrderRequest {
  items: {
    productId: number;
    quantity: number;
  }[];
}

export interface UpdateOrderRequest {
  status?: OrderStatus;
}
