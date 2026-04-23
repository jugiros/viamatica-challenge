export interface AuditLogModel {
  id: number;
  userId: number;
  userName: string;
  action: string;
  entity: string;
  entityId: number;
  timestamp: string;
  details?: string;
}
