import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDividerModule } from '@angular/material/divider';
import { CuidadorService } from '../../services/cuidador.service';
import { ServicioService } from '../../services/servicio.service';
import { FamiliarService } from '../../services/familiar.service';
import { SesionService } from '../../services/sesion.service';
import { Cuidador } from '../../model/cuidador.model';
import { Paciente } from '../../model/paciente.model';
import { Servicio } from '../../model/servicio.model';

@Component({
  selector: 'app-servicio-solicitar',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatCardModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatSnackBarModule,
    MatIconModule,
    MatChipsModule,
    MatProgressSpinnerModule,
    MatDividerModule,
  ],
  templateUrl: './servicio-solicitar.component.html',
  styleUrl: './servicio-solicitar.component.css',
})
export class ServicioSolicitarComponent implements OnInit {
  private fb              = inject(FormBuilder);
  private route           = inject(ActivatedRoute);
  private router          = inject(Router);
  private cuidadorService = inject(CuidadorService);
  private servicioService = inject(ServicioService);
  private familiarService = inject(FamiliarService);
  private sesionService   = inject(SesionService);
  private snackBar        = inject(MatSnackBar);
  private cdr             = inject(ChangeDetectorRef);

  form!: FormGroup;
  cuidador: Cuidador | null = null;
  pacientes: Paciente[] = [];
  idFamiliar = 0;
  cargando = false;
  enviando = false;

  horas    = Array.from({ length: 12 }, (_, i) => i + 1);
  minutos  = Array.from({ length: 60 }, (_, i) => i);
  periodos = ['AM', 'PM'];
  hoy      = new Date();

  ngOnInit(): void {
    this.form = this.fb.group({
      idPaciente:    [null,     Validators.required],
      fechaInicio:   [null,     Validators.required],
      horaInicioH:   [null,     Validators.required],
      horaInicioM:   [0,        Validators.required],
      horaInicioP:   ['AM',     Validators.required],
      fechaFin:      [null,     Validators.required],
      horaFinH:      [null,     Validators.required],
      horaFinM:      [0,        Validators.required],
      horaFinP:      ['AM',     Validators.required],
      tipoServicio:  ['basico', Validators.required],
    });

    const idCuidador = Number(this.route.snapshot.paramMap.get('idCuidador'));
    if (!idCuidador) { this.router.navigate(['/buscar']); return; }

    this.cargando = true;
    this.cuidadorService.buscarPorId(idCuidador).subscribe({
      next: (c) => { this.cuidador = c; this.cargando = false; this.cdr.detectChanges(); },
      error: () => { this.router.navigate(['/buscar']); },
    });

    const idUsuario = this.sesionService.obtener()?.idUsuario;
    if (!idUsuario) return;

    this.familiarService.obtenerPerfilPorUsuario(idUsuario).subscribe({
      next: (familiar) => {
        this.idFamiliar = familiar.idFamiliar;
        this.familiarService.listarPacientes(familiar.idFamiliar).subscribe({
          next: (lista) => { this.pacientes = lista; this.cdr.detectChanges(); },
          error: () => this.snackBar.open('Error al cargar pacientes', 'Cerrar', { duration: 3000 }),
        });
      },
      error: () => this.snackBar.open('No se encontró tu perfil de familiar', 'Cerrar', { duration: 3000 }),
    });
  }

  pad(n: number): string { return String(n).padStart(2, '0'); }

  estrellas(cal: number): number[] {
    return Array.from({ length: Math.round(cal) }, (_, i) => i);
  }

  private a24h(h: number, periodo: string): number {
    if (periodo === 'AM') return h === 12 ? 0 : h;
    return h === 12 ? 12 : h + 12;
  }

  private combinarFechaHora(fecha: Date, h: number, m: number, periodo: string): string {
    const d   = new Date(fecha);
    const h24 = this.a24h(h, periodo);
    const pad = (n: number) => String(n).padStart(2, '0');
    return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(h24)}:${pad(m)}:00`;
  }

  enviar(): void {
    if (this.form.invalid || !this.idFamiliar || !this.cuidador) {
      this.form.markAllAsTouched();
      this.snackBar.open('Completa todos los campos requeridos', 'Cerrar', { duration: 3000 });
      return;
    }

    this.enviando = true;
    const v = this.form.value;
    const servicio: Servicio = {
      familiar:          { idFamiliar: this.idFamiliar },
      cuidador:          { idCuidador: this.cuidador.idCuidador },
      paciente:          { idPaciente: v.idPaciente },
      fechaInicio:       this.combinarFechaHora(v.fechaInicio, v.horaInicioH, v.horaInicioM, v.horaInicioP),
      fechaFin:          this.combinarFechaHora(v.fechaFin,   v.horaFinH,    v.horaFinM,    v.horaFinP),
      tipoServicio:      v.tipoServicio,
      recargoHorario:    0,
      descuentoAplicado: 0,
    };

    this.servicioService.insertar(servicio).subscribe({
      next: (resp) => {
        this.enviando = false;
        this.snackBar.open(
          `¡Solicitud enviada! Servicio #${resp.idServicio} — Costo estimado: S/ ${resp.costoTotal?.toFixed(2)}`,
          'Cerrar',
          { duration: 5000 }
        );
        this.router.navigate(['/pendientes']);
      },
      error: (err) => {
        this.enviando = false;
        const msg = err.error?.mensaje ?? 'Error al solicitar el servicio';
        this.snackBar.open(msg, 'Cerrar', { duration: 4000 });
      },
    });
  }
}
