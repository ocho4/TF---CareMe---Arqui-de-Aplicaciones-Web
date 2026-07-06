import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {
  CalificacionRequest,
  Calificacion,
  CalificacionFamiliarRequest,
  CalificacionFamiliar
} from '../model/calificacion.model';

@Injectable({ providedIn: 'root' })
export class CalificacionService {
  private url = `${environment.apiUrl}/calificaciones`;

  constructor(private http: HttpClient) {}

  registrarResena(request: CalificacionRequest): Observable<string> {
    return this.http.post(`${this.url}/resena`, request, { responseType: 'text' });
  }

  listarPorCuidador(idCuidador: number): Observable<Calificacion[]> {
    return this.http.get<Calificacion[]>(`${this.url}/cuidador/${idCuidador}`);
  }

  registrarResenaFamiliar(request: CalificacionFamiliarRequest): Observable<string> {
    return this.http.post(`${this.url}/resena-familiar`, request, { responseType: 'text' });
  }

  listarPorFamiliar(idFamiliar: number): Observable<CalificacionFamiliar[]> {
    return this.http.get<CalificacionFamiliar[]>(`${this.url}/familiar/${idFamiliar}`);
  }

  listarIdsServiciosCalificadosPorCuidador(idCuidador: number): Observable<number[]> {
    return this.http.get<number[]>(`${this.url}/resena-familiar/ids-por-cuidador/${idCuidador}`);
  }
}
