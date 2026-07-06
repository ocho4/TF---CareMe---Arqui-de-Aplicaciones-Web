import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface FavoritoRequest {
  idFamiliar: number;
  idCuidador: number;
}

@Injectable({ providedIn: 'root' })
export class FavoritoService {
  private url = `${environment.apiUrl}/favoritos`;

  constructor(private http: HttpClient) {}

  agregar(req: FavoritoRequest): Observable<string> {
    return this.http.post(this.url, req, { responseType: 'text' });
  }

  eliminar(idFamiliar: number, idCuidador: number): Observable<string> {
    const params = new HttpParams()
      .set('idFamiliar', idFamiliar)
      .set('idCuidador', idCuidador);
    return this.http.delete(this.url, { params, responseType: 'text' });
  }

  listarPorFamiliar(idFamiliar: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.url}/familiar/${idFamiliar}`);
  }

  listarIdsPorFamiliar(idFamiliar: number): Observable<number[]> {
    return this.http.get<number[]>(`${this.url}/familiar/${idFamiliar}/ids`);
  }
}
