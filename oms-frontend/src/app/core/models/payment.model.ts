export interface PaymentModel {
  id: number;
  orderId: number;
  method: string;
  status: string;
  amount: number;
  externalReference: string;
  paymentDate: string;
}

export interface ProcessPaymentRequest {
  orderId: number;
  method: string;
  amount: number;
  externalReference: string;
}
