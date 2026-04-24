import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BaseHttpService } from '../../core/services/base-http.service';
import { NotificationModel } from '../../core/models';

@Injectable({
  providedIn: 'root'
})
export class NotificationService extends BaseHttpService {
  getNotifications(): Observable<NotificationModel[]> {
    return this.get<NotificationModel[]>('/notifications');
  }

  getNotificationById(id: number): Observable<NotificationModel> {
    return this.get<NotificationModel>(`/notifications/${id}`);
  }

  markAsRead(id: number): Observable<NotificationModel> {
    return this.put<NotificationModel>(`/notifications/${id}/read`, {});
  }

  markAllAsRead(): Observable<void> {
    return this.put<void>('/notifications/read-all', {});
  }

  deleteNotification(id: number): Observable<void> {
    return this.delete<void>(`/notifications/${id}`);
  }
}
