import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { AdminService } from '../../services/admin.service';
import { SesionService } from '../../services/sesion.service';
import { Verificacion } from '../../model/verificacion.model';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatButtonModule,
    MatCardModule,
    MatIconModule,
    MatChipsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatSnackBarModule,
    MatProgressSpinnerModule,
    MatTooltipModule,
  ],
  templateUrl: './admin.component.html',
  styleUrl: './admin.component.css',
})
export class AdminComponent implements OnInit {
  verificaciones: Verificacion[] = [];
  cargando = true;
  filtroEstado = 'pendiente';
  pendientesCount = 0;
  idAdmin = 0;

  rechazandoId: number | null = null;
  motivoRechazo = '';

  constructor(
    private adminService: AdminService,
    private sesionService: SesionService,
    private snackBar: MatSnackBar,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.idAdmin = this.sesionService.obtener()?.idUsuario ?? 0;
    this.cargar();
    this.adminService.contarPendientes().subscribe({
      next: (r) => { this.pendientesCount = r.total; this.cdr.markForCheck(); },
    });
  }

  cargar(): void {
    this.cargando = true;
    this.adminService.listarVerificaciones(this.filtroEstado || undefined).subscribe({
      next: (lista) => {
        this.verificaciones = lista;
        this.cargando = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.cargando = false;
        this.snackBar.open('Error al cargar verificaciones', 'Cerrar', { duration: 3000 });
        this.cdr.markForCheck();
      },
    });
  }

  aprobar(v: Verificacion): void {
    this.adminService.aprobar(v.idVerificacion!, this.idAdmin, 'Aprobado').subscribe({
      next: () => {
        this.snackBar.open(`✓ ${v.nombresCuidador} aprobado`, 'Cerrar', { duration: 3000 });
        this.pendientesCount = Math.max(0, this.pendientesCount - 1);
        this.cargar();
      },
      error: () => this.snackBar.open('Error al aprobar', 'Cerrar', { duration: 3000 }),
    });
  }

  iniciarRechazo(v: Verificacion): void {
    this.rechazandoId = v.idVerificacion!;
    this.motivoRechazo = '';
  }

  confirmarRechazo(v: Verificacion): void {
    if (!this.motivoRechazo.trim()) {
      this.snackBar.open('Escribe el motivo del rechazo', 'Cerrar', { duration: 2500 });
      return;
    }
    this.adminService.rechazar(v.idVerificacion!, this.idAdmin, this.motivoRechazo).subscribe({
      next: () => {
        this.snackBar.open(`✗ ${v.nombresCuidador} rechazado`, 'Cerrar', { duration: 3000 });
        this.rechazandoId = null;
        this.motivoRechazo = '';
        this.pendientesCount = Math.max(0, this.pendientesCount - 1);
        this.cargar();
      },
      error: () => this.snackBar.open('Error al rechazar', 'Cerrar', { duration: 3000 }),
    });
  }

  cancelarRechazo(): void {
    this.rechazandoId = null;
    this.motivoRechazo = '';
  }

  chipColor(estado?: string): string {
    if (estado === 'aprobado') return 'chip-verde';
    if (estado === 'rechazado') return 'chip-rojo';
    return 'chip-amarillo';
  }

  initiales(v: Verificacion): string {
    return ((v.nombresCuidador?.[0] ?? '') + (v.apellidosCuidador?.[0] ?? '')).toUpperCase();
  }
}
