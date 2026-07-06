import { Paciente } from './paciente.model';

export interface RecordatorioMedicacion {
  idRecordatorio?: number;
  paciente?: Paciente;
  nombreMedicamento?: string;
  horaProgramada?: string;
  tomado?: boolean;
}
