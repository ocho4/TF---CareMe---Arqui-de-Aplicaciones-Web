import { AfterViewInit, Component, Inject, OnInit, ViewChild } from '@angular/core';
import { CommonModule, DatePipe, DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatPaginatorModule, MatPaginator } from '@angular/material/paginator';
import { MatSortModule, MatSort } from '@angular/material/sort';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import {
  MatDialog,
  MatDialogModule,
  MatDialogRef,
  MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { Router } from '@angular/router';
import { ServicioService } from '../../services/servicio.service';
import { CalificacionService } from '../../services/calificacion.service';
import { SesionService } from '../../services/sesion.service';
import { MensajeService } from '../../services/mensaje.service';
import { TareaService } from '../../services/tarea.service';
import { FamiliarService } from '../../services/familiar.service';
import { CuidadorService } from '../../services/cuidador.service';
import { FavoritoService } from '../../services/favorito.service';
import { Servicio } from '../../model/servicio.model';

/* ── Diálogo cancelación ── */
@Component({
  selector: 'app-confirmar-cancelacion-dialog',
  standalone: true,
  imports: [CommonModule, MatDialogModule, MatButtonModule, DecimalPipe],
  template: `
    <h2 mat-dialog-title>Confirmar cancelación</h2>
    <mat-dialog-content>
      @if (data.penalizacion > 0) {
        <p class="advertencia">
          <strong>⚠️ Cancelación tardía</strong><br />
          Quedan menos de 24 horas para el inicio del servicio.<br />
          Se aplicará una penalización del 20%:
          <strong>S/ {{ data.penalizacion | number: '1.2-2' }}</strong>
        </p>
      } @else {
        <p class="sin-cargo">
          <strong>✅ Sin penalización</strong><br />
          Cancelas con más de 24 horas de anticipación. No se generará ningún cargo.
        </p>
      }
      <p>¿Deseas continuar con la cancelación?</p>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>No, volver</button>
      <button mat-raised-button color="warn" [mat-dialog-close]="true">Sí, cancelar servicio</button>
    </mat-dialog-actions>
  `,
  styles: [`
    .advertencia { color: #b45309; background: #fef3c7; padding: 12px; border-radius: 6px; }
    .sin-cargo   { color: #065f46; background: #d1fae5; padding: 12px; border-radius: 6px; }
    p { margin-top: 12px; }
    mat-dialog-actions { padding-bottom: 12px; }
  `],
})
export class ConfirmarCancelacionDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<ConfirmarCancelacionDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { penalizacion: number }
  ) {}
}

/* ── Diálogo calificación ── */
@Component({
  selector: 'app-calificar-dialog',
  standalone: true,
  imports: [CommonModule, FormsModule, MatDialogModule, MatButtonModule, MatIconModule, MatFormFieldModule, MatInputModule],
  template: `
    <h2 mat-dialog-title>Calificar servicio</h2>
    <mat-dialog-content>
      <p class="subtitulo">¿Cómo fue la atención de <strong>{{ data.nombreCuidador }}</strong>?</p>

      <div class="estrellas">
        @for (i of [1,2,3,4,5]; track i) {
          <button mat-icon-button (click)="puntuacion = i" [class.activa]="i <= puntuacion">
            <mat-icon>{{ i <= puntuacion ? 'star' : 'star_border' }}</mat-icon>
          </button>
        }
        <span class="label-estrellas">{{ etiquetaPuntuacion() }}</span>
      </div>

      <mat-form-field appearance="outline" class="comentario-field">
        <mat-label>Comentario (opcional)</mat-label>
        <textarea matInput [(ngModel)]="comentario" rows="3"
                  placeholder="Comparte tu experiencia con la comunidad..."></textarea>
      </mat-form-field>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>Cancelar</button>
      <button mat-raised-button color="primary"
              [disabled]="puntuacion === 0"
              [mat-dialog-close]="{ puntuacion, comentario }">
        <mat-icon>send</mat-icon> Enviar reseña
      </button>
    </mat-dialog-actions>
  `,
  styles: [`
    .subtitulo { margin-bottom: 16px; color: #555; }
    .estrellas { display: flex; align-items: center; gap: 4px; margin-bottom: 16px; }
    .estrellas button { color: #ccc; }
    .estrellas button.activa { color: #f59e0b; }
    .label-estrellas { font-size: 13px; color: #666; margin-left: 8px; }
    .comentario-field { width: 100%; }
    mat-dialog-content { min-width: 340px; }
    mat-dialog-actions { padding-bottom: 12px; }
  `],
})
export class CalificarDialogComponent {
  puntuacion = 0;
  comentario = '';

  constructor(
    public dialogRef: MatDialogRef<CalificarDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { nombreCuidador: string }
  ) {}

  etiquetaPuntuacion(): string {
    const etiquetas = ['', 'Muy malo', 'Malo', 'Regular', 'Bueno', 'Excelente'];
    return etiquetas[this.puntuacion] ?? '';
  }
}

/* ── Diálogo calificación al familiar (para cuidador) ── */
@Component({
  selector: 'app-calificar-familiar-dialog',
  standalone: true,
  imports: [CommonModule, FormsModule, MatDialogModule, MatButtonModule, MatIconModule, MatFormFieldModule, MatInputModule],
  template: `
    <h2 mat-dialog-title>Calificar al familiar</h2>
    <mat-dialog-content>
      <p class="subtitulo">¿Cómo fue la experiencia con <strong>{{ data.nombreFamiliar }}</strong>?</p>

      <div class="estrellas">
        @for (i of [1,2,3,4,5]; track i) {
          <button mat-icon-button (click)="puntuacion = i" [class.activa]="i <= puntuacion">
            <mat-icon>{{ i <= puntuacion ? 'star' : 'star_border' }}</mat-icon>
          </button>
        }
        <span class="label-estrellas">{{ etiquetaPuntuacion() }}</span>
      </div>

      <mat-form-field appearance="outline" class="comentario-field">
        <mat-label>Comentario (opcional)</mat-label>
        <textarea matInput [(ngModel)]="comentario" rows="3"
                  placeholder="Comparte cómo fue trabajar con esta familia..."></textarea>
      </mat-form-field>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>Cancelar</button>
      <button mat-raised-button color="primary"
              [disabled]="puntuacion === 0"
              [mat-dialog-close]="{ puntuacion, comentario }">
        <mat-icon>send</mat-icon> Enviar calificación
      </button>
    </mat-dialog-actions>
  `,
  styles: [`
    .subtitulo { margin-bottom: 16px; color: #555; }
    .estrellas { display: flex; align-items: center; gap: 4px; margin-bottom: 16px; }
    .estrellas button { color: #ccc; }
    .estrellas button.activa { color: #f59e0b; }
    .label-estrellas { font-size: 13px; color: #666; margin-left: 8px; }
    .comentario-field { width: 100%; }
    mat-dialog-content { min-width: 340px; }
    mat-dialog-actions { padding-bottom: 12px; }
  `],
})
export class CalificarFamiliarDialogComponent {
  puntuacion = 0;
  comentario = '';

  constructor(
    public dialogRef: MatDialogRef<CalificarFamiliarDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { nombreFamiliar: string }
  ) {}

  etiquetaPuntuacion(): string {
    const etiquetas = ['', 'Muy malo', 'Malo', 'Regular', 'Bueno', 'Excelente'];
    return etiquetas[this.puntuacion] ?? '';
  }
}

/* ── Componente principal ── */
@Component({
  selector: 'app-servicio-cancelar',
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
    MatCardModule,
    MatChipsModule,
    MatSnackBarModule,
    MatTooltipModule,
    MatDialogModule,
  ],
  templateUrl: './servicio-cancelar.component.html',
  styleUrl: './servicio-cancelar.component.css',
})
export class ServicioCancelarComponent implements OnInit, AfterViewInit {
  columnas: string[] = [];
  dataSource = new MatTableDataSource<Servicio>();
  esFamiliar = false;
  noLeidos: Record<number, number> = {};
  tareasNoVistas: Record<number, number> = {};
  idCuidadorActual = 0;
  idFamiliarActual = 0;
  calificacionesFamiliar = new Set<number>();
  idsFavoritos = new Set<number>();

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private servicioService: ServicioService,
    private calificacionService: CalificacionService,
    private sesionService: SesionService,
    private mensajeService: MensajeService,
    private tareaService: TareaService,
    private familiarService: FamiliarService,
    private cuidadorService: CuidadorService,
    private favoritoService: FavoritoService,
    private snackBar: MatSnackBar,
    private dialog: MatDialog,
    private router: Router
  ) {}

  irAlChat(idServicio: number): void {
    this.router.navigate(['/chat', idServicio]);
  }

  irAlPago(idServicio: number): void {
    this.router.navigate(['/pago', idServicio]);
  }

  irAlSeguimiento(idServicio: number): void {
    this.router.navigate(['/seguimiento', idServicio]);
  }

  private idUsuario = 0;

  ngOnInit(): void {
    const usuario = this.sesionService.obtener();
    this.esFamiliar = usuario?.rol === 'familiar';
    this.idUsuario  = usuario?.idUsuario ?? 0;
    this.columnas = [
      'idServicio', 'cuidador', 'paciente', 'fechaInicio',
      'fechaFin', 'costoTotal', 'estado', 'chat',
      ...(this.esFamiliar ? ['pago', 'favorito'] : []),
      'seguimiento', 'acciones',
    ];
    this.cargarActivos();
    this.cargarNoLeidos();
    this.cargarTareasNoVistas();

    if (this.esFamiliar && usuario?.idUsuario) {
      this.familiarService.obtenerPerfilPorUsuario(usuario.idUsuario).subscribe({
        next: (familiar) => {
          this.idFamiliarActual = familiar.idFamiliar;
          this.favoritoService.listarIdsPorFamiliar(familiar.idFamiliar).subscribe({
            next: (ids) => { this.idsFavoritos = new Set(ids); },
            error: () => {},
          });
        },
        error: () => {},
      });
    }

    if (!this.esFamiliar && usuario?.idUsuario) {
      this.cuidadorService.buscarPorUsuario(usuario.idUsuario).subscribe({
        next: (c) => {
          this.idCuidadorActual = c.idCuidador ?? 0;
          this.calificacionService.listarIdsServiciosCalificadosPorCuidador(this.idCuidadorActual).subscribe({
            next: (ids) => { this.calificacionesFamiliar = new Set(ids); },
            error: () => {},
          });
        },
        error: () => {},
      });
    }
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  cargarTareasNoVistas(): void {
    const obs$ = this.esFamiliar
      ? this.tareaService.noVistosFamiliar(this.idUsuario)
      : this.tareaService.noVistosCuidador(this.idUsuario);
    obs$.subscribe({ next: (mapa) => { this.tareasNoVistas = mapa; }, error: () => {} });
  }

  cargarNoLeidos(): void {
    this.mensajeService.noLeidosPorServicio(this.idUsuario).subscribe({
      next: (mapa) => { this.noLeidos = mapa; },
      error: () => {},
    });
  }

  cargarActivos(): void {
    this.servicioService.listarMisServicios(this.idUsuario).subscribe({
      next: (lista) => { this.dataSource.data = lista; },
      error: () =>
        this.snackBar.open('Error al cargar servicios', 'Cerrar', { duration: 3000 }),
    });
  }

  private calcularPenalizacion(servicio: Servicio): number {
    if (!servicio.fechaInicio) return 0;
    const inicio = new Date(servicio.fechaInicio).getTime();
    const ahora = Date.now();
    const horasRestantes = (inicio - ahora) / 3_600_000;
    return horasRestantes < 24 ? (servicio.costoTotal ?? 0) * 0.2 : 0;
  }

  abrirDialogo(servicio: Servicio): void {
    const penalizacion = this.calcularPenalizacion(servicio);
    const ref = this.dialog.open(ConfirmarCancelacionDialogComponent, {
      width: '420px',
      data: { penalizacion },
    });
    ref.afterClosed().subscribe((confirmado) => {
      if (confirmado) this.ejecutarCancelacion(servicio.idServicio!);
    });
  }

  private ejecutarCancelacion(id: number): void {
    this.servicioService.cancelar(id).subscribe({
      next: (resp) => {
        const msg =
          (resp.costoTotal ?? 0) > 0
            ? `Servicio cancelado. Penalización aplicada: S/ ${resp.costoTotal?.toFixed(2)}`
            : 'Servicio cancelado sin penalización.';
        this.snackBar.open(msg, 'Cerrar', { duration: 5000 });
        this.cargarActivos();
      },
      error: () =>
        this.snackBar.open('Error al cancelar el servicio', 'Cerrar', { duration: 3000 }),
    });
  }

  finalizarServicio(servicio: Servicio): void {
    if (!servicio.idServicio) return;
    this.servicioService.finalizar(servicio.idServicio).subscribe({
      next: () => {
        this.snackBar.open('Servicio marcado como finalizado', 'Cerrar', { duration: 3000 });
        this.cargarActivos();
      },
      error: () => this.snackBar.open('Error al finalizar el servicio', 'Cerrar', { duration: 3000 }),
    });
  }

  abrirCalificacion(servicio: Servicio): void {
    const nombreCuidador = `${servicio.cuidador?.usuario?.['nombres'] ?? ''} ${servicio.cuidador?.usuario?.['apellidos'] ?? ''}`.trim();
    const ref = this.dialog.open(CalificarDialogComponent, {
      width: '420px',
      data: { nombreCuidador },
    });

    ref.afterClosed().subscribe((resultado) => {
      if (!resultado) return;
      this.ejecutarCalificacion(servicio, resultado.puntuacion, resultado.comentario);
    });
  }

  abrirCalificacionFamiliar(servicio: Servicio): void {
    const nombreFamiliar = `${(servicio.familiar as any)?.usuario?.nombres ?? ''} ${(servicio.familiar as any)?.usuario?.apellidos ?? ''}`.trim();
    const ref = this.dialog.open(CalificarFamiliarDialogComponent, {
      width: '420px',
      data: { nombreFamiliar },
    });
    ref.afterClosed().subscribe((resultado) => {
      if (!resultado) return;
      const idFamiliar = (servicio.familiar as any)?.idFamiliar ?? 0;
      this.calificacionService.registrarResenaFamiliar({
        idServicio: servicio.idServicio!,
        idCuidador: this.idCuidadorActual,
        idFamiliar,
        puntuacion: resultado.puntuacion,
        comentario: resultado.comentario,
      }).subscribe({
        next: () => {
          this.calificacionesFamiliar.add(servicio.idServicio!);
          this.calificacionesFamiliar = new Set(this.calificacionesFamiliar);
          this.snackBar.open('Calificación enviada correctamente.', 'Cerrar', { duration: 4000 });
        },
        error: (err) => {
          let msg = 'Error al enviar calificación';
          if (err.error) {
            try { const p = JSON.parse(err.error); msg = p.mensaje ?? p.error ?? msg; }
            catch { msg = typeof err.error === 'string' ? err.error : msg; }
          }
          this.snackBar.open(msg, 'Cerrar', { duration: 4000 });
        },
      });
    });
  }

  yaCalificoFamiliar(idServicio?: number): boolean {
    return !!idServicio && this.calificacionesFamiliar.has(idServicio);
  }

  esFavoritoCuidador(idCuidador?: number): boolean {
    return !!idCuidador && this.idsFavoritos.has(idCuidador);
  }

  toggleFavoritoDesdeServicio(servicio: Servicio): void {
    const idCuidador = (servicio.cuidador as any)?.idCuidador as number;
    if (!idCuidador) return;

    if (this.idsFavoritos.has(idCuidador)) {
      this.favoritoService.eliminar(this.idFamiliarActual, idCuidador).subscribe({
        next: () => {
          this.idsFavoritos.delete(idCuidador);
          this.idsFavoritos = new Set(this.idsFavoritos);
          this.snackBar.open('Cuidador eliminado de favoritos', 'Cerrar', { duration: 2500 });
        },
        error: () => this.snackBar.open('Error al eliminar de favoritos', 'Cerrar', { duration: 3000 }),
      });
    } else {
      this.favoritoService.agregar({ idFamiliar: this.idFamiliarActual, idCuidador }).subscribe({
        next: () => {
          this.idsFavoritos.add(idCuidador);
          this.idsFavoritos = new Set(this.idsFavoritos);
          this.snackBar.open('Guardado en favoritos', 'Cerrar', { duration: 2500 });
        },
        error: () => this.snackBar.open('Error al guardar en favoritos', 'Cerrar', { duration: 3000 }),
      });
    }
  }

  private ejecutarCalificacion(servicio: Servicio, puntuacion: number, comentario: string): void {
    const idFamiliar = (servicio.familiar as any)?.idFamiliar ?? 0;
    const idCuidador = (servicio.cuidador as any)?.idCuidador ?? 0;
    this.calificacionService.registrarResena({
      idServicio: servicio.idServicio!,
      idFamiliar,
      idCuidador,
      puntuacion,
      comentario,
    }).subscribe({
      next: () => {
        this.snackBar.open('¡Reseña enviada! Gracias por tu opinión.', 'Cerrar', { duration: 5000 });
        this.cargarActivos();
      },
      error: (err) => {
        let msg = 'Error al enviar la reseña';
        if (err.error) {
          try {
            const parsed = JSON.parse(err.error);
            msg = parsed.mensaje ?? parsed.error ?? msg;
          } catch {
            msg = typeof err.error === 'string' ? err.error : msg;
          }
        }
        this.snackBar.open(msg, 'Cerrar', { duration: 5000 });
      },
    });
  }

  esProximo(fechaInicio?: string): boolean {
    if (!fechaInicio) return false;
    const horasRestantes = (new Date(fechaInicio).getTime() - Date.now()) / 3_600_000;
    return horasRestantes > 0 && horasRestantes < 24;
  }

  yaTermino(fechaFin?: string): boolean {
    if (!fechaFin) return false;
    return Date.now() >= new Date(fechaFin).getTime();
  }

  tieneTareasNoVistas(idServicio?: number): boolean {
    return !!idServicio && (this.tareasNoVistas[idServicio] ?? 0) > 0;
  }

  tieneNoLeidos(idServicio?: number): boolean {
    return !!idServicio && (this.noLeidos[idServicio] ?? 0) > 0;
  }

  colorEstado(estado: string): 'primary' | 'accent' | 'warn' {
    if (estado === 'CONFIRMADO') return 'primary';
    if (estado === 'FINALIZADO') return 'primary';
    if (estado === 'RECHAZADO' || estado === 'CANCELADO') return 'warn';
    return 'accent';
  }

  iniciales(nombres?: string, apellidos?: string): string {
    return ((nombres?.[0] ?? '') + (apellidos?.[0] ?? '')).toUpperCase();
  }

  colorPersona(nombres?: string): string {
    const colores = ['#3D44DD','#0891b2','#7c3aed','#d97706','#16a34a','#dc2626'];
    return colores[(nombres?.charCodeAt(0) ?? 65) % colores.length];
  }
}
