import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Mensaje } from '../model/mensaje.model';

export interface EnviarMensajeRequest {
  idServicio:  number;
  idRemitente: number;
  contenido:   string;
  archivoUrl?: string;
}

@Injectable({ providedIn: 'root' })
export class MensajeService {
  private url = `${environment.apiUrl}/mensajes`;

  constructor(private http: HttpClient) {}

  enviar(request: EnviarMensajeRequest): Observable<string> {
    return this.http.post(`${this.url}/enviar`, request, { responseType: 'text' });
  }

  historial(idServicio: number): Observable<Mensaje[]> {
    return this.http.get<Mensaje[]>(`${this.url}/servicio/${idServicio}`);
  }

  noLeidosPorServicio(idUsuario: number): Observable<Record<number, number>> {
    return this.http.get<Record<number, number>>(`${this.url}/no-leidos-por-servicio/${idUsuario}`);
  }

  marcarLeidos(idServicio: number, idUsuario: number): Observable<void> {
    return this.http.put<void>(`${this.url}/servicio/${idServicio}/leer/${idUsuario}`, {});
  }
}
