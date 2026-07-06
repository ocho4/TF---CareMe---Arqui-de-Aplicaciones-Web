import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatChipsModule } from '@angular/material/chips';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { RecordatorioService } from '../../services/recordatorio.service';
import { FamiliarService } from '../../services/familiar.service';
import { SesionService } from '../../services/sesion.service';
import { RecordatorioMedicacion } from '../../model/recordatorio.model';
import { Paciente } from '../../model/paciente.model';

@Component({
  selector: 'app-medicacion',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatDividerModule,
    MatChipsModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatSnackBarModule,
  ],
  templateUrl: './medicacion.component.html',
  styleUrl: './medicacion.component.css',
})
export class MedicacionComponent implements OnInit {
  private recordatorioService = inject(RecordatorioService);
  private familiarService     = inject(FamiliarService);
  private sesionService       = inject(SesionService);
  private snackBar            = inject(MatSnackBar);
  private cdr                 = inject(ChangeDetectorRef);
  private fb                  = inject(FormBuilder);

  form!: FormGroup;
  pacientes:     Paciente[] = [];
  recordatorios: RecordatorioMedicacion[] = [];

  guardando      = false;
  cargandoPacientes = false;
  cargandoRecordatorios = false;

  horas    = Array.from({ length: 12 }, (_, i) => i + 1);
  minutos  = [0, 15, 30, 45];
  periodos = ['AM', 'PM'];

  ngOnInit(): void {
    this.form = this.fb.group({
      idPaciente:        [null, Validators.required],
      nombreMedicamento: ['',   [Validators.required, Validators.minLength(2)]],
      fecha:             [null, Validators.required],
      horaH:             [null, Validators.required],
      horaM:             [0,    Validators.required],
      horaP:             ['AM', Validators.required],
    });

    const idUsuario = this.sesionService.obtener()?.idUsuario;
    if (idUsuario) {
      this.cargandoPacientes = true;
      this.familiarService.obtenerPerfilPorUsuario(idUsuario).subscribe({
        next: (familiar) => {
          this.familiarService.listarPacientes(familiar.idFamiliar).subscribe({
            next: (lista) => {
              this.pacientes         = lista;
              this.cargandoPacientes = false;
              this.cdr.detectChanges();
            },
            error: () => { this.cargandoPacientes = false; this.cdr.detectChanges(); },
          });
        },
        error: () => { this.cargandoPacientes = false; this.cdr.detectChanges(); },
      });
    }
  }

  cargarRecordatorios(): void {
    const idPaciente = this.form.get('idPaciente')?.value;
    if (!idPaciente) return;

    this.cargandoRecordatorios = true;
    this.recordatorioService.pendientesPorPaciente(idPaciente).subscribe({
      next: (lista) => {
        this.recordatorios         = lista;
        this.cargandoRecordatorios = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.cargandoRecordatorios = false;
        this.cdr.detectChanges();
        this.snackBar.open('Error al cargar recordatorios', 'Cerrar', { duration: 3000 });
      },
    });
  }

  programar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const v = this.form.value;
    this.guardando = true;

    this.recordatorioService.programar({
      idPaciente:        v.idPaciente,
      nombreMedicamento: v.nombreMedicamento,
      horaProgramada:    this.construirFechaHora(v.fecha, v.horaH, v.horaM, v.horaP),
    }).subscribe({
      next: () => {
        this.guardando = false;
        this.snackBar.open('Recordatorio programado correctamente', 'Cerrar', { duration: 3000 });
        this.form.patchValue({ nombreMedicamento: '', fecha: null, horaH: null, horaM: 0, horaP: 'AM' });
        this.cargarRecordatorios();
      },
      error: (err) => {
        this.guardando = false;
        this.cdr.detectChanges();
        const msg = err.error?.mensaje ?? 'Error al programar el recordatorio';
        this.snackBar.open(msg, 'Cerrar', { duration: 4000 });
      },
    });
  }

  marcarTomado(id: number): void {
    this.recordatorioService.marcarTomado(id).subscribe({
      next: () => {
        this.snackBar.open('Medicación registrada como tomada ✓', 'Cerrar', { duration: 3000 });
        this.cargarRecordatorios();
      },
      error: () => this.snackBar.open('Error al registrar la toma', 'Cerrar', { duration: 3000 }),
    });
  }

  pad(n: number): string { return String(n).padStart(2, '0'); }

  private a24h(h: number, p: string): number {
    if (p === 'AM') return h === 12 ? 0 : h;
    return h === 12 ? 12 : h + 12;
  }

  private construirFechaHora(fecha: Date, h: number, m: number, p: string): string {
    const d   = new Date(fecha);
    const h24 = this.a24h(h, p);
    const pad = (n: number) => String(n).padStart(2, '0');
    return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(h24)}:${pad(m)}:00`;
  }
}
