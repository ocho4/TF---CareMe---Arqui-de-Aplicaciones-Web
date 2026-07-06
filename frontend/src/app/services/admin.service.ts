import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Verificacion } from '../model/verificacion.model';

@Injectable({ providedIn: 'root' })
export class AdminService {
  private url = `${environment.apiUrl}/admin`;

  constructor(private http: HttpClient) {}

  listarVerificaciones(estado?: string): Observable<Verificacion[]> {
    let params = new HttpParams();
    if (estado) params = params.set('estado', estado);
    return this.http.get<Verificacion[]>(`${this.url}/verificaciones`, { params });
  }

  contarPendientes(): Observable<{ total: number }> {
    return this.http.get<{ total: number }>(`${this.url}/verificaciones/pendientes/count`);
  }

  aprobar(idVerificacion: number, idAdmin: number, observaciones?: string): Observable<Verificacion> {
    return this.http.put<Verificacion>(`${this.url}/verificaciones/${idVerificacion}/aprobar`,
      { idAdmin, observaciones });
  }

  rechazar(idVerificacion: number, idAdmin: number, observaciones: string): Observable<Verificacion> {
    return this.http.put<Verificacion>(`${this.url}/verificaciones/${idVerificacion}/rechazar`,
      { idAdmin, observaciones });
  }

  estadoCuidador(idCuidador: number): Observable<Verificacion> {
    return this.http.get<Verificacion>(`${this.url}/verificaciones/cuidador/${idCuidador}`);
  }
}
