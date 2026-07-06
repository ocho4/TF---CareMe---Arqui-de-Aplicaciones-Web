import { Component, Inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatDialogModule, MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDividerModule } from '@angular/material/divider';
import { FamiliarService } from '../../services/familiar.service';
import { PerfilService } from '../../services/perfil.service';
import { SesionService } from '../../services/sesion.service';
import { Paciente } from '../../model/paciente.model';

@Component({
  selector: 'app-pacientes',
  standalone: true,
  imports: [
    CommonModule,
    MatButtonModule,
    MatCardModule,
    MatIconModule,
    MatChipsModule,
    MatDialogModule,
    MatSnackBarModule,
    MatProgressSpinnerModule,
    MatDividerModule,
  ],
  templateUrl: './pacientes.component.html',
  styleUrl: './pacientes.component.css',
})
export class PacientesComponent implements OnInit {
  pacientes: Paciente[] = [];
  cargando = true;
  idFamiliar = 0;

  constructor(
    private familiarService: FamiliarService,
    private perfilService: PerfilService,
    private sesionService: SesionService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    const idUsuario = this.sesionService.obtener()?.idUsuario;
    if (!idUsuario) { this.cargando = false; return; }

    this.familiarService.obtenerPerfilPorUsuario(idUsuario).subscribe({
      next: (familiar) => { this.idFamiliar = familiar.idFamiliar; this.cargarPacientes(); },
      error: () => {
        this.cargando = false;
        this.snackBar.open('No se encontró tu perfil', 'Cerrar', { duration: 3000 });
        this.cdr.markForCheck();
      },
    });
  }

  cargarPacientes(): void {
    this.familiarService.listarPacientes(this.idFamiliar).subscribe({
      next: (lista) => { this.pacientes = lista; this.cargando = false; this.cdr.markForCheck(); },
      error: () => { this.cargando = false; this.cdr.markForCheck(); },
    });
  }

  abrirNuevo(): void {
    const ref = this.dialog.open(PacienteFormDialogComponent, {
      width: '600px',
      maxWidth: '96vw',
      data: null,
    });
    ref.afterClosed().subscribe((dto) => {
      if (!dto) return;
      this.familiarService.registrarPaciente(this.idFamiliar, dto).subscribe({
        next: (nuevo) => {
          this.pacientes = [...this.pacientes, nuevo];
          this.snackBar.open('Familiar registrado con éxito', 'Cerrar', { duration: 3000 });
          this.cdr.markForCheck();
        },
        error: () => this.snackBar.open('Error al registrar familiar', 'Cerrar', { duration: 3000 }),
      });
    });
  }

  confirmarEliminar(p: Paciente): void {
    const nombre = `${p.usuario?.nombres ?? ''} ${p.usuario?.apellidos ?? ''}`.trim();
    const ref = this.dialog.open(ConfirmarEliminarDialogComponent, {
      width: '420px',
      maxWidth: '95vw',
      data: { nombre },
    });
    ref.afterClosed().subscribe((confirmado) => {
      if (!confirmado || !p.idPaciente) return;
      this.familiarService.eliminarPaciente(p.idPaciente).subscribe({
        next: () => {
          this.pacientes = this.pacientes.filter(x => x.idPaciente !== p.idPaciente);
          this.snackBar.open(`${nombre} eliminado correctamente`, 'Cerrar', { duration: 3000 });
          this.cdr.markForCheck();
        },
        error: () => this.snackBar.open('Error al eliminar familiar', 'Cerrar', { duration: 3000 }),
      });
    });
  }

  abrirEditar(p: Paciente): void {
    const ref = this.dialog.open(PacienteFormDialogComponent, {
      width: '600px',
      maxWidth: '96vw',
      data: p,
    });
    ref.afterClosed().subscribe((dto) => {
      if (!dto || !p.idPaciente) return;
      this.perfilService.actualizarPaciente(p.idPaciente, dto).subscribe({
        next: (actualizado) => {
          this.pacientes = this.pacientes.map(x =>
            x.idPaciente === p.idPaciente ? { ...x, ...actualizado } : x
          );
          this.snackBar.open('Datos actualizados correctamente', 'Cerrar', { duration: 3000 });
          this.cdr.markForCheck();
        },
        error: () => this.snackBar.open('Error al actualizar datos', 'Cerrar', { duration: 3000 }),
      });
    });
  }

  edad(fechaNacimiento?: string): string {
    if (!fechaNacimiento) return '—';
    const anios = Math.floor(
      (Date.now() - new Date(fechaNacimiento).getTime()) / (365.25 * 24 * 60 * 60 * 1000)
    );
    return `${anios} años`;
  }

  iniciales(p: Paciente): string {
    return ((p.usuario?.nombres?.[0] ?? '') + (p.usuario?.apellidos?.[0] ?? '')).toUpperCase();
  }

  colorAvatar(p: Paciente): string {
    const colores = ['#3D44DD','#0891b2','#7c3aed','#d97706','#16a34a','#dc2626'];
    return colores[(p.usuario?.nombres?.charCodeAt(0) ?? 65) % colores.length];
  }
}

/* ══════════════════════════════════════════════════════
   DIÁLOGO — crear y editar paciente
══════════════════════════════════════════════════════ */
@Component({
  selector: 'app-paciente-form-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    MatDividerModule,
  ],
  template: `
    <div class="dialog-header">
      <mat-icon class="dialog-icono">{{ esEdicion ? 'edit' : 'person_add' }}</mat-icon>
      <div>
        <h2 mat-dialog-title class="dialog-titulo">
          {{ esEdicion ? 'Editar datos del familiar' : 'Registrar nuevo familiar' }}
        </h2>
        <p class="dialog-sub">{{ esEdicion ? 'Actualiza la información del paciente' : 'Completa los datos del familiar a tu cargo' }}</p>
      </div>
    </div>

    <mat-divider />

    <mat-dialog-content class="dialog-contenido">
      <form [formGroup]="form">

        <!-- Foto -->
        <div class="seccion-foto">
          @if (form.get('fotoUrl')?.value) {
            <img [src]="form.get('fotoUrl')?.value" class="foto-preview"
                 (error)="$any($event.target).style.display='none'" />
          } @else {
            <div class="foto-placeholder">
              <mat-icon>person</mat-icon>
            </div>
          }
          <mat-form-field appearance="outline" class="campo-foto">
            <mat-label>Foto de perfil (URL)</mat-label>
            <mat-icon matPrefix>link</mat-icon>
            <input matInput formControlName="fotoUrl" placeholder="https://..." />
            <mat-hint>Pega el enlace de una imagen</mat-hint>
          </mat-form-field>
        </div>

        <div class="fila-doble">
          <mat-form-field appearance="outline">
            <mat-label>Nombres</mat-label>
            <mat-icon matPrefix>person</mat-icon>
            <input matInput formControlName="nombres" placeholder="Ej: Ana María" />
            @if (form.get('nombres')?.invalid && form.get('nombres')?.touched) {
              <mat-error>Mínimo 2 caracteres</mat-error>
            }
          </mat-form-field>
          <mat-form-field appearance="outline">
            <mat-label>Apellidos</mat-label>
            <mat-icon matPrefix>person</mat-icon>
            <input matInput formControlName="apellidos" placeholder="Ej: Rodríguez Lima" />
            @if (form.get('apellidos')?.invalid && form.get('apellidos')?.touched) {
              <mat-error>Mínimo 2 caracteres</mat-error>
            }
          </mat-form-field>
        </div>

        <p class="etiqueta-grupo">Fecha de nacimiento</p>
        <div class="fila-triple">
          <mat-form-field appearance="outline">
            <mat-label>Día</mat-label>
            <mat-icon matPrefix>cake</mat-icon>
            <mat-select formControlName="diaFecha">
              @for (d of dias; track d) {
                <mat-option [value]="d">{{ d }}</mat-option>
              }
            </mat-select>
            @if (form.get('diaFecha')?.invalid && form.get('diaFecha')?.touched) {
              <mat-error>Requerido</mat-error>
            }
          </mat-form-field>
          <mat-form-field appearance="outline">
            <mat-label>Mes</mat-label>
            <mat-select formControlName="mesFecha">
              @for (m of meses; track m.v) {
                <mat-option [value]="m.v">{{ m.l }}</mat-option>
              }
            </mat-select>
            @if (form.get('mesFecha')?.invalid && form.get('mesFecha')?.touched) {
              <mat-error>Requerido</mat-error>
            }
          </mat-form-field>
          <mat-form-field appearance="outline">
            <mat-label>Año</mat-label>
            <mat-select formControlName="anioFecha">
              @for (a of anios; track a) {
                <mat-option [value]="a">{{ a }}</mat-option>
              }
            </mat-select>
            @if (form.get('anioFecha')?.invalid && form.get('anioFecha')?.touched) {
              <mat-error>Requerido</mat-error>
            }
          </mat-form-field>
        </div>

        <div class="fila-doble">
          <mat-form-field appearance="outline">
            <mat-label>Parentesco</mat-label>
            <mat-icon matPrefix>family_restroom</mat-icon>
            <mat-select formControlName="parentesco">
              <mat-option value="Padre/Madre">Padre / Madre</mat-option>
              <mat-option value="Abuelo/a">Abuelo/a</mat-option>
              <mat-option value="Hijo/a">Hijo/a</mat-option>
              <mat-option value="Hermano/a">Hermano/a</mat-option>
              <mat-option value="Cónyuge">Cónyuge</mat-option>
              <mat-option value="Tío/a">Tío/a</mat-option>
              <mat-option value="Otro">Otro</mat-option>
            </mat-select>
            @if (form.get('parentesco')?.invalid && form.get('parentesco')?.touched) {
              <mat-error>Selecciona el parentesco</mat-error>
            }
          </mat-form-field>
          <span></span>
        </div>

        <div class="fila-doble">
          <mat-form-field appearance="outline">
            <mat-label>Teléfono (opcional)</mat-label>
            <mat-icon matPrefix>phone</mat-icon>
            <input matInput type="tel" formControlName="telefono" placeholder="987654321" />
          </mat-form-field>
          <mat-form-field appearance="outline">
            <mat-label>Correo (opcional)</mat-label>
            <mat-icon matPrefix>email</mat-icon>
            <input matInput type="email" formControlName="email" placeholder="correo@ejemplo.com" />
          </mat-form-field>
        </div>

        <mat-form-field appearance="outline" class="campo-full">
          <mat-label>Necesidades especiales de cuidado</mat-label>
          <mat-icon matPrefix>favorite</mat-icon>
          <textarea matInput formControlName="necesidadesEspecificas" rows="3"
            placeholder="Describe condiciones de salud, medicamentos, movilidad, etc."></textarea>
          <mat-hint>Esta información ayuda al cuidador a prepararse</mat-hint>
        </mat-form-field>

      </form>
    </mat-dialog-content>

    <mat-divider />

    <mat-dialog-actions align="end" class="dialog-acciones">
      <button mat-stroked-button (click)="cerrar()">
        <mat-icon>close</mat-icon> Cancelar
      </button>
      <button mat-raised-button color="primary" (click)="guardar()">
        <mat-icon>{{ esEdicion ? 'save' : 'person_add' }}</mat-icon>
        {{ esEdicion ? 'Guardar cambios' : 'Registrar' }}
      </button>
    </mat-dialog-actions>
  `,
  styles: [`
    .dialog-header {
      display: flex; align-items: center; gap: 14px;
      padding: 20px 24px 12px;
    }
    .dialog-icono {
      font-size: 32px; width: 32px; height: 32px;
      color: #3D44DD; flex-shrink: 0;
    }
    .dialog-titulo {
      font-family: 'Zilla Slab', Georgia, serif;
      font-size: 1.15rem; font-weight: 700;
      color: #1e293b; margin: 0 0 2px;
    }
    .dialog-sub { font-size: 12.5px; color: #64748b; margin: 0; }
    .dialog-contenido { padding: 20px 24px 8px !important; }
    .seccion-foto {
      display: flex; align-items: center; gap: 16px; margin-bottom: 16px;
    }
    .foto-preview {
      width: 72px; height: 72px; border-radius: 50%;
      object-fit: cover; border: 3px solid #e8edf8; flex-shrink: 0;
    }
    .foto-placeholder {
      width: 72px; height: 72px; border-radius: 50%;
      background: #f1f5f9; display: flex; align-items: center;
      justify-content: center; flex-shrink: 0; border: 2px dashed #cbd5e1;
    }
    .foto-placeholder mat-icon { color: #94a3b8; font-size: 32px; width: 32px; height: 32px; }
    .campo-foto { flex: 1; }
    .etiqueta-grupo { font-size: 0.78rem; color: #64748b; margin: 4px 0 2px 2px; font-weight: 600; }
    .fila-triple { display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 12px; margin-bottom: 4px; }
    .fila-doble { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; margin-bottom: 4px; }
    .campo-full { width: 100%; margin-bottom: 4px; }
    mat-form-field { width: 100%; }
    .dialog-acciones { padding: 12px 24px 16px !important; gap: 10px; }
    @media (max-width: 500px) { .fila-doble { grid-template-columns: 1fr; } }
  `],
})
export class PacienteFormDialogComponent {
  form: FormGroup;
  esEdicion: boolean;

  readonly dias = Array.from({ length: 31 }, (_, i) => i + 1);
  readonly meses = [
    { v: 1, l: 'Enero' }, { v: 2, l: 'Febrero' }, { v: 3, l: 'Marzo' },
    { v: 4, l: 'Abril' }, { v: 5, l: 'Mayo' }, { v: 6, l: 'Junio' },
    { v: 7, l: 'Julio' }, { v: 8, l: 'Agosto' }, { v: 9, l: 'Septiembre' },
    { v: 10, l: 'Octubre' }, { v: 11, l: 'Noviembre' }, { v: 12, l: 'Diciembre' },
  ];
  readonly anios = Array.from({ length: 110 }, (_, i) => new Date().getFullYear() - i);

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<PacienteFormDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: Paciente | null,
  ) {
    this.esEdicion = data !== null;

    let diaFecha: number | '' = '';
    let mesFecha: number | '' = '';
    let anioFecha: number | '' = '';
    if (data?.fechaNacimiento) {
      const [y, m, d] = data.fechaNacimiento.split('-').map(Number);
      anioFecha = y; mesFecha = m; diaFecha = d;
    }

    this.form = this.fb.group({
      fotoUrl:               [data?.usuario?.fotoUrl ?? ''],
      nombres:               [data?.usuario?.nombres ?? '',  [Validators.required, Validators.minLength(2)]],
      apellidos:             [data?.usuario?.apellidos ?? '', [Validators.required, Validators.minLength(2)]],
      diaFecha:              [diaFecha,  Validators.required],
      mesFecha:              [mesFecha,  Validators.required],
      anioFecha:             [anioFecha, Validators.required],
      parentesco:            [data?.parentesco ?? '', Validators.required],
      telefono:              [data?.usuario?.['telefono'] ?? ''],
      email:                 ['', Validators.email],
      necesidadesEspecificas:[data?.necesidadesEspecificas ?? ''],
    });
  }

  cerrar(): void { this.dialogRef.close(null); }

  guardar(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    const v = this.form.value;
    const pad = (n: number) => String(n).padStart(2, '0');
    const fechaStr = `${v.anioFecha}-${pad(v.mesFecha)}-${pad(v.diaFecha)}`;

    this.dialogRef.close({
      fotoUrl:               v.fotoUrl               || null,
      nombres:               v.nombres,
      apellidos:             v.apellidos,
      fechaNacimiento:       fechaStr,
      parentesco:            v.parentesco,
      telefono:              v.telefono               || null,
      email:                 v.email                  || null,
      necesidadesEspecificas: v.necesidadesEspecificas || null,
    });
  }
}

/* ══════════════════════════════════════════════════════
   DIÁLOGO — confirmar eliminación
══════════════════════════════════════════════════════ */
@Component({
  selector: 'app-confirmar-eliminar-dialog',
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatIconModule, MatDialogModule],
  template: `
    <div class="confirm-wrap">
      <div class="confirm-icono">
        <mat-icon>warning_amber</mat-icon>
      </div>
      <h2 class="confirm-titulo">¿Eliminar familiar?</h2>
      <p class="confirm-desc">
        Estás a punto de eliminar a <strong>{{ data.nombre }}</strong> de tu lista de familiares.
        Esta acción no se puede deshacer.
      </p>
      <div class="confirm-acciones">
        <button mat-stroked-button (click)="dialogRef.close(false)">Cancelar</button>
        <button mat-raised-button class="btn-eliminar" (click)="dialogRef.close(true)">
          <mat-icon>delete</mat-icon> Eliminar
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
    .btn-eliminar { background: #ef4444 !important; color: #fff !important; border-radius: 8px !important; }
  `],
})
export class ConfirmarEliminarDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<ConfirmarEliminarDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { nombre: string },
  ) {}
}
