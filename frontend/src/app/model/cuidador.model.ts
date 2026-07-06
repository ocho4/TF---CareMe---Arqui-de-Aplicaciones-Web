import { Usuario } from './usuario.model';

export interface CondicionMedica {
  idCondicion?: number;
  nombreCondicion?: string;
}

export interface Cuidador {
  idCuidador: number;
  usuario?: Usuario;
  especialidad?: string;
  ubicacion?: string;
  disponibilidadTexto?: string;
  tarifaBase?: number;
  calificacionPromedio?: number;
  activo?: boolean;
  condiciones?: CondicionMedica[];
}
