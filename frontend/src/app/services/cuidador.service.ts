import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, Subject } from 'rxjs';
import { environment } from '../../environments/environment';
import { Cuidador } from '../model/cuidador.model';

export interface FiltrosCuidador {
  ubicacion?: string;
  especialidad?: string;
  disponibilidad?: string;
}

export interface RespuestaCondicion {
  mensaje: string;
  sugerencia: string;
  resultados: Cuidador[];
}

@Injectable({ providedIn: 'root' })
export class CuidadorService {
  private url = `${environment.apiUrl}/cuidadores`;
  private listaCambio = new Subject<Cuidador[]>();

  constructor(private http: HttpClient) {}

  listar(): Observable<Cuidador[]> {
    return this.http.get<Cuidador[]>(this.url);
  }

  buscarPorId(id: number): Observable<Cuidador> {
    return this.http.get<Cuidador>(`${this.url}/${id}`);
  }

  buscarPorUsuario(idUsuario: number): Observable<Cuidador> {
    return this.http.get<Cuidador>(`${this.url}/usuario/${idUsuario}`);
  }

  buscar(filtros: FiltrosCuidador): Observable<Cuidador[]> {
    let params = new HttpParams();
    if (filtros.ubicacion?.trim())    params = params.set('ubicacion',    filtros.ubicacion.trim());
    if (filtros.especialidad?.trim()) params = params.set('especialidad', filtros.especialidad.trim());
    if (filtros.disponibilidad?.trim()) params = params.set('disponibilidad', filtros.disponibilidad.trim());
    return this.http.get<Cuidador[]>(`${this.url}/buscar`, { params });
  }

  buscarPorCondicion(condicion: string): Observable<Cuidador[] | RespuestaCondicion> {
    const params = new HttpParams().set('condicion', condicion.trim());
    return this.http.get<Cuidador[] | RespuestaCondicion>(`${this.url}/condicion-medica`, { params });
  }

  setList(lista: Cuidador[]) { this.listaCambio.next(lista); }
  getListaCambio()           { return this.listaCambio.asObservable(); }
}
