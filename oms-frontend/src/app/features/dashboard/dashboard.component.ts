import { Component, inject, signal, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent {
  stats = signal([
    { label: 'Total Productos', value: '0', icon: '📦', color: 'bg-blue-500' },
    { label: 'Órdenes Hoy', value: '0', icon: '🛒', color: 'bg-green-500' },
    { label: 'Órdenes Pendientes', value: '0', icon: '⏳', color: 'bg-yellow-500' },
    { label: 'Ingresos del Mes', value: '$0', icon: '💰', color: 'bg-purple-500' }
  ]);

  recentActivities = signal([
    { action: 'Sistema inicializado', time: 'Hace un momento', icon: '🚀' }
  ]);
}
