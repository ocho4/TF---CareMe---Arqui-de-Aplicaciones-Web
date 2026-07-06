import { Usuario } from './usuario.model';

export interface Mensaje {
  idMensaje?: number;
  remitente?: Usuario;
  contenido?: string;
  archivoUrl?: string;
  fechaEnvio?: string;
}
