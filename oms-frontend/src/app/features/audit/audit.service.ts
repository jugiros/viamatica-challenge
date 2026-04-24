import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BaseHttpService } from '../../core/services/base-http.service';

@Injectable({
  providedIn: 'root'
})
export class AuditService extends BaseHttpService {
  getAuditLogs(filters?: {
    userId?: number;
    table?: string;
    operation?: string;
    fechaDesde?: string;
    fechaHasta?: string;
    page?: number;
    size?: number;
  }): Observable<string> {
    const params = new URLSearchParams();
    if (filters?.userId) params.append('userId', filters.userId.toString());
    if (filters?.table) params.append('table', filters.table);
    if (filters?.operation) params.append('operation', filters.operation);
    if (filters?.fechaDesde) params.append('fechaDesde', filters.fechaDesde);
    if (filters?.fechaHasta) params.append('fechaHasta', filters.fechaHasta);
    if (filters?.page !== undefined) params.append('page', filters.page.toString());
    if (filters?.size !== undefined) params.append('size', filters.size.toString());
    
    const queryString = params.toString();
    const url = queryString ? `/audit?${queryString}` : '/audit';
    return this.get<string>(url);
  }
}
