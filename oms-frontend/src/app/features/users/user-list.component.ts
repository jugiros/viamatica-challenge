import { Component, inject, signal, OnInit, ChangeDetectionStrategy, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { UserService } from './user.service';
import { UserModel } from '../../core/models';
import { BaseListComponent } from '../../shared/base/base-list.component';
import { Observable, Subject } from 'rxjs';
import { ConfirmDialogComponent } from '../../shared/components/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-user-list',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule, RouterLink, ConfirmDialogComponent],
  templateUrl: './user-list.component.html',
  styleUrl: './user-list.component.scss'
})
export class UserListComponent extends BaseListComponent<UserModel> implements OnInit {
  private readonly userService = inject(UserService);
  
  @ViewChild(ConfirmDialogComponent) confirmDialog!: ConfirmDialogComponent;
  private itemToDelete = signal<UserModel | null>(null);
  private confirmSubject = new Subject<boolean>();

  ngOnInit() {
    this.loadData();
  }

  loadItems(): Observable<UserModel[]> {
    return this.userService.getUsers();
  }

  deleteItem(id: number): Observable<void> {
    return this.userService.deleteUser(id);
  }

  getItemName(): string {
    return 'usuario';
  }

  getItemNamePlural(): string {
    return 'usuarios';
  }

  confirmDelete(item: UserModel): Observable<boolean> {
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

  deleteUser(item: UserModel) {
    this.deleteItemWithConfirmation(item);
  }
}
