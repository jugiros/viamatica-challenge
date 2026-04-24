export interface AuditLogModel {
  id: number;
  userId: number;
  tablaAfectada: string;
  operacion: string;
  datosAnteriores: string;
  datosNuevos: string;
  fechaEvento: string;
}
