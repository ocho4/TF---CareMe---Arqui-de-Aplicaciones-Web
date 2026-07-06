export interface Notificacion {
  tipo: 'perfil' | 'solicitud' | 'mensaje' | 'proximo';
  titulo: string;
  descripcion: string;
  ruta: string;
  cantidad: number;
}
