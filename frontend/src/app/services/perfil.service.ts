import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Cuidador } from '../model/cuidador.model';
import { Paciente } from '../model/paciente.model';

export interface PerfilCuidadorRequest {
  fotoUrl?: string;
  ubicacion?: string;
  especialidad?: string;
  disponibilidadTexto?: string;
  tarifaBase?: number;
}

export interface PerfilFamiliarRequest {
  fotoUrl?: string;
  direccion?: string;
  distrito?: string;
}

export interface PerfilPacienteRequest {
  fotoUrl?: string;
  necesidadesEspecificas?: string;
  nombres?: string;
  apellidos?: string;
  telefono?: string;
  fechaNacimiento?: string;
  parentesco?: string;
}

export interface FamiliarPerfil {
  idFamiliar?: number;
  direccion?: string;
  distrito?: string;
  relacionPaciente?: string;
  usuario?: { nombres?: string; apellidos?: string; fotoUrl?: string; };
}

export interface PacientePerfil {
  idPaciente?: number;
  necesidadesEspecificas?: string;
  usuario?: { nombres?: string; apellidos?: string; fotoUrl?: string; };
}

@Injectable({ providedIn: 'root' })
export class PerfilService {
  private base = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getCuidadorPorUsuario(idUsuario: number): Observable<Cuidador> {
    return this.http.get<Cuidador>(`${this.base}/cuidadores/usuario/${idUsuario}`);
  }

  getFamiliarPorUsuario(idUsuario: number): Observable<FamiliarPerfil> {
    return this.http.get<FamiliarPerfil>(`${this.base}/familiares/usuario/${idUsuario}`);
  }

  getPacientePorUsuario(idUsuario: number): Observable<PacientePerfil> {
    return this.http.get<PacientePerfil>(`${this.base}/pacientes/usuario/${idUsuario}`);
  }

  actualizarCuidador(id: number, req: PerfilCuidadorRequest): Observable<Cuidador> {
    return this.http.put<Cuidador>(`${this.base}/cuidadores/${id}/perfil`, req);
  }

  actualizarFamiliar(id: number, req: PerfilFamiliarRequest): Observable<FamiliarPerfil> {
    return this.http.put<FamiliarPerfil>(`${this.base}/familiares/${id}/perfil`, req);
  }

  actualizarPaciente(id: number, req: PerfilPacienteRequest): Observable<Paciente> {
    return this.http.put<Paciente>(`${this.base}/pacientes/${id}/perfil`, req);
  }
}
