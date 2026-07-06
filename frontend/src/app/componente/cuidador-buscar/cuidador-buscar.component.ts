import { ChangeDetectorRef, Component, Inject, inject, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatTabsModule } from '@angular/material/tabs';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDividerModule } from '@angular/material/divider';
import { MatDialog, MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { RouterLink } from '@angular/router';
import { CuidadorService, RespuestaCondicion } from '../../services/cuidador.service';
import { CalificacionService } from '../../services/calificacion.service';
import { FavoritoService } from '../../services/favorito.service';
import { FamiliarService } from '../../services/familiar.service';
import { SesionService } from '../../services/sesion.service';
import { Cuidador } from '../../model/cuidador.model';
import { Calificacion } from '../../model/calificacion.model';

/* ── Diálogo de reseñas ── */
@Component({
  selector: 'app-resenas-dialog',
  standalone: true,
  imports: [CommonModule, DatePipe, MatDialogModule, MatButtonModule, MatIconModule, MatProgressSpinnerModule],
  template: `
    <h2 mat-dialog-title>
      <mat-icon style="vertical-align:middle;margin-right:6px">star_rate</mat-icon>
      Reseñas de {{ data.nombreCuidador }}
    </h2>
    <mat-dialog-content>
      @if (cargando) {
        <div class="spinner-wrap"><mat-spinner diameter="40" /></div>
      } @else if (resenas.length === 0) {
        <div class="sin-resenas">
          <mat-icon>rate_review</mat-icon>
          <p>Este cuidador aún no tiene reseñas.</p>
        </div>
      } @else {
        @for (r of resenas; track r.idCalificacion) {
          <div class="resena-item">
            <div class="resena-header">
              <div class="avatar-mini">{{ iniciales(r) }}</div>
              <div>
                <strong>{{ r.familiar?.usuario?.nombres }} {{ r.familiar?.usuario?.apellidos }}</strong>
                <div class="resena-fecha">{{ r.fechaRegistro | date: 'dd/MM/yyyy' }}</div>
              </div>
              <div class="estrellas-resena">
                @for (i of [1,2,3,4,5]; track i) {
                  <mat-icon [style.color]="i <= (r.puntuacion ?? 0) ? '#f59e0b' : '#e2e8f0'"
                            style="font-size:18px;width:18px;height:18px">star</mat-icon>
                }
                <span class="puntuacion-texto">{{ r.puntuacion }}/5</span>
              </div>
            </div>
            @if (r.comentario) {
              <p class="resena-comentario">"{{ r.comentario }}"</p>
            }
          </div>
        }
      }
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>Cerrar</button>
    </mat-dialog-actions>
  `,
  styles: [`
    mat-dialog-content { min-width: 380px; max-height: 420px; }
    .spinner-wrap { display:flex; justify-content:center; padding: 32px; }
    .sin-resenas { display:flex; flex-direction:column; align-items:center; padding:32px; color:#94a3b8; }
    .sin-resenas mat-icon { font-size:48px; width:48px; height:48px; margin-bottom:8px; }
    .resena-item { border-bottom: 1px solid #f1f5f9; padding: 12px 0; }
    .resena-item:last-child { border-bottom: none; }
    .resena-header { display:flex; align-items:center; gap:10px; margin-bottom:6px; }
    .avatar-mini { width:36px; height:36px; border-radius:50%; background:#2563eb; color:#fff; display:flex; align-items:center; justify-content:center; font-size:13px; font-weight:700; flex-shrink:0; }
    .resena-fecha { font-size:12px; color:#94a3b8; }
    .estrellas-resena { display:flex; align-items:center; gap:2px; margin-left:auto; }
    .puntuacion-texto { font-size:13px; color:#64748b; margin-left:4px; }
    .resena-comentario { font-size:14px; color:#475569; font-style:italic; margin: 4px 0 0 46px; }
  `],
})
export class ResenasDialogComponent implements OnInit {
  resenas: Calificacion[] = [];
  cargando = true;

  constructor(
    public dialogRef: MatDialogRef<ResenasDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { idCuidador: number; nombreCuidador: string },
    private calificacionService: CalificacionService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.calificacionService.listarPorCuidador(this.data.idCuidador).subscribe({
      next: (lista) => {
        this.resenas = lista;
        this.cargando = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.cargando = false;
        this.cdr.markForCheck();
      },
    });
  }

  iniciales(r: Calificacion): string {
    const n = r.familiar?.usuario?.nombres?.charAt(0) ?? '?';
    const a = r.familiar?.usuario?.apellidos?.charAt(0) ?? '';
    return (n + a).toUpperCase();
  }
}

/* ── Componente principal ── */
@Component({
  selector: 'app-cuidador-buscar',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    MatTabsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatButtonModule,
    MatCardModule,
    MatChipsModule,
    MatIconModule,
    MatSlideToggleModule,
    MatSnackBarModule,
    MatDividerModule,
    MatDialogModule,
    MatTooltipModule,
  ],
  templateUrl: './cuidador-buscar.component.html',
  styleUrl: './cuidador-buscar.component.css',
})
export class CuidadorBuscarComponent implements OnInit {

  /* ── Datos de filtros ── */
  readonly distritos = [
    'Ancón','Ate','Barranco','Breña','Carabayllo','Cercado de Lima',
    'Chorrillos','Cieneguilla','Comas','El Agustino','Independencia',
    'Jesús María','La Molina','La Victoria','Lince','Los Olivos',
    'Lurigancho','Lurín','Magdalena del Mar','Miraflores','Pachacámac',
    'Pueblo Libre','Puente Piedra','Rímac','San Borja','San Isidro',
    'San Juan de Lurigancho','San Juan de Miraflores','San Luis',
    'San Martín de Porres','San Miguel','Santa Anita','Santiago de Surco',
    'Surquillo','Villa El Salvador','Villa María del Triunfo',
  ];

  readonly especialidades = [
    'Cuidados Generales','Geriatría','Enfermería','Fisioterapia',
    'Terapia Ocupacional','Neurología','Cuidados Paliativos',
    'Psicología','Nutrición','Pediatría','Demencia y Alzheimer','Parkinson',
  ];

  readonly rangosCalificacion = [
    { label: '⭐⭐⭐⭐⭐  4.5 – 5.0  Excelente', value: '4.5-5.0' },
    { label: '⭐⭐⭐⭐    4.0 – 4.5  Muy bueno', value: '4.0-4.5' },
    { label: '⭐⭐⭐      3.5 – 4.0  Bueno',     value: '3.5-4.0' },
    { label: '⭐⭐        3.0 – 3.5  Regular',   value: '3.0-3.5' },
  ];

  /* ── Tab 1 ── */
  form: FormGroup;
  resultados: Cuidador[]    = [];
  cargandoInicial           = false;
  buscando                  = false;
  buscado                   = false;
  ordenarPorCalificacion    = false;

  /* ── Tab 2 ── */
  formCondicion: FormGroup;
  resultadosCondicion: Cuidador[] = [];
  mensajeCondicion    = '';
  sugerenciaCondicion = '';
  buscandoCondicion   = false;
  buscadoCondicion    = false;

  /* ── Favoritos ── */
  idFamiliarActual = 0;
  esFamiliar       = false;
  idsFavoritos     = new Set<number>();

  private cdr            = inject(ChangeDetectorRef);
  private favoritoService = inject(FavoritoService);
  private familiarService = inject(FamiliarService);
  private sesionService   = inject(SesionService);

  constructor(
    private fb: FormBuilder,
    private cuidadorService: CuidadorService,
    private snackBar: MatSnackBar,
    private dialog: MatDialog
  ) {
    this.form = this.fb.group({
      ubicacion:      [''],
      especialidad:   [''],
      disponibilidad: [null as Date | null],
      calificacion:   [''],
    });

    this.formCondicion = this.fb.group({
      condicion: [''],
    });
  }

  ngOnInit(): void {
    this.cargandoInicial = true;
    this.cuidadorService.listar().subscribe({
      next: (lista) => {
        this.resultados      = lista;
        this.buscado         = true;
        this.cargandoInicial = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.cargandoInicial = false;
        this.cdr.markForCheck();
        this.snackBar.open('Error al cargar cuidadores', 'Cerrar', { duration: 3000 });
      },
    });

    const usuario = this.sesionService.obtener();
    this.esFamiliar = usuario?.rol === 'familiar';
    if (this.esFamiliar && usuario?.idUsuario) {
      this.familiarService.obtenerPerfilPorUsuario(usuario.idUsuario).subscribe({
        next: (familiar) => {
          this.idFamiliarActual = familiar.idFamiliar;
          this.favoritoService.listarIdsPorFamiliar(familiar.idFamiliar).subscribe({
            next: (ids) => { this.idsFavoritos = new Set(ids); this.cdr.markForCheck(); },
            error: () => {},
          });
        },
        error: () => {},
      });
    }
  }

  esFavorito(idCuidador?: number): boolean {
    return !!idCuidador && this.idsFavoritos.has(idCuidador);
  }

  toggleFavorito(cuidador: Cuidador): void {
    const id = cuidador.idCuidador!;
    if (this.idsFavoritos.has(id)) {
      this.favoritoService.eliminar(this.idFamiliarActual, id).subscribe({
        next: () => {
          this.idsFavoritos.delete(id);
          this.idsFavoritos = new Set(this.idsFavoritos);
          this.snackBar.open('Eliminado de favoritos', 'Cerrar', { duration: 2500 });
          this.cdr.markForCheck();
        },
        error: () => this.snackBar.open('Error al eliminar de favoritos', 'Cerrar', { duration: 3000 }),
      });
    } else {
      this.favoritoService.agregar({ idFamiliar: this.idFamiliarActual, idCuidador: id }).subscribe({
        next: () => {
          this.idsFavoritos.add(id);
          this.idsFavoritos = new Set(this.idsFavoritos);
          this.snackBar.open('Guardado en favoritos', 'Cerrar', { duration: 2500 });
          this.cdr.markForCheck();
        },
        error: () => this.snackBar.open('Error al guardar en favoritos', 'Cerrar', { duration: 3000 }),
      });
    }
  }

  /* ── Tab 1 ── */
  buscar(): void {
    this.buscando = true;
    this.buscado  = false;

    const v = this.form.value;
    this.cuidadorService.buscar({
      ubicacion:      v.ubicacion     || undefined,
      especialidad:   v.especialidad  || undefined,
      disponibilidad: v.disponibilidad ? this.diaEnEspanol(v.disponibilidad) : undefined,
    }).subscribe({
      next: (lista) => {
        this.resultados = this.aplicarFiltroCalificacion(lista);
        this.buscado    = true;
        this.buscando   = false;
        if (this.ordenarPorCalificacion) this.aplicarOrden();
        this.cdr.markForCheck();
      },
      error: () => {
        this.buscando = false;
        this.cdr.markForCheck();
        this.snackBar.open('Error al buscar cuidadores', 'Cerrar', { duration: 3000 });
      },
    });
  }

  toggleOrden(): void {
    this.ordenarPorCalificacion = !this.ordenarPorCalificacion;
    this.aplicarOrden();
  }

  private aplicarOrden(): void {
    if (this.ordenarPorCalificacion) {
      this.resultados = [...this.resultados].sort(
        (a, b) => (b.calificacionPromedio ?? 0) - (a.calificacionPromedio ?? 0)
      );
    }
  }

  private aplicarFiltroCalificacion(lista: Cuidador[]): Cuidador[] {
    const rango = this.form.value.calificacion as string;
    if (!rango) return lista;
    const [min, max] = rango.split('-').map(Number);
    return lista.filter(c => {
      const cal = c.calificacionPromedio ?? 0;
      return cal === 0 || (cal >= min && cal <= max);
    });
  }

  diaEnEspanolPublico(fecha: Date | null): string {
    if (!fecha) return '';
    const dias = ['domingos','lunes','martes','miércoles','jueves','viernes','sábados'];
    return dias[fecha.getDay()];
  }

  private diaEnEspanol(fecha: Date): string {
    const dias = ['domingo','lunes','martes','miércoles','jueves','viernes','sábado'];
    return dias[fecha.getDay()];
  }

  limpiar(): void {
    this.form.reset();
    this.ordenarPorCalificacion = false;
    this.ngOnInit();
  }

  /* ── Tab 2 ── */
  buscarPorCondicion(): void {
    const condicion = this.formCondicion.value.condicion?.trim();
    if (!condicion) {
      this.formCondicion.markAllAsTouched();
      return;
    }

    this.buscandoCondicion  = true;
    this.buscadoCondicion   = false;
    this.mensajeCondicion   = '';
    this.sugerenciaCondicion = '';

    this.cuidadorService.buscarPorCondicion(condicion).subscribe({
      next: (resp) => {
        this.buscandoCondicion = false;
        this.buscadoCondicion  = true;

        if (Array.isArray(resp)) {
          this.resultadosCondicion = resp;
        } else {
          const sin = resp as RespuestaCondicion;
          this.resultadosCondicion = sin.resultados ?? [];
          this.mensajeCondicion    = sin.mensaje    ?? '';
          this.sugerenciaCondicion = sin.sugerencia ?? '';
        }
        this.cdr.markForCheck();
      },
      error: () => {
        this.buscandoCondicion = false;
        this.cdr.markForCheck();
        this.snackBar.open('Error al buscar por condición médica', 'Cerrar', { duration: 3000 });
      },
    });
  }

  limpiarCondicion(): void {
    this.formCondicion.reset();
    this.resultadosCondicion = [];
    this.buscadoCondicion    = false;
    this.mensajeCondicion    = '';
    this.sugerenciaCondicion = '';
  }

  estrellas(n: number): number[] {
    return Array.from({ length: Math.round(n ?? 0) });
  }

  verResenas(cuidador: Cuidador): void {
    const nombre = `${cuidador.usuario?.nombres ?? ''} ${cuidador.usuario?.apellidos ?? ''}`.trim();
    const ref = this.dialog.open(ResenasDialogComponent, {
      width: '480px',
      data: { idCuidador: cuidador.idCuidador, nombreCuidador: nombre },
    });
    ref.afterClosed().subscribe(() => {
      this.cuidadorService.buscarPorId(cuidador.idCuidador).subscribe({
        next: (actualizado) => {
          const actualizar = (lista: Cuidador[]) => {
            const idx = lista.findIndex(c => c.idCuidador === cuidador.idCuidador);
            if (idx !== -1) lista[idx] = { ...lista[idx], calificacionPromedio: actualizado.calificacionPromedio };
          };
          actualizar(this.resultados);
          actualizar(this.resultadosCondicion);
          this.resultados = [...this.resultados];
          this.resultadosCondicion = [...this.resultadosCondicion];
          this.cdr.markForCheck();
        },
      });
    });
  }
}
