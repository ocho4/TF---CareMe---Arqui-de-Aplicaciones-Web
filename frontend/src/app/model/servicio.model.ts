import { Familiar } from './familiar.model';
import { Cuidador } from './cuidador.model';
import { Paciente } from './paciente.model';

export interface Servicio {
  idServicio?: number;
  familiar?: Partial<Familiar>;
  cuidador?: Partial<Cuidador>;
  paciente?: Partial<Paciente>;
  fechaInicio?: string;
  fechaFin?: string;
  fechaCancelacion?: string;
  tipoServicio?: string;
  recargoHorario?: number;
  descuentoAplicado?: number;
  costoTotal?: number;
  estado?: string;
  pagado?: boolean;
  calificado?: boolean;
}
