import { Component, inject, signal, OnInit, ChangeDetectionStrategy, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotificationService } from './notification.service';
import { NotificationModel } from '../../core/models';
import { ToastService } from '../../shared/services/toast.service';
import { ConfirmDialogComponent } from '../../shared/components/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-notification-list',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule, ConfirmDialogComponent],
  templateUrl: './notification-list.component.html',
  styleUrl: './notification-list.component.scss'
})
export class NotificationListComponent implements OnInit {
  private readonly notificationService = inject(NotificationService);
  private readonly toastService = inject(ToastService);
  
  @ViewChild(ConfirmDialogComponent) confirmDialog!: ConfirmDialogComponent;
  
  notifications = signal<NotificationModel[]>([]);
  isLoading = signal(false);
  errorMessage = signal<string | null>(null);
  
  ngOnInit() {
    this.loadNotifications();
  }
  
  private loadNotifications() {
    this.isLoading.set(true);
    this.errorMessage.set(null);
    
    // Temporarily disabled due to missing backend endpoint
    this.notificationService.getNotifications().subscribe({
      next: (notifications) => {
        this.notifications.set(notifications || []);
        this.isLoading.set(false);
      },
      error: (error) => {
        this.toastService.showWarning('El servicio de notificaciones no está disponible actualmente.');
        this.errorMessage.set('El servicio de notificaciones no está disponible actualmente.');
        this.isLoading.set(false);
        this.notifications.set([]);
      }
    });
  }

  markAsRead(notificationId: number) {
    this.notificationService.markAsRead(notificationId).subscribe({
      next: () => {
        this.notifications.update(notifications =>
          notifications.map(n =>
            n.id === notificationId ? { ...n, read: true } : n
          )
        );
        this.toastService.showSuccess('Notificación marcada como leída.');
      },
      error: (error) => {
        this.toastService.showError('Error al marcar notificación como leída.');
        this.errorMessage.set('Error al marcar notificación como leída.');
      }
    });
  }

  markAllAsRead() {
    this.notificationService.markAllAsRead().subscribe({
      next: () => {
        this.notifications.update(notifications =>
          notifications.map(n => ({ ...n, read: true }))
        );
        this.toastService.showSuccess('Todas las notificaciones marcadas como leídas.');
      },
      error: (error) => {
        this.toastService.showError('Error al marcar todas las notificaciones como leídas.');
        this.errorMessage.set('Error al marcar todas las notificaciones como leídas.');
      }
    });
  }

  deleteNotification(notificationId: number) {
    this.confirmDialog.open({
      title: 'Confirmar Eliminación',
      message: '¿Estás seguro de eliminar esta notificación?',
      confirmText: 'Eliminar',
      onConfirm: () => {
        this.notificationService.deleteNotification(notificationId).subscribe({
          next: () => {
            this.notifications.update(notifications =>
              notifications.filter(n => n.id !== notificationId)
            );
            this.toastService.showSuccess('Notificación eliminada.');
          },
          error: (error) => {
            this.toastService.showError('Error al eliminar notificación.');
            this.errorMessage.set('Error al eliminar notificación.');
          }
        });
      }
    });
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
