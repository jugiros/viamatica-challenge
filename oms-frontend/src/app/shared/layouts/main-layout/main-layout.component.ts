import { Component, inject, signal, ChangeDetectionStrategy, OnInit } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../core/auth/auth.service';
import { ToastComponent } from '../../components/toast/toast.component';
import { ToastService } from '../../services/toast.service';
import { BreadcrumbComponent } from '../../components/breadcrumb/breadcrumb.component';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule, RouterLink, RouterLinkActive, RouterOutlet, ToastComponent, BreadcrumbComponent],
  templateUrl: './main-layout.component.html',
  styleUrl: './main-layout.component.scss'
})
export class MainLayoutComponent implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly toastService = inject(ToastService);

  isSidebarOpen = signal(true);

  currentUser = this.authService.currentUser;
  isAdmin = this.authService.isAdmin;

  ngOnInit() {
    // Register toast component with service
    // This will be done in the template with ViewChild
  }

  toggleSidebar() {
    this.isSidebarOpen.update(open => !open);
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  onToastReady(toastComponent: ToastComponent) {
    this.toastService.registerToastComponent(toastComponent);
  }
}
