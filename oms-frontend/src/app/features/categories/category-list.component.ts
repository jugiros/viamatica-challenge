import { Component, inject, signal, OnInit, ChangeDetectionStrategy, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { CategoryService } from './category.service';
import { CategoryModel } from '../../core/models';
import { BaseListComponent } from '../../shared/base/base-list.component';
import { Observable, Subject } from 'rxjs';
import { ConfirmDialogComponent } from '../../shared/components/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-category-list',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule, RouterLink, ConfirmDialogComponent],
  templateUrl: './category-list.component.html',
  styleUrl: './category-list.component.scss'
})
export class CategoryListComponent extends BaseListComponent<CategoryModel> implements OnInit {
  private readonly categoryService = inject(CategoryService);
  
  @ViewChild(ConfirmDialogComponent) confirmDialog!: ConfirmDialogComponent;
  private itemToDelete = signal<CategoryModel | null>(null);
  private confirmSubject = new Subject<boolean>();

  ngOnInit() {
    this.loadData();
  }

  loadItems(): Observable<CategoryModel[]> {
    return this.categoryService.getCategories();
  }

  deleteItem(id: number): Observable<void> {
    return this.categoryService.deleteCategory(id);
  }

  getItemName(): string {
    return 'categoría';
  }

  getItemNamePlural(): string {
    return 'categorías';
  }

  confirmDelete(item: CategoryModel): Observable<boolean> {
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

  deleteCategory(item: CategoryModel) {
    this.deleteItemWithConfirmation(item);
  }
}
