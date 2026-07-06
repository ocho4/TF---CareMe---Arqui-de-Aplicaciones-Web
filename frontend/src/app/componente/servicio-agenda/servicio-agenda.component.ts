import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { RouterLink } from '@angular/router';
import { ServicioService } from '../../services/servicio.service';
import { SesionService } from '../../services/sesion.service';
import { Servicio } from '../../model/servicio.model';

interface DiaAgenda {
  fecha: Date;
  etiqueta: string;
  servicios: Servicio[];
}

@Component({
  selector: 'app-servicio-agenda',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    MatFormFieldModule,
    MatInputModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatButtonModule,
    MatIconModule,
    MatTooltipModule,
    MatSnackBarModule,
  ],
  templateUrl: './servicio-agenda.component.html',
  styleUrl: './servicio-agenda.component.css',
})
export class ServicioAgendaComponent implements OnInit {
  private servicioService = inject(ServicioService);
  private sesionService   = inject(SesionService);
  private snackBar        = inject(MatSnackBar);
  private cdr             = inject(ChangeDetectorRef);
  private fb              = inject(FormBuilder);

  form: FormGroup;
  dias: DiaAgenda[] = [];
  cargando  = false;
  consultado = false;

  get usuario() { return this.sesionService.obtener(); }

  constructor() {
    const hoy    = new Date();
    const fin    = new Date(hoy);
    fin.setDate(fin.getDate() + 6);

    this.form = this.fb.group({
      fechaInicio: [hoy,  Validators.required],
      fechaFin:    [fin,  Validators.required],
    });
  }

  ngOnInit(): void {
    this.consultar();
  }

  consultar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const idUsuario = this.usuario?.idUsuario;
    if (!idUsuario) {
      this.snackBar.open('Debes iniciar sesión', 'Cerrar', { duration: 3000 });
      return;
    }

    this.cargando   = true;
    this.consultado = false;

    const fi = this.formatearFecha(this.form.value.fechaInicio);
    const ff = this.formatearFecha(this.form.value.fechaFin);

    this.servicioService.agenda(idUsuario, fi, ff).subscribe({
      next: (lista) => {
        this.dias     = this.agruparPorDia(lista, this.form.value.fechaInicio, this.form.value.fechaFin);
        this.cargando  = false;
        this.consultado = true;
        this.cdr.detectChanges();
      },
      error: () => {
        this.cargando = false;
        this.cdr.detectChanges();
        this.snackBar.open('Error al cargar la agenda', 'Cerrar', { duration: 3000 });
      },
    });
  }

  private formatearFecha(d: Date): string {
    const pad = (n: number) => String(n).padStart(2, '0');
    return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`;
  }

  private agruparPorDia(servicios: Servicio[], desde: Date, hasta: Date): DiaAgenda[] {
    const dias: DiaAgenda[] = [];
    const cursor = new Date(desde);
    cursor.setHours(0, 0, 0, 0);
    const fin = new Date(hasta);
    fin.setHours(23, 59, 59, 999);

    const diasSemana = ['Dom', 'Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb'];
    const meses = ['ene', 'feb', 'mar', 'abr', 'may', 'jun', 'jul', 'ago', 'sep', 'oct', 'nov', 'dic'];

    while (cursor <= fin) {
      const fechaDia = new Date(cursor);
      const serviciosDelDia = servicios.filter(s => {
        if (!s.fechaInicio) return false;
        const fi = new Date(s.fechaInicio);
        return fi.getFullYear() === fechaDia.getFullYear()
          && fi.getMonth()      === fechaDia.getMonth()
          && fi.getDate()       === fechaDia.getDate();
      });

      dias.push({
        fecha:    fechaDia,
        etiqueta: `${diasSemana[fechaDia.getDay()]} ${fechaDia.getDate()} ${meses[fechaDia.getMonth()]}`,
        servicios: serviciosDelDia,
      });

      cursor.setDate(cursor.getDate() + 1);
    }
    return dias;
  }

  esHoy(fecha: Date): boolean {
    const hoy = new Date();
    return fecha.getFullYear() === hoy.getFullYear()
      && fecha.getMonth()      === hoy.getMonth()
      && fecha.getDate()       === hoy.getDate();
  }
}
