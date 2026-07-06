export interface CalificacionRequest {
  idServicio: number;
  idFamiliar: number;
  idCuidador: number;
  puntuacion: number;
  comentario?: string;
}

export interface Calificacion {
  idCalificacion?: number;
  puntuacion?: number;
  comentario?: string;
  fechaRegistro?: string;
  familiar?: {
    usuario?: {
      nombres?: string;
      apellidos?: string;
    };
  };
}

export interface CalificacionFamiliarRequest {
  idServicio: number;
  idCuidador: number;
  idFamiliar: number;
  puntuacion: number;
  comentario?: string;
}

export interface CalificacionFamiliar {
  idCalificacionFamiliar?: number;
  puntuacion?: number;
  comentario?: string;
  fechaRegistro?: string;
  cuidador?: {
    usuario?: {
      nombres?: string;
      apellidos?: string;
      fotoUrl?: string;
    };
  };
  familiar?: {
    usuario?: {
      nombres?: string;
      apellidos?: string;
    };
  };
}
