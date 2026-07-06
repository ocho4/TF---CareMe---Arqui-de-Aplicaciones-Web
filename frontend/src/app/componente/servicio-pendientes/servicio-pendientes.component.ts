import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule, DatePipe, DecimalPipe } from '@angular/common';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatPaginatorModule, MatPaginator } from '@angular/material/paginator';
import { MatSortModule, MatSort } from '@angular/material/sort';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDialogModule, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { ServicioService } from '../../services/servicio.service';
import { SesionService } from '../../services/sesion.service';
import { Servicio } from '../../model/servicio.model';

@Component({
  selector: 'app-servicio-pendientes',
  standalone: true,
  imports: [
    CommonModule,
    DatePipe,
    DecimalPipe,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule,
    MatTooltipModule,
    MatDialogModule,
  ],
  templateUrl: './servicio-pendientes.component.html',
  styleUrl: './servicio-pendientes.component.css',
})
export class ServicioPendientesComponent implements OnInit, AfterViewInit {
  columnas = [
    'idServicio',
    'familiar',
    'paciente',
    'fechaInicio',
    'fechaFin',
    'tipoServicio',
    'costoTotal',
    'acciones',
  ];
  dataSource = new MatTableDataSource<Servicio>();

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  private idUsuario = 0;

  constructor(
    private servicioService: ServicioService,
    private sesionService: SesionService,
    private snackBar: MatSnackBar,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.idUsuario = this.sesionService.obtener()?.idUsuario ?? 0;
    this.cargarPendientes();
    this.servicioService.getListaCambio().subscribe(() => this.cargarPendientes());
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  cargarPendientes(): void {
    if (!this.idUsuario) return;
    this.servicioService.listarPendientesCuidador(this.idUsuario).subscribe({
      next: (lista) => { this.dataSource.data = lista; },
      error: () =>
        this.snackBar.open('Error al cargar servicios', 'Cerrar', { duration: 3000 }),
    });
  }

  confirmar(id: number): void {
    this.servicioService.confirmar(id, this.idUsuario).subscribe({
      next: () => {
        this.snackBar.open('Servicio confirmado exitosamente', 'Cerrar', { duration: 3000 });
        this.cargarPendientes();
      },
      error: () => this.snackBar.open('Error al confirmar el servicio', 'Cerrar', { duration: 3000 }),
    });
  }

  confirmarRechazo(id: number): void {
    const ref = this.dialog.open(ConfirmarRechazoDialogComponent, {
      width: '420px',
      maxWidth: '95vw',
    });
    ref.afterClosed().subscribe((confirmado) => {
      if (confirmado) this.rechazar(id);
    });
  }

  private rechazar(id: number): void {
    this.servicioService.rechazar(id, this.idUsuario).subscribe({
      next: () => {
        this.snackBar.open('Servicio rechazado', 'Cerrar', { duration: 3000 });
        this.cargarPendientes();
      },
      error: () => this.snackBar.open('Error al rechazar el servicio', 'Cerrar', { duration: 3000 }),
    });
  }

  iniciales(nombres?: string, apellidos?: string): string {
    return ((nombres?.[0] ?? '') + (apellidos?.[0] ?? '')).toUpperCase();
  }

  colorPersona(nombres?: string): string {
    const colores = ['#3D44DD','#0891b2','#7c3aed','#d97706','#16a34a','#dc2626'];
    return colores[(nombres?.charCodeAt(0) ?? 65) % colores.length];
  }
}

@Component({
  selector: 'app-confirmar-rechazo-dialog',
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatIconModule, MatDialogModule],
  template: `
    <div class="confirm-wrap">
      <div class="confirm-icono">
        <mat-icon>warning_amber</mat-icon>
      </div>
      <h2 class="confirm-titulo">¿Rechazar solicitud?</h2>
      <p class="confirm-desc">
        Estás a punto de rechazar esta solicitud de servicio. El familiar sera notificado
        y tendrá que buscar otro cuidador. Esta acción no se puede deshacer.
      </p>
      <div class="confirm-acciones">
        <button mat-stroked-button (click)="dialogRef.close(false)">Cancelar</button>
        <button mat-raised-button class="btn-rechazar-confirmar" (click)="dialogRef.close(true)">
          <mat-icon>cancel</mat-icon> Rechazar
        </button>
      </div>
    </div>
  `,
  styles: [`
    .confirm-wrap { padding: 28px 24px 20px; text-align: center; }
    .confirm-icono {
      width: 60px; height: 60px; border-radius: 50%;
      background: #fff7ed; display: flex; align-items: center;
      justify-content: center; margin: 0 auto 16px;
    }
    .confirm-icono mat-icon { font-size: 30px; width: 30px; height: 30px; color: #f97316; }
    .confirm-titulo {
      font-family: 'Zilla Slab', Georgia, serif;
      font-size: 1.15rem; font-weight: 700; color: #1e293b; margin: 0 0 10px;
    }
    .confirm-desc { font-size: 0.88rem; color: #64748b; margin: 0 0 24px; line-height: 1.6; }
    .confirm-acciones { display: flex; justify-content: center; gap: 12px; }
    .btn-rechazar-confirmar { background: #ef4444 !important; color: #fff !important; border-radius: 8px !important; }
  `],
})
export class ConfirmarRechazoDialogComponent {
  constructor(public dialogRef: MatDialogRef<ConfirmarRechazoDialogComponent>) {}
}
