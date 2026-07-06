import {
  ChangeDetectorRef, Component, OnDestroy, OnInit, inject,
} from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatChipsModule } from '@angular/material/chips';
import { MatDividerModule } from '@angular/material/divider';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { SesionService } from '../../services/sesion.service';
import { ServicioService } from '../../services/servicio.service';
import { TareaService } from '../../services/tarea.service';
import { Servicio } from '../../model/servicio.model';
import { Tarea } from '../../model/tarea.model';
import { Usuario } from '../../model/usuario.model';

@Component({
  selector: 'app-seguimiento',
  standalone: true,
  imports: [
    CommonModule,
    DatePipe,
    FormsModule,
    MatButtonModule,
    MatIconModule,
    MatInputModule,
    MatFormFieldModule,
    MatCheckboxModule,
    MatChipsModule,
    MatDividerModule,
    MatSnackBarModule,
    MatProgressSpinnerModule,
    MatTooltipModule,
  ],
  templateUrl: './seguimiento.component.html',
  styleUrl: './seguimiento.component.css',
})
export class SeguimientoComponent implements OnInit, OnDestroy {
  private route           = inject(ActivatedRoute);
  private router          = inject(Router);
  private sesionService   = inject(SesionService);
  private servicioService = inject(ServicioService);
  private tareaService    = inject(TareaService);
  private snackBar        = inject(MatSnackBar);
  private cdr             = inject(ChangeDetectorRef);

  usuario: Usuario | null = null;
  esFamiliar = false;

  idServicio = 0;
  servicio: Servicio | null = null;
  tareas: Tarea[] = [];

  nuevaDescripcion = '';
  cargando = true;
  cargandoTareas = false;

  private pollId?: ReturnType<typeof setInterval>;

  ngOnInit(): void {
    this.usuario   = this.sesionService.obtener();
    this.esFamiliar = this.usuario?.rol === 'familiar';
    this.idServicio = Number(this.route.snapshot.paramMap.get('idServicio'));
    if (!this.idServicio) { this.router.navigate(['/mis-servicios']); return; }
    this.cargarServicio();
  }

  ngOnDestroy(): void {
    if (this.pollId) clearInterval(this.pollId);
  }

  cargarServicio(): void {
    this.servicioService.listar().subscribe({
      next: (lista) => {
        this.servicio = lista.find(s => s.idServicio === this.idServicio) ?? null;
        this.cargando = false;
        this.cdr.markForCheck();
        if (this.servicio) {
          this.cargarTareas();
          this.marcarVisto();
          this.pollId = setInterval(() => this.cargarTareas(), 30_000);
        }
      },
      error: () => { this.cargando = false; this.cdr.markForCheck(); },
    });
  }

  private marcarVisto(): void {
    if (this.esFamiliar) {
      this.tareaService.marcarVistoFamiliar(this.idServicio).subscribe();
    } else {
      this.tareaService.marcarVistoCuidador(this.idServicio).subscribe();
    }
  }

  cargarTareas(): void {
    this.cargandoTareas = true;
    this.tareaService.listarPorServicio(this.idServicio).subscribe({
      next: (tareas) => {
        this.tareas = tareas;
        this.cargandoTareas = false;
        this.cdr.markForCheck();
      },
      error: () => { this.cargandoTareas = false; this.cdr.markForCheck(); },
    });
  }

  agregarTarea(): void {
    const desc = this.nuevaDescripcion.trim();
    if (!desc) return;
    this.tareaService.crear({
      idServicio: this.idServicio,
      descripcion: desc,
      creadoPor: this.usuario?.idUsuario,
    }).subscribe({
      next: (tarea) => {
        this.tareas = [...this.tareas, tarea];
        this.nuevaDescripcion = '';
        this.cdr.markForCheck();
      },
      error: () => this.snackBar.open('Error al añadir la tarea', 'Cerrar', { duration: 3000 }),
    });
  }

  toggleTarea(tarea: Tarea): void {
    const op = tarea.completada
      ? this.tareaService.descompletar(tarea.idTarea!)
      : this.tareaService.completar(tarea.idTarea!);
    op.subscribe({
      next: (updated) => {
        this.tareas = this.tareas.map(t => t.idTarea === updated.idTarea ? updated : t);
        this.cdr.markForCheck();
      },
      error: () => this.snackBar.open('Error al actualizar la tarea', 'Cerrar', { duration: 3000 }),
    });
  }

  eliminarTarea(id: number): void {
    this.tareaService.eliminar(id).subscribe({
      next: () => {
        this.tareas = this.tareas.filter(t => t.idTarea !== id);
        this.cdr.markForCheck();
      },
      error: () => this.snackBar.open('Error al eliminar la tarea', 'Cerrar', { duration: 3000 }),
    });
  }

  volver(): void { this.router.navigate(['/mis-servicios']); }

  nombreCuidador(): string {
    const c = (this.servicio?.cuidador as any)?.usuario;
    return c ? `${c.nombres ?? ''} ${c.apellidos ?? ''}`.trim() : '—';
  }

  nombrePaciente(): string {
    const p = (this.servicio?.paciente as any)?.usuario;
    return p ? `${p.nombres ?? ''} ${p.apellidos ?? ''}`.trim() : '—';
  }

  get pendientes(): Tarea[] { return this.tareas.filter(t => !t.completada); }
  get completadas(): Tarea[] { return this.tareas.filter(t => t.completada); }
}
