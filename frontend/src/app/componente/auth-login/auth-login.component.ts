import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AuthService } from '../../services/auth.service';
import { SesionService } from '../../services/sesion.service';

@Component({
  selector: 'app-auth-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    RouterLink,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule,
  ],
  templateUrl: './auth-login.component.html',
  styleUrl: './auth-login.component.css',
})
export class AuthLoginComponent {
  form: FormGroup;
  ocultarPassword = true;
  cargando = false;

  estadoCuidador: 'pendiente' | 'rechazado' | null = null;
  observacionesRechazo: string | null = null;
  idUsuarioRechazado: number | null = null;

  mostrarFormRepostular = false;
  motivacionRepostular = '';
  repostulando = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private sesionService: SesionService,
    private snackBar: MatSnackBar,
    private router: Router
  ) {
    this.form = this.fb.group({
      email:    ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
    });
  }

  iniciarSesion(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.snackBar.open('Ingresa tu correo y contraseña', 'Cerrar', { duration: 3000 });
      return;
    }

    this.estadoCuidador = null;
    this.observacionesRechazo = null;
    this.mostrarFormRepostular = false;
    this.cargando = true;

    this.authService.login(this.form.value).subscribe({
      next: (usuario) => {
        this.cargando = false;
        this.sesionService.guardar(usuario);
        this.snackBar.open(`¡Bienvenido, ${usuario.nombres}!`, 'Cerrar', { duration: 3000 });
        this.redirigirSegunRol(usuario.rol ?? '');
      },
      error: (err) => {
        this.cargando = false;
        if (err.status === 403 && err.error?.tipo) {
          this.estadoCuidador = err.error.tipo;
          this.observacionesRechazo = err.error.observaciones ?? null;
          this.idUsuarioRechazado = err.error.idUsuario ?? null;
        } else {
          const msg = err.error?.mensaje ?? 'Correo o contraseña incorrectos';
          this.snackBar.open(msg, 'Cerrar', { duration: 4000 });
        }
      },
    });
  }

  abrirFormRepostular(): void {
    this.mostrarFormRepostular = true;
    this.motivacionRepostular = '';
  }

  cancelarRepostular(): void {
    this.mostrarFormRepostular = false;
    this.motivacionRepostular = '';
  }

  enviarRepostulacion(): void {
    if (!this.motivacionRepostular.trim()) {
      this.snackBar.open('Escribe una motivación antes de enviar', 'Cerrar', { duration: 3000 });
      return;
    }

    this.repostulando = true;
    this.authService.repostular({
      email: this.form.get('email')?.value,
      password: this.form.get('password')?.value,
      motivacion: this.motivacionRepostular,
    }).subscribe({
      next: () => {
        this.repostulando = false;
        this.router.navigate(['/pendiente-aprobacion']);
      },
      error: (err) => {
        this.repostulando = false;
        const msg = err.error?.mensaje ?? 'Error al enviar la solicitud';
        this.snackBar.open(msg, 'Cerrar', { duration: 4000 });
      },
    });
  }

  private redirigirSegunRol(rol: string): void {
    if (rol === 'admin') {
      this.router.navigate(['/admin']);
    } else {
      this.router.navigate(['/dashboard']);
    }
  }
}
