import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Familiar } from '../model/familiar.model';
import { Paciente } from '../model/paciente.model';

@Injectable({ providedIn: 'root' })
export class FamiliarService {
  private urlFamiliares = `${environment.apiUrl}/familiares`;
  private urlPacientes  = `${environment.apiUrl}/pacientes`;

  constructor(private http: HttpClient) {}

  obtenerPerfilPorUsuario(idUsuario: number): Observable<Familiar> {
    return this.http.get<Familiar>(`${this.urlFamiliares}/usuario/${idUsuario}`);
  }

  listarPacientes(idFamiliar: number): Observable<Paciente[]> {
    return this.http.get<Paciente[]>(`${this.urlPacientes}/familiar/${idFamiliar}`);
  }

  registrarPaciente(idFamiliar: number, datos: any): Observable<Paciente> {
    return this.http.post<Paciente>(`${this.urlPacientes}/familiar/${idFamiliar}`, datos);
  }

  eliminarPaciente(idPaciente: number): Observable<void> {
    return this.http.delete<void>(`${this.urlPacientes}/${idPaciente}`);
  }
}
