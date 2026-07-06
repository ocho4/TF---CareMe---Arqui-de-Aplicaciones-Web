import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterOutlet, RouterLink, RouterLinkActive, NavigationEnd } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatBadgeModule } from '@angular/material/badge';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
import { SesionService } from './services/sesion.service';
import { NotificacionesService } from './services/notificaciones.service';
import { Notificacion } from './model/notificacion.model';
import { Subscription, filter } from 'rxjs';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatBadgeModule,
    MatMenuModule,
    MatDividerModule,
  ],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App implements OnInit, OnDestroy {
  private sesionService       = inject(SesionService);
  private notifService        = inject(NotificacionesService);
  private router              = inject(Router);

  usuario$ = this.sesionService.usuario$;

  notificaciones: Notificacion[] = [];
  totalNotificaciones = 0;

  private intervalId: ReturnType<typeof setInterval> | null = null;
  private routerSub?: Subscription;
  private usuarioSub?: Subscription;

  readonly iconosPorTipo: Record<string, string> = {
    perfil:   'edit_note',
    solicitud: 'assignment',
    mensaje:  'chat',
    proximo:  'alarm',
    pago:     'payments',
    tarea:    'checklist',
  };

  private readonly rolesConNotif = ['cuidador', 'familiar'];

  ngOnInit(): void {
    this.usuarioSub = this.usuario$.subscribe(usuario => {
      if (usuario?.rol && this.rolesConNotif.includes(usuario.rol)) {
        this.cargarNotificaciones(usuario);
        this.iniciarPolling(usuario);
      } else {
        this.notificaciones = [];
        this.totalNotificaciones = 0;
        this.detenerPolling();
      }
    });

    this.routerSub = this.router.events.pipe(
      filter(e => e instanceof NavigationEnd)
    ).subscribe(() => {
      const usuario = this.sesionService.obtener();
      if (usuario?.rol && this.rolesConNotif.includes(usuario.rol)) {
        this.cargarNotificaciones(usuario);
      }
    });
  }

  ngOnDestroy(): void {
    this.detenerPolling();
    this.routerSub?.unsubscribe();
    this.usuarioSub?.unsubscribe();
  }

  cargarNotificaciones(usuario: any): void {
    const idUsuario = usuario?.idUsuario;
    if (!idUsuario) return;
    const obs$ = usuario.rol === 'familiar'
      ? this.notifService.obtenerParaFamiliar(idUsuario)
      : this.notifService.obtenerParaCuidador(idUsuario);
    obs$.subscribe({
      next: (lista) => {
        this.notificaciones = lista;
        this.totalNotificaciones = lista.reduce((sum, n) => sum + n.cantidad, 0);
      },
      error: () => {},
    });
  }

  navegarNotificacion(ruta: string): void {
    this.router.navigate([ruta]);
  }

  cerrarSesion(): void {
    this.sesionService.cerrar();
    this.detenerPolling();
    this.notificaciones = [];
    this.totalNotificaciones = 0;
    this.router.navigate(['/login']);
  }

  private iniciarPolling(usuario: any): void {
    this.detenerPolling();
    this.intervalId = setInterval(() => this.cargarNotificaciones(usuario), 60_000);
  }

  private detenerPolling(): void {
    if (this.intervalId !== null) {
      clearInterval(this.intervalId);
      this.intervalId = null;
    }
  }
}
