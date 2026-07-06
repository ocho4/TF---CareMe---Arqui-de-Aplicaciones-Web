import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, Subject } from 'rxjs';
import { environment } from '../../environments/environment';
import { Servicio } from '../model/servicio.model';

export interface CotizacionResponse {
  horas: number;
  tarifaBase: number;
  costoBase: number;
  recargoNocturno: number;
  recargoFinDeSemana: number;
  recargoEspecialidad: number;
  descuentoLargaDuracion: number;
  costoTotal: number;
}

@Injectable({ providedIn: 'root' })
export class ServicioService {
  private url = `${environment.apiUrl}/servicios`;
  private listaCambio = new Subject<Servicio[]>();

  constructor(private http: HttpClient) {}

  listar(): Observable<Servicio[]> {
    return this.http.get<Servicio[]>(this.url);
  }

  listarMisServicios(idUsuario: number): Observable<Servicio[]> {
    return this.http.get<Servicio[]>(`${this.url}/mis-servicios/${idUsuario}`);
  }

  obtenerPorId(id: number): Observable<Servicio> {
    return this.http.get<Servicio>(`${this.url}/${id}`);
  }

  insertar(servicio: Servicio): Observable<Servicio> {
    return this.http.post<Servicio>(this.url, servicio);
  }

  listarPendientesCuidador(idUsuario: number): Observable<Servicio[]> {
    return this.http.get<Servicio[]>(`${this.url}/pendientes-cuidador/${idUsuario}`);
  }

  confirmar(id: number, idUsuarioCuidador: number): Observable<Servicio> {
    return this.http.put<Servicio>(`${this.url}/${id}/confirmar?idUsuarioCuidador=${idUsuarioCuidador}`, {});
  }

  rechazar(id: number, idUsuarioCuidador: number): Observable<Servicio> {
    return this.http.put<Servicio>(`${this.url}/${id}/rechazar?idUsuarioCuidador=${idUsuarioCuidador}`, {});
  }

  cancelar(id: number): Observable<Servicio> {
    return this.http.put<Servicio>(`${this.url}/${id}/cancelar`, {});
  }

  finalizar(id: number): Observable<Servicio> {
    return this.http.put<Servicio>(`${this.url}/${id}/finalizar`, {});
  }

  proximos(idUsuario: number): Observable<Servicio[]> {
    return this.http.get<Servicio[]>(`${this.url}/proximos/${idUsuario}`);
  }

  agenda(idUsuario: number, fechaInicio: string, fechaFin: string): Observable<Servicio[]> {
    return this.http.get<Servicio[]>(
      `${this.url}/agenda/${idUsuario}?fechaInicio=${fechaInicio}&fechaFin=${fechaFin}`
    );
  }

  cotizar(body: {
    idCuidador: number;
    fechaInicio: string;
    fechaFin: string;
    especialidadRequerida?: string;
    esHorarioNocturno: boolean;
    esFinDeSemana: boolean;
  }): Observable<CotizacionResponse> {
    return this.http.post<CotizacionResponse>(`${this.url}/cotizar`, body);
  }

  setList(lista: Servicio[]) {
    this.listaCambio.next(lista);
  }

  getListaCambio() {
    return this.listaCambio.asObservable();
  }
}
