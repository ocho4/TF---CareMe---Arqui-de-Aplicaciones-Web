export interface Verificacion {
  idVerificacion?: number;
  idCuidador?: number;
  nombresCuidador?: string;
  apellidosCuidador?: string;
  emailCuidador?: string;
  especialidadCuidador?: string;
  ubicacionCuidador?: string;
  tarifaBase?: number;
  estado?: string;
  motivacion?: string;
  observaciones?: string;
  fechaSolicitud?: string;
  fechaRevision?: string;
}
