import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface ProcesarPagoRequest {
  idServicio:        number;
  metodoPago:        string;
  datosTransaccion:  string;
  telefonoYape?:     string;
}

export interface PagoHistorial {
  idPago:           number;
  monto:            number;
  estadoPago:       string;
  fechaPago:        string;
  codigoOperacion?: string;
  metodoPago?:      string;
  observacion?:     string;
  servicio?: {
    idServicio:  number;
    estado:      string;
    costoTotal:  number;
    cuidador?:   { usuario?: { nombres?: string; apellidos?: string } };
    familiar?:   { usuario?: { nombres?: string; apellidos?: string } };
  };
}

@Injectable({ providedIn: 'root' })
export class PagoService {
  private url = `${environment.apiUrl}/pagos`;

  constructor(private http: HttpClient) {}

  procesar(req: ProcesarPagoRequest): Observable<string> {
    return this.http.post(`${this.url}/procesar`, req, { responseType: 'text' });
  }

  listarPorUsuario(idUsuario: number): Observable<PagoHistorial[]> {
    return this.http.get<PagoHistorial[]>(`${this.url}/usuario/${idUsuario}`);
  }
}
