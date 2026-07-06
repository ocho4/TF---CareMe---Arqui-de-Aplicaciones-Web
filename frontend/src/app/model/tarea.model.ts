export interface Tarea {
  idTarea?: number;
  idServicio?: number;
  descripcion?: string;
  completada?: boolean;
  horaCompletado?: string;
  creadoPor?: number;
  vistaPorFamiliar?: boolean;
  vistaPorCuidador?: boolean;
}
