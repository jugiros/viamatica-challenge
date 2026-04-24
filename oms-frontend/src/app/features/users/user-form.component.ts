import { Component, inject, signal, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { UserService } from './user.service';
import { UserModel } from '../../core/models';
import { ToastService } from '../../shared/services/toast.service';

@Component({
  selector: 'app-user-form',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule, FormsModule],
  templateUrl: './user-form.component.html',
  styleUrl: './user-form.component.scss'
})
export class UserFormComponent implements OnInit {
  private readonly userService = inject(UserService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly toastService = inject(ToastService);
  
  user = signal<UserModel | null>(null);
  isLoading = signal(false);
  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);
  
  userId = signal<number | null>(null);
  userRequest = signal<any>({
    name: '',
    email: '',
    role: 'USER',
    active: true
  });
  
  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.userId.set(Number(id));
      this.loadUser(Number(id));
    }
  }
  
  private loadUser(id: number) {
    this.isLoading.set(true);
    this.userService.getUserById(id).subscribe({
      next: (user) => {
        this.user.set(user);
        this.userRequest.set({
          name: user.name,
          email: user.email,
          role: user.role,
          active: user.active
        });
        this.isLoading.set(false);
      },
      error: (error) => {
        this.errorMessage.set('Error al cargar el usuario.');
        this.isLoading.set(false);
      }
    });
  }
  
  saveUser() {
    if (!this.userRequest().name || !this.userRequest().email) {
      this.errorMessage.set('Por favor, complete todos los campos requeridos.');
      return;
    }
    
    this.isLoading.set(true);
    this.errorMessage.set(null);
    this.successMessage.set(null);
    
    const id = this.userId();
    if (id) {
      this.userService.updateUser(id, this.userRequest()).subscribe({
        next: (user) => {
          this.toastService.showSuccess('Usuario actualizado exitosamente.');
          this.user.set(user);
          this.successMessage.set('Usuario actualizado exitosamente.');
          this.isLoading.set(false);
          this.router.navigate(['/users']);
        },
        error: (error) => {
          this.toastService.showError('Error al actualizar el usuario.');
          this.errorMessage.set('Error al actualizar el usuario.');
          this.isLoading.set(false);
        }
      });
    }
  }
  
  cancel() {
    this.router.navigate(['/users']);
  }
}
