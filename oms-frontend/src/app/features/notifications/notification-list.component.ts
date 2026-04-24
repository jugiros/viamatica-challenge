import { Component, inject, signal, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotificationModel } from '../../core/models';

@Component({
  selector: 'app-notification-list',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule],
  templateUrl: './notification-list.component.html',
  styleUrl: './notification-list.component.scss'
})
export class NotificationListComponent implements OnInit {
  notifications = signal<NotificationModel[]>([]);
  isLoading = signal(false);
  errorMessage = signal<string | null>(null);
  
  ngOnInit() {
    this.loadNotifications();
  }
  
  private loadNotifications() {
    this.isLoading.set(true);
    this.errorMessage.set(null);
    
    // Mock data for notifications
    this.notifications.set([
      {
        id: 1,
        userId: 1,
        orderId: 1,
        type: 'PAYMENT_RECEIVED',
        status: 'SENT',
        message: 'Pago recibido para orden #1',
        read: false,
        sentDate: new Date().toISOString()
      },
      {
        id: 2,
        userId: 1,
        orderId: 2,
        type: 'ORDER_CONFIRMED',
        status: 'SENT',
        message: 'Orden #2 ha sido confirmada',
        read: true,
        sentDate: new Date(Date.now() - 86400000).toISOString()
      }
    ]);
    
    this.isLoading.set(false);
  }

  markAsRead(notificationId: number) {
    this.notifications.update(notifications =>
      notifications.map(n =>
        n.id === notificationId ? { ...n, read: true } : n
      )
    );
  }

  markAllAsRead() {
    this.notifications.update(notifications =>
      notifications.map(n => ({ ...n, read: true }))
    );
  }

  deleteNotification(notificationId: number) {
    this.notifications.update(notifications =>
      notifications.filter(n => n.id !== notificationId)
    );
  }

  getNotificationIcon(type: string): string {
    switch (type) {
      case 'PAYMENT_RECEIVED':
        return '💰';
      case 'ORDER_CONFIRMED':
        return '✅';
      case 'ORDER_CANCELLED':
        return '❌';
      case 'ORDER_SHIPPED':
        return '📦';
      default:
        return '🔔';
    }
  }

  getTypeColor(type: string): string {
    switch (type) {
      case 'PAYMENT_RECEIVED':
        return '#28a745';
      case 'ORDER_CONFIRMED':
        return '#17a2b8';
      case 'ORDER_CANCELLED':
        return '#dc3545';
      case 'ORDER_SHIPPED':
        return '#ffc107';
      default:
        return '#6c757d';
    }
  }
}
