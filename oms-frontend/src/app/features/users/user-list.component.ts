import { Component, inject, signal, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserService } from './user.service';
import { UserModel } from '../../core/models';

@Component({
  selector: 'app-user-list',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule],
  templateUrl: './user-list.component.html',
  styleUrl: './user-list.component.scss'
})
export class UserListComponent implements OnInit {
  private readonly userService = inject(UserService);
  
  users = signal<UserModel[]>([]);
  isLoading = signal(false);
  errorMessage = signal<string | null>(null);
  
  ngOnInit() {
    this.loadUsers();
  }
  
  private loadUsers() {
    this.isLoading.set(true);
    this.errorMessage.set(null);
    
    this.userService.getUsers().subscribe({
      next: (users) => {
        this.users.set(users || []);
        this.isLoading.set(false);
      },
      error: (error) => {
        this.errorMessage.set('Error al cargar usuarios. Por favor, intenta nuevamente.');
        this.isLoading.set(false);
      }
    });
  }

  deleteUser(id: number) {
    if (confirm('¿Estás seguro de eliminar este usuario?')) {
      this.userService.deleteUser(id).subscribe({
        next: () => {
          this.loadUsers();
        },
        error: (error) => {
          this.errorMessage.set('Error al eliminar el usuario.');
        }
      });
    }
  }
}
