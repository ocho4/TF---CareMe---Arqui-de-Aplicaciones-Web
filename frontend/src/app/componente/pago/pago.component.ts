import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { PagoService } from '../../services/pago.service';
import { ServicioService } from '../../services/servicio.service';
import { Servicio } from '../../model/servicio.model';

@Component({
  selector: 'app-pago',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatDividerModule,
    MatSnackBarModule,
  ],
  templateUrl: './pago.component.html',
  styleUrl: './pago.component.css',
})
export class PagoComponent implements OnInit {
  private route          = inject(ActivatedRoute);
  private router         = inject(Router);
  private pagoService    = inject(PagoService);
  private servicioService = inject(ServicioService);
  private snackBar       = inject(MatSnackBar);
  private cdr            = inject(ChangeDetectorRef);
  private fb             = inject(FormBuilder);

  idServicio  = 0;
  servicio?: Servicio;
  metodo: 'yape' | 'tarjeta' = 'yape';
  procesando  = false;
  cargando    = true;

  formYape!:    FormGroup;
  formTarjeta!: FormGroup;

  ngOnInit(): void {
    this.idServicio = Number(this.route.snapshot.paramMap.get('idServicio'));

    this.formYape = this.fb.group({
      telefono: ['', [Validators.required, Validators.pattern(/^\d{9}$/)]],
    });

    this.formTarjeta = this.fb.group({
      nombreTitular: ['', Validators.required],
      numeroTarjeta: ['', [Validators.required, Validators.pattern(/^\d{16}$/)]],
      vencimiento:   ['', [Validators.required, Validators.pattern(/^\d{2}\/\d{2}$/)]],
      cvv:           ['', [Validators.required, Validators.pattern(/^\d{3,4}$/)]],
    });

    this.servicioService.listar().subscribe({
      next: (lista) => {
        this.servicio = lista.find(s => s.idServicio === this.idServicio);
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.cargando = false;
        this.cdr.detectChanges();
      },
    });
  }

  seleccionarMetodo(m: 'yape' | 'tarjeta'): void {
    this.metodo = m;
  }

  pagar(): void {
    const form = this.metodo === 'yape' ? this.formYape : this.formTarjeta;
    if (form.invalid) {
      form.markAllAsTouched();
      return;
    }

    this.procesando = true;
    const ref = `REF-${Date.now()}`;

    const req = {
      idServicio:       this.idServicio,
      metodoPago:       this.metodo,
      datosTransaccion: this.metodo === 'tarjeta'
        ? `CARD-${this.formTarjeta.value.numeroTarjeta.slice(-4)}-${ref}`
        : `YAPE-${this.formYape.value.telefono}-${ref}`,
      telefonoYape: this.metodo === 'yape' ? this.formYape.value.telefono : undefined,
    };

    this.pagoService.procesar(req).subscribe({
      next: (msg) => {
        this.procesando = false;
        this.snackBar.open(msg, 'Cerrar', { duration: 6000 });
        this.router.navigate(['/mis-servicios']);
      },
      error: (err) => {
        this.procesando = false;
        this.cdr.detectChanges();
        const msg = err.error?.mensaje ?? 'Error al procesar el pago';
        this.snackBar.open(msg, 'Cerrar', { duration: 4000 });
      },
    });
  }
}
