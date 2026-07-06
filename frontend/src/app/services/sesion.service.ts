import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Usuario } from '../model/usuario.model';

const STORAGE_KEY = 'careme_usuario';

@Injectable({ providedIn: 'root' })
export class SesionService {
  private usuarioSubject = new BehaviorSubject<Usuario | null>(this.cargarDesdeStorage());
  usuario$ = this.usuarioSubject.asObservable();

  private cargarDesdeStorage(): Usuario | null {
    try {
      const raw = localStorage.getItem(STORAGE_KEY);
      return raw ? (JSON.parse(raw) as Usuario) : null;
    } catch {
      return null;
    }
  }

  guardar(usuario: Usuario): void {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(usuario));
    this.usuarioSubject.next(usuario);
  }

  cerrar(): void {
    localStorage.removeItem(STORAGE_KEY);
    this.usuarioSubject.next(null);
  }

  obtener(): Usuario | null {
    return this.usuarioSubject.getValue();
  }

  estaLogueado(): boolean {
    return this.usuarioSubject.getValue() !== null;
  }

  getToken(): string | null {
    return this.usuarioSubject.getValue()?.token ?? null;
  }
}
