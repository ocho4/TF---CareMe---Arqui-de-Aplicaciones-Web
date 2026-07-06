import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Notificacion } from '../model/notificacion.model';

@Injectable({ providedIn: 'root' })
export class NotificacionesService {
  private base = environment.apiUrl;

  constructor(private http: HttpClient) {}

  obtenerParaCuidador(idUsuario: number): Observable<Notificacion[]> {
    return this.http.get<Notificacion[]>(`${this.base}/notificaciones/cuidador/${idUsuario}`);
  }

  obtenerParaFamiliar(idUsuario: number): Observable<Notificacion[]> {
    return this.http.get<Notificacion[]>(`${this.base}/notificaciones/familiar/${idUsuario}`);
  }

  marcarLeidosServicio(idServicio: number, idUsuario: number): Observable<void> {
    return this.http.put<void>(`${this.base}/mensajes/servicio/${idServicio}/leer/${idUsuario}`, {});
  }
}
