import { Component, inject, signal, OnInit, ChangeDetectionStrategy, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ProductService } from './product.service';
import { ProductModel } from '../../core/models';
import { BaseListComponent } from '../../shared/base/base-list.component';
import { CurrencyLocalePipe } from '../../shared/pipes/currency-locale.pipe';
import { Observable, Subject } from 'rxjs';
import { ConfirmDialogComponent } from '../../shared/components/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-product-list',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule, RouterLink, CurrencyLocalePipe, ConfirmDialogComponent],
  templateUrl: './product-list.component.html',
  styleUrl: './product-list.component.scss'
})
export class ProductListComponent extends BaseListComponent<ProductModel> implements OnInit {
  private readonly productService = inject(ProductService);
  
  @ViewChild(ConfirmDialogComponent) confirmDialog!: ConfirmDialogComponent;
  private itemToDelete = signal<ProductModel | null>(null);
  private confirmSubject = new Subject<boolean>();

  ngOnInit() {
    this.loadData();
  }

  loadItems(): Observable<ProductModel[]> {
    return this.productService.getProducts();
  }

  deleteItem(id: number): Observable<void> {
    return this.productService.deleteProduct(id);
  }

  getItemName(): string {
    return 'producto';
  }

  getItemNamePlural(): string {
    return 'productos';
  }

  confirmDelete(item: ProductModel): Observable<boolean> {
    this.itemToDelete.set(item);
    this.confirmDialog.open({
      title: 'Confirmar Eliminación',
      message: `¿Estás seguro de eliminar ${this.getItemName()}?`,
      confirmText: 'Eliminar',
      onConfirm: () => this.confirmSubject.next(true),
      onCancel: () => this.confirmSubject.next(false)
    });
    return this.confirmSubject.asObservable();
  }

  deleteProduct(item: ProductModel) {
    this.deleteItemWithConfirmation(item);
  }
}
