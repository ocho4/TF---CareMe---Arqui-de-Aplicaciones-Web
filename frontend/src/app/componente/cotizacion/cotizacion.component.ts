import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ServicioService, CotizacionResponse } from '../../services/servicio.service';
import { CuidadorService } from '../../services/cuidador.service';
import { Cuidador } from '../../model/cuidador.model';

@Component({
  selector: 'app-cotizacion',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatCheckboxModule,
    MatButtonModule,
    MatIconModule,
    MatDividerModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatSnackBarModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './cotizacion.component.html',
  styleUrl: './cotizacion.component.css',
})
export class CotizacionComponent implements OnInit {
  private servicioService = inject(ServicioService);
  private cuidadorService = inject(CuidadorService);
  private snackBar        = inject(MatSnackBar);
  private cdr             = inject(ChangeDetectorRef);
  private fb              = inject(FormBuilder);
  private router          = inject(Router);

  form!: FormGroup;
  cuidadores: Cuidador[]       = [];
  cuidadorSeleccionado: Cuidador | null = null;
  resultado?: CotizacionResponse;
  cargando   = true;
  calculando = false;
  hoy        = new Date();

  horas    = Array.from({ length: 12 }, (_, i) => i + 1);
  minutos  = [0, 15, 30, 45];
  periodos = ['AM', 'PM'];

  ngOnInit(): void {
    this.form = this.fb.group({
      fechaInicio:           [null, Validators.required],
      horaInicioH:           [null, Validators.required],
      horaInicioM:           [0],
      horaInicioP:           ['AM'],
      fechaFin:              [null, Validators.required],
      horaFinH:              [null, Validators.required],
      horaFinM:              [0],
      horaFinP:              ['AM'],
      especialidadRequerida: [''],
      esHorarioNocturno:     [false],
      esFinDeSemana:         [false],
    });

    this.cuidadorService.listar().subscribe({
      next: (lista) => { this.cuidadores = lista; this.cargando = false; this.cdr.detectChanges(); },
      error: () => {
        this.cargando = false;
        this.snackBar.open('Error al cargar cuidadores', 'Cerrar', { duration: 3000 });
      },
    });
  }

  seleccionar(c: Cuidador): void {
    this.cuidadorSeleccionado = c;
    this.resultado = undefined;
    setTimeout(() => {
      document.getElementById('seccion-form')?.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }, 50);
  }

  estrellas(cal: number): number[] {
    return Array.from({ length: Math.round(cal) }, (_, i) => i);
  }

  cotizar(): void {
    if (this.form.invalid || !this.cuidadorSeleccionado) {
      this.form.markAllAsTouched();
      if (!this.cuidadorSeleccionado) {
        this.snackBar.open('Primero selecciona un cuidador', 'Cerrar', { duration: 3000 });
      }
      return;
    }

    this.calculando = true;
    this.resultado  = undefined;
    const v = this.form.value;

    this.servicioService.cotizar({
      idCuidador:            this.cuidadorSeleccionado.idCuidador,
      fechaInicio:           this.combinar(v.fechaInicio, v.horaInicioH, v.horaInicioM, v.horaInicioP),
      fechaFin:              this.combinar(v.fechaFin,   v.horaFinH,    v.horaFinM,    v.horaFinP),
      especialidadRequerida: v.especialidadRequerida?.trim() || undefined,
      esHorarioNocturno:     v.esHorarioNocturno,
      esFinDeSemana:         v.esFinDeSemana,
    }).subscribe({
      next: (res) => {
        this.resultado  = res;
        this.calculando = false;
        this.cdr.detectChanges();
        setTimeout(() => {
          document.getElementById('resultado')?.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }, 50);
      },
      error: (err) => {
        this.calculando = false;
        this.cdr.detectChanges();
        this.snackBar.open(err.error?.mensaje ?? 'Error al calcular la cotización', 'Cerrar', { duration: 4000 });
      },
    });
  }

  solicitarServicio(): void {
    if (this.cuidadorSeleccionado) {
      this.router.navigate(['/solicitar', this.cuidadorSeleccionado.idCuidador]);
    }
  }

  pad(n: number): string { return String(n).padStart(2, '0'); }

  private a24h(h: number, p: string): number {
    if (p === 'AM') return h === 12 ? 0 : h;
    return h === 12 ? 12 : h + 12;
  }

  private combinar(fecha: Date, h: number, m: number, p: string): string {
    const d   = new Date(fecha);
    const h24 = this.a24h(h, p);
    const pad = (n: number) => String(n).padStart(2, '0');
    return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(h24)}:${pad(m)}:00`;
  }
}
