import { Component, inject, signal, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuditService } from './audit.service';

@Component({
  selector: 'app-audit-list',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule, FormsModule],
  templateUrl: './audit-list.component.html',
  styleUrl: './audit-list.component.scss'
})
export class AuditListComponent implements OnInit {
  private readonly auditService = inject(AuditService);
  
  auditLogs = signal<string>('');
  isLoading = signal(false);
  errorMessage = signal<string | null>(null);
  
  filters = signal({
    userId: undefined as number | undefined,
    table: '',
    operation: '',
    fechaDesde: '',
    fechaHasta: '',
    page: 0,
    size: 10
  });
  
  ngOnInit() {
    this.loadAuditLogs();
  }
  
  loadAuditLogs() {
    this.isLoading.set(true);
    this.errorMessage.set(null);
    
    this.auditService.getAuditLogs(this.filters()).subscribe({
      next: (logs) => {
        this.auditLogs.set(logs || '');
        this.isLoading.set(false);
      },
      error: (error) => {
        this.errorMessage.set('Error al cargar logs de auditoría. Por favor, intenta nuevamente.');
        this.isLoading.set(false);
      }
    });
  }

  applyFilters() {
    this.filters().page = 0;
    this.loadAuditLogs();
  }

  clearFilters() {
    this.filters.set({
      userId: undefined,
      table: '',
      operation: '',
      fechaDesde: '',
      fechaHasta: '',
      page: 0,
      size: 10
    });
    this.loadAuditLogs();
  }
}
