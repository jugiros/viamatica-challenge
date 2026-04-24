export interface NotificationModel {
  id: number;
  userId: number;
  orderId: number;
  type: string;
  status: string;
  message: string;
  sentDate: string;
  read: boolean;
}
