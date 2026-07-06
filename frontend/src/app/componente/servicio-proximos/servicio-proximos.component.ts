import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatDividerModule } from '@angular/material/divider';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { RouterLink } from '@angular/router';
import { ServicioService } from '../../services/servicio.service';
import { SesionService } from '../../services/sesion.service';
import { Servicio } from '../../model/servicio.model';
import { ConfirmarCancelacionDialogComponent } from '../servicio-cancelar/servicio-cancelar.component';

@Component({
  selector: 'app-servicio-proximos',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    MatCardModule,
    MatIconModule,
    MatButtonModule,
    MatChipsModule,
    MatDividerModule,
    MatSnackBarModule,
    MatDialogModule,
  ],
  templateUrl: './servicio-proximos.component.html',
  styleUrl: './servicio-proximos.component.css',
})
export class ServicioProximosComponent implements OnInit {
  private servicioService = inject(ServicioService);
  private sesionService   = inject(SesionService);
  private snackBar        = inject(MatSnackBar);
  private cdr             = inject(ChangeDetectorRef);
  private dialog          = inject(MatDialog);

  proximos: Servicio[] = [];
  cargando = true;

  get usuario() { return this.sesionService.obtener(); }

  ngOnInit(): void {
    const idUsuario = this.usuario?.idUsuario;
    if (!idUsuario) {
      this.cargando = false;
      this.cdr.detectChanges();
      return;
    }

    this.servicioService.proximos(idUsuario).subscribe({
      next: (lista) => {
        this.proximos = lista;
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.cargando = false;
        this.cdr.detectChanges();
        this.snackBar.open('Error al cargar recordatorios', 'Cerrar', { duration: 3000 });
      },
    });
  }

  horasRestantes(fechaInicio?: string): number {
    if (!fechaInicio) return 0;
    return Math.max(0, Math.floor(
      (new Date(fechaInicio).getTime() - Date.now()) / 3_600_000
    ));
  }

  colorAlerta(horas: number): string {
    if (horas <= 2)  return 'urgente';
    if (horas <= 6)  return 'pronto';
    return 'normal';
  }

  abrirCancelacion(servicio: Servicio): void {
    const horasRestantes = this.horasRestantes(servicio.fechaInicio);
    const penalizacion = horasRestantes < 24 ? (servicio.costoTotal ?? 0) * 0.2 : 0;
    const ref = this.dialog.open(ConfirmarCancelacionDialogComponent, {
      width: '420px',
      data: { penalizacion },
    });
    ref.afterClosed().subscribe((confirmado) => {
      if (!confirmado || !servicio.idServicio) return;
      this.servicioService.cancelar(servicio.idServicio).subscribe({
        next: (resp) => {
          const msg =
            (resp.costoTotal ?? 0) > 0
              ? `Servicio cancelado. Penalización aplicada: S/ ${resp.costoTotal?.toFixed(2)}`
              : 'Servicio cancelado sin penalización.';
          this.snackBar.open(msg, 'Cerrar', { duration: 5000 });
          this.proximos = this.proximos.filter((s) => s.idServicio !== servicio.idServicio);
          this.cdr.detectChanges();
        },
        error: () => this.snackBar.open('Error al cancelar el servicio', 'Cerrar', { duration: 3000 }),
      });
    });
  }
}
