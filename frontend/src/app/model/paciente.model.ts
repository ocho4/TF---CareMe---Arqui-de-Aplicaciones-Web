import { Usuario } from './usuario.model';

export interface Paciente {
  idPaciente?: number;
  usuario?: Usuario;
  fechaNacimiento?: string;
  necesidadesEspecificas?: string;
  parentesco?: string;
}
