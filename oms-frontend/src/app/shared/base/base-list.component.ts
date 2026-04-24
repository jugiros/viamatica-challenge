import { signal, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ToastService } from '../services/toast.service';

export abstract class BaseListComponent<T> {
  items = signal<T[]>([]);
  isLoading = signal(false);
  errorMessage = signal<string | null>(null);
  protected readonly toastService = inject(ToastService);

  abstract loadItems(): Observable<T[]>;
  abstract deleteItem(id: number): Observable<void>;
  abstract getItemName(): string;
  abstract getItemNamePlural(): string;
  abstract confirmDelete(item: T): Observable<boolean>;

  loadData() {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.loadItems().subscribe({
      next: (items) => {
        if (Array.isArray(items)) {
          this.items.set(items);
        } else if (items && typeof items === 'object') {
          this.items.set([]);
        } else {
          this.items.set([]);
        }
        this.isLoading.set(false);
      },
      error: (error) => {
        this.toastService.showError(`Error al cargar ${this.getItemNamePlural()}. Por favor, intenta nuevamente.`);
        this.errorMessage.set(`Error al cargar ${this.getItemNamePlural()}. Por favor, intenta nuevamente.`);
        this.isLoading.set(false);
      }
    });
  }

  deleteItemWithConfirmation(item: T) {
    this.confirmDelete(item).subscribe({
      next: (confirmed) => {
        if (confirmed) {
          this.deleteItem(this.getItemId(item)).subscribe({
            next: () => {
              this.toastService.showSuccess(`${this.getItemName()} eliminado exitosamente.`);
              this.loadData();
            },
            error: (error) => {
              this.toastService.showError(`Error al eliminar ${this.getItemName()}.`);
              this.errorMessage.set(`Error al eliminar ${this.getItemName()}.`);
            }
          });
        }
      }
    });
  }

  protected getItemId(item: T): number {
    return (item as any).id;
  }
}
