import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule, DatePipe, DecimalPipe } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatDividerModule } from '@angular/material/divider';
import { SesionService } from '../../services/sesion.service';
import { ServicioService } from '../../services/servicio.service';
import { Servicio } from '../../model/servicio.model';
import { Usuario } from '../../model/usuario.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    DatePipe,
    DecimalPipe,
    RouterLink,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatDividerModule,
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css',
})
export class DashboardComponent implements OnInit {
  private sesionService = inject(SesionService);
  private servicioService = inject(ServicioService);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  usuario: Usuario | null = null;
  proximoServicio: Servicio | null = null;
  totalServicios = 0;
  serviciosCompletados = 0;
  gastoTotal = 0;
  solicitudesPendientes = 0;
  cargando = true;

  ngOnInit(): void {
    this.usuario = this.sesionService.obtener();
    if (!this.usuario) {
      this.router.navigate(['/login']);
      return;
    }
    this.cargarDatos();
  }

  private cargarDatos(): void {
    const idUsuario = this.usuario?.idUsuario;

    this.servicioService.listar().subscribe({
      next: (lista) => {
        // Filter to only this user's services
        const misServicios = lista.filter(s => {
          const idFamiliarUsuario = (s.familiar as any)?.usuario?.idUsuario;
          const idCuidadorUsuario = (s.cuidador as any)?.usuario?.idUsuario;
          return idFamiliarUsuario === idUsuario || idCuidadorUsuario === idUsuario;
        });

        this.totalServicios = misServicios.length;
        this.serviciosCompletados = misServicios.filter(s => s.estado === 'FINALIZADO').length;
        this.gastoTotal = misServicios
          .filter(s => s.estado === 'FINALIZADO' || s.estado === 'CONFIRMADO')
          .reduce((acc, s) => acc + (s.costoTotal ?? 0), 0);
        this.solicitudesPendientes = misServicios.filter(s => s.estado === 'SOLICITADO').length;

        // Find próximo servicio: CONFIRMADO + starts within next 24h
        const ahora = Date.now();
        const limite = ahora + 24 * 60 * 60 * 1000;
        this.proximoServicio = misServicios
          .filter(s => {
            if (s.estado !== 'CONFIRMADO' || !s.fechaInicio) return false;
            const inicio = new Date(s.fechaInicio).getTime();
            return inicio >= ahora && inicio <= limite;
          })
          .sort((a, b) => new Date(a.fechaInicio!).getTime() - new Date(b.fechaInicio!).getTime())[0] ?? null;

        this.cargando = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.cargando = false;
        this.cdr.markForCheck();
      },
    });
  }

  get saludoHora(): string {
    const hora = new Date().getHours();
    if (hora < 12) return 'Buenos días';
    if (hora < 19) return 'Buenas tardes';
    return 'Buenas noches';
  }

  get esFamiliar(): boolean {
    return this.usuario?.rol === 'familiar';
  }

  get esCuidador(): boolean {
    return this.usuario?.rol === 'cuidador';
  }
}
