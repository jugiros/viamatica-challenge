import { Injectable } from '@angular/core';
import { ToastComponent, ToastType } from '../components/toast/toast.component';

@Injectable({
  providedIn: 'root'
})
export class ToastService {
  private toastComponent?: ToastComponent;

  registerToastComponent(component: ToastComponent) {
    this.toastComponent = component;
  }

  show(message: string, type: ToastType = 'info', duration?: number) {
    if (this.toastComponent) {
      this.toastComponent.show(message, type, duration);
    }
  }

  showSuccess(message: string, duration?: number) {
    this.show(message, 'success', duration);
  }

  showError(message: string, duration?: number) {
    this.show(message, 'error', duration);
  }

  showInfo(message: string, duration?: number) {
    this.show(message, 'info', duration);
  }

  showWarning(message: string, duration?: number) {
    this.show(message, 'warning', duration);
  }
}
