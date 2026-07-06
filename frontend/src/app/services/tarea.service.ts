import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Tarea } from '../model/tarea.model';

@Injectable({ providedIn: 'root' })
export class TareaService {
  private url = `${environment.apiUrl}/tareas`;

  constructor(private http: HttpClient) {}

  listarPorServicio(idServicio: number): Observable<Tarea[]> {
    return this.http.get<Tarea[]>(`${this.url}/servicio/${idServicio}`);
  }

  crear(tarea: Tarea): Observable<Tarea> {
    return this.http.post<Tarea>(this.url, tarea);
  }

  completar(id: number): Observable<Tarea> {
    return this.http.put<Tarea>(`${this.url}/${id}/completar`, {});
  }

  descompletar(id: number): Observable<Tarea> {
    return this.http.put<Tarea>(`${this.url}/${id}/descompletar`, {});
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }

  marcarVistoCuidador(idServicio: number): Observable<void> {
    return this.http.put<void>(`${this.url}/servicio/${idServicio}/visto-cuidador`, {});
  }

  marcarVistoFamiliar(idServicio: number): Observable<void> {
    return this.http.put<void>(`${this.url}/servicio/${idServicio}/visto-familiar`, {});
  }

  noVistosCuidador(idUsuario: number): Observable<Record<number, number>> {
    return this.http.get<Record<number, number>>(`${this.url}/no-vistos-cuidador/${idUsuario}`);
  }

  noVistosFamiliar(idUsuario: number): Observable<Record<number, number>> {
    return this.http.get<Record<number, number>>(`${this.url}/no-vistos-familiar/${idUsuario}`);
  }
}
