import { Usuario } from './usuario.model';

export interface Familiar {
  idFamiliar: number;
  usuario?: Usuario;
  direccion?: string;
  distrito?: string;
}
