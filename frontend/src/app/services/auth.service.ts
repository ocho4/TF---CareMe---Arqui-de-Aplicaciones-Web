import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Usuario } from '../model/usuario.model';

export interface RegistroRequest {
  email: string;
  password: string;
  nombres: string;
  apellidos: string;
  telefono: string;
  idTipo: number;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RepostularRequest {
  email: string;
  password: string;
  motivacion: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private url = `${environment.apiUrl}/auth`;

  constructor(private http: HttpClient) {}

  registro(datos: RegistroRequest): Observable<Usuario> {
    return this.http.post<Usuario>(`${this.url}/registro`, datos);
  }

  login(datos: LoginRequest): Observable<Usuario> {
    return this.http.post<Usuario>(`${this.url}/login`, datos);
  }

  repostular(datos: RepostularRequest): Observable<void> {
    return this.http.post<void>(`${this.url}/repostular`, datos);
  }

  recuperarPassword(datos: { email?: string; telefono?: string }): Observable<string> {
    return this.http.post(`${this.url}/recuperar-password`, datos, { responseType: 'text' });
  }

  resetPassword(datos: { token: string; nuevaPassword: string }): Observable<string> {
    return this.http.put(`${this.url}/reset-password`, datos, { responseType: 'text' });
  }
}
