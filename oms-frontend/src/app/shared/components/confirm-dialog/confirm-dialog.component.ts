import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-confirm-dialog',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './confirm-dialog.component.html',
  styleUrl: './confirm-dialog.component.scss'
})
export class ConfirmDialogComponent {
  isOpen = signal(false);
  title = signal('');
  message = signal('');
  confirmText = signal('');
  onConfirm = signal<(() => void) | null>(null);
  onCancel = signal<(() => void) | null>(null);

  open(options: {
    title: string;
    message: string;
    confirmText: string;
    onConfirm: () => void;
    onCancel?: () => void;
  }) {
    this.title.set(options.title);
    this.message.set(options.message);
    this.confirmText.set(options.confirmText);
    this.onConfirm.set(options.onConfirm);
    this.onCancel.set(options.onCancel || (() => this.close()));
    this.isOpen.set(true);
  }

  close() {
    this.isOpen.set(false);
    this.title.set('');
    this.message.set('');
    this.confirmText.set('');
    this.onConfirm.set(null);
    this.onCancel.set(null);
  }

  handleConfirm() {
    if (this.onConfirm()) {
      this.onConfirm()!();
    }
    this.close();
  }

  handleCancel() {
    if (this.onCancel()) {
      this.onCancel()!();
    } else {
      this.close();
    }
  }
}
