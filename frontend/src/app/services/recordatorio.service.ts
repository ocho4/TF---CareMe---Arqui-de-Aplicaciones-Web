import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { RecordatorioMedicacion } from '../model/recordatorio.model';

export interface ProgramarRequest {
  idPaciente:        number;
  nombreMedicamento: string;
  horaProgramada:    string;
}

@Injectable({ providedIn: 'root' })
export class RecordatorioService {
  private url = `${environment.apiUrl}/recordatorios-medicacion`;

  constructor(private http: HttpClient) {}

  programar(req: ProgramarRequest): Observable<string> {
    return this.http.post(`${this.url}/programar`, req, { responseType: 'text' });
  }

  pendientesPorPaciente(idPaciente: number): Observable<RecordatorioMedicacion[]> {
    return this.http.get<RecordatorioMedicacion[]>(`${this.url}/paciente/${idPaciente}/pendientes`);
  }

  marcarTomado(id: number): Observable<string> {
    return this.http.put(`${this.url}/${id}/tomar`, {}, { responseType: 'text' });
  }
}
