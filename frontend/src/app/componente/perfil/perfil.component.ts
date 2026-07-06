import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { SesionService } from '../../services/sesion.service';
import {
  PerfilService,
  PerfilCuidadorRequest,
  PerfilFamiliarRequest,
  PerfilPacienteRequest,
} from '../../services/perfil.service';
import { Cuidador } from '../../model/cuidador.model';
import { Usuario } from '../../model/usuario.model';

@Component({
  selector: 'app-perfil',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatCardModule,
    MatIconModule,
    MatDividerModule,
    MatSnackBarModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './perfil.component.html',
  styleUrl: './perfil.component.css',
})
export class PerfilComponent implements OnInit {

  usuario: Usuario | null = null;
  cargando = true;
  guardando = false;

  idEntidad: number | null = null;
  fotoActual: string | null = null;

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

  readonly diasSemana = ['Lunes','Martes','Miércoles','Jueves','Viernes','Sábado','Domingo'];

  form!: FormGroup;

  private cdr = inject(ChangeDetectorRef);

  constructor(
    private fb: FormBuilder,
    private sesionService: SesionService,
    private perfilService: PerfilService,
    private snackBar: MatSnackBar,
  ) {}

  ngOnInit(): void {
    this.usuario = this.sesionService.obtener();
    if (!this.usuario?.idUsuario) {
      this.cargando = false;
      return;
    }

    this.initForm();
    this.cargarDatos();
  }

  private initForm(): void {
    const rol = this.usuario?.rol;
    if (rol === 'cuidador') {
      this.form = this.fb.group({
        fotoUrl:         [''],
        ubicacion:       [''],
        especialidad:    [''],
        diasDisponibles: [[] as string[]],
        tarifaBase:      [null as number | null],
      });
    } else if (rol === 'familiar') {
      this.form = this.fb.group({
        fotoUrl:   [''],
        direccion: [''],
        distrito:  [''],
      });
    } else {
      this.form = this.fb.group({
        fotoUrl:               [''],
        necesidadesEspecificas: [''],
      });
    }
  }

  private cargarDatos(): void {
    const idUsuario = this.usuario!.idUsuario!;
    const rol = this.usuario?.rol;

    if (rol === 'cuidador') {
      this.perfilService.getCuidadorPorUsuario(idUsuario).subscribe({
        next: (c: Cuidador) => {
          this.idEntidad = c.idCuidador;
          this.fotoActual = c.usuario?.fotoUrl ?? null;
          this.form.patchValue({
            fotoUrl:         c.usuario?.fotoUrl ?? '',
            ubicacion:       c.ubicacion        ?? '',
            especialidad:    c.especialidad      ?? '',
            diasDisponibles: this.parsearDias(c.disponibilidadTexto ?? ''),
            tarifaBase:      c.tarifaBase        ?? null,
          });
          this.cargando = false;
          this.cdr.markForCheck();
        },
        error: () => { this.cargando = false; this.cdr.markForCheck(); },
      });

    } else if (rol === 'familiar') {
      this.perfilService.getFamiliarPorUsuario(idUsuario).subscribe({
        next: (f) => {
          this.idEntidad = f.idFamiliar ?? null;
          this.fotoActual = f.usuario?.fotoUrl ?? null;
          this.form.patchValue({
            fotoUrl:   this.usuario?.fotoUrl ?? '',
            direccion: f.direccion ?? '',
            distrito:  f.distrito  ?? '',
          });
          this.cargando = false;
          this.cdr.markForCheck();
        },
        error: () => { this.cargando = false; this.cdr.markForCheck(); },
      });

    } else {
      this.perfilService.getPacientePorUsuario(idUsuario).subscribe({
        next: (p) => {
          this.idEntidad = p.idPaciente ?? null;
          this.fotoActual = p.usuario?.fotoUrl ?? null;
          this.form.patchValue({
            fotoUrl:               this.usuario?.fotoUrl ?? '',
            necesidadesEspecificas: p.necesidadesEspecificas ?? '',
          });
          this.cargando = false;
          this.cdr.markForCheck();
        },
        error: () => { this.cargando = false; this.cdr.markForCheck(); },
      });
    }
  }

  get previewUrl(): string {
    return this.form.value.fotoUrl?.trim() || '';
  }

  guardar(): void {
    if (!this.idEntidad) return;
    this.guardando = true;

    const rol = this.usuario?.rol;
    const v = this.form.value;

    if (rol === 'cuidador') {
      const req: PerfilCuidadorRequest = {
        fotoUrl:             v.fotoUrl      || undefined,
        ubicacion:           v.ubicacion    || undefined,
        especialidad:        v.especialidad || undefined,
        disponibilidadTexto: v.diasDisponibles?.length
                               ? v.diasDisponibles.join(', ')
                               : undefined,
        tarifaBase:          v.tarifaBase ?? undefined,
      };
      this.perfilService.actualizarCuidador(this.idEntidad, req).subscribe({
        next: (c: Cuidador) => {
          this.fotoActual = c.usuario?.fotoUrl ?? null;
          this.actualizarSesion(c.usuario?.fotoUrl);
          this.onGuardado();
        },
        error: () => this.onError(),
      });

    } else if (rol === 'familiar') {
      const req: PerfilFamiliarRequest = {
        fotoUrl:   v.fotoUrl   || undefined,
        direccion: v.direccion || undefined,
        distrito:  v.distrito  || undefined,
      };
      this.perfilService.actualizarFamiliar(this.idEntidad, req).subscribe({
        next: () => {
          this.fotoActual = v.fotoUrl || null;
          this.actualizarSesion(v.fotoUrl);
          this.onGuardado();
        },
        error: () => this.onError(),
      });

    } else {
      const req: PerfilPacienteRequest = {
        fotoUrl:               v.fotoUrl               || undefined,
        necesidadesEspecificas: v.necesidadesEspecificas || undefined,
      };
      this.perfilService.actualizarPaciente(this.idEntidad, req).subscribe({
        next: () => {
          this.fotoActual = v.fotoUrl || null;
          this.actualizarSesion(v.fotoUrl);
          this.onGuardado();
        },
        error: () => this.onError(),
      });
    }
  }

  private actualizarSesion(fotoUrl: string | undefined): void {
    if (this.usuario) {
      const actualizado = { ...this.usuario, fotoUrl };
      this.sesionService.guardar(actualizado as any);
      this.usuario = this.sesionService.obtener();
    }
  }

  private parsearDias(texto: string): string[] {
    if (!texto?.trim()) return [];
    const mapa: Record<string, string> = {
      'lunes':'Lunes', 'martes':'Martes',
      'miercoles':'Miércoles', 'miércoles':'Miércoles',
      'jueves':'Jueves', 'viernes':'Viernes',
      'sabado':'Sábado', 'sábado':'Sábado', 'domingo':'Domingo',
    };
    return texto.split(/[\s,]+/)
      .map(d => mapa[d.trim().toLowerCase()])
      .filter((d): d is string => !!d);
  }

  private onGuardado(): void {
    this.guardando = false;
    this.snackBar.open('Perfil actualizado correctamente', 'Cerrar', { duration: 3000 });
    this.cdr.markForCheck();
  }

  private onError(): void {
    this.guardando = false;
    this.snackBar.open('Error al guardar el perfil', 'Cerrar', { duration: 4000 });
    this.cdr.markForCheck();
  }
}
