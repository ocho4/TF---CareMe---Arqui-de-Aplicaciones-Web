import { ChangeDetectorRef, Component, OnInit, OnDestroy, ViewChild, ElementRef, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDividerModule } from '@angular/material/divider';
import { Subscription, interval } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { MensajeService } from '../../services/mensaje.service';
import { SesionService } from '../../services/sesion.service';
import { ServicioService } from '../../services/servicio.service';
import { Mensaje } from '../../model/mensaje.model';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatSnackBarModule,
    MatDividerModule,
  ],
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.css',
})
export class ChatComponent implements OnInit, OnDestroy {
  @ViewChild('mensajesContainer') contenedor!: ElementRef<HTMLDivElement>;

  private route           = inject(ActivatedRoute);
  private mensajeService  = inject(MensajeService);
  private sesionService   = inject(SesionService);
  private servicioService = inject(ServicioService);
  private snackBar        = inject(MatSnackBar);
  private cdr             = inject(ChangeDetectorRef);

  idServicio   = 0;
  mensajes: Mensaje[] = [];
  enviando     = false;
  cargando     = true;

  otroNombre   = '';
  otroFotoUrl: string | null = null;

  inputMensaje = new FormControl('', [Validators.required, Validators.minLength(1)]);

  private pollSub?: Subscription;

  get usuarioActual() { return this.sesionService.obtener(); }

  ngOnInit(): void {
    this.idServicio = Number(this.route.snapshot.paramMap.get('idServicio'));
    this.cargarParticipante();
    this.cargarHistorial();
    this.iniciarPolling();
    const idUsuario = this.sesionService.obtener()?.idUsuario;
    if (idUsuario) {
      this.mensajeService.marcarLeidos(this.idServicio, idUsuario).subscribe();
    }
  }

  private cargarParticipante(): void {
    this.servicioService.obtenerPorId(this.idServicio).subscribe({
      next: (s) => {
        const yo = this.usuarioActual;
        const u = yo?.rol === 'cuidador'
          ? s.familiar?.usuario
          : s.cuidador?.usuario;
        this.otroNombre  = `${u?.nombres ?? ''} ${u?.apellidos ?? ''}`.trim();
        this.otroFotoUrl = u?.fotoUrl ?? null;
        this.cdr.markForCheck();
      },
    });
  }

  ngOnDestroy(): void {
    this.pollSub?.unsubscribe();
  }

  private cargarHistorial(): void {
    this.mensajeService.historial(this.idServicio).subscribe({
      next: (lista) => {
        this.mensajes = lista;
        this.cargando = false;
        this.cdr.detectChanges();
        this.scrollAbajo();
      },
      error: () => {
        this.cargando = false;
        this.cdr.detectChanges();
      },
    });
  }

  private iniciarPolling(): void {
    this.pollSub = interval(4000)
      .pipe(switchMap(() => this.mensajeService.historial(this.idServicio)))
      .subscribe({
        next: (lista) => {
          const hayCambios = lista.length !== this.mensajes.length;
          this.mensajes = lista;
          this.cdr.detectChanges();
          if (hayCambios) this.scrollAbajo();
        },
      });
  }

  enviar(): void {
    const texto = this.inputMensaje.value?.trim();
    if (!texto || !this.usuarioActual) return;

    this.enviando = true;
    this.mensajeService.enviar({
      idServicio:  this.idServicio,
      idRemitente: this.usuarioActual.idUsuario!,
      contenido:   texto,
    }).subscribe({
      next: () => {
        this.inputMensaje.reset();
        this.enviando = false;
        this.cargarHistorial();
      },
      error: (err) => {
        this.enviando = false;
        const msg = err.error?.mensaje ?? 'Error al enviar el mensaje';
        this.snackBar.open(msg, 'Cerrar', { duration: 3000 });
      },
    });
  }

  esMio(mensaje: Mensaje): boolean {
    return mensaje.remitente?.idUsuario === this.usuarioActual?.idUsuario;
  }

  private scrollAbajo(): void {
    setTimeout(() => {
      if (this.contenedor) {
        this.contenedor.nativeElement.scrollTop = this.contenedor.nativeElement.scrollHeight;
      }
    }, 50);
  }
}
