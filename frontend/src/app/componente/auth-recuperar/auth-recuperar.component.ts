import { Component } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
  AbstractControl,
  ValidationErrors,
} from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AuthService } from '../../services/auth.service';

function emailOTelefonoRequerido(group: AbstractControl): ValidationErrors | null {
  const email    = (group.get('email')?.value    ?? '').trim();
  const telefono = (group.get('telefono')?.value ?? '').trim();
  return email || telefono ? null : { sinContacto: true };
}

function passwordsIguales(group: AbstractControl): ValidationErrors | null {
  const nueva     = group.get('nuevaPassword')?.value;
  const confirmar = group.get('confirmarPassword')?.value;
  return nueva && confirmar && nueva !== confirmar ? { noCoinciden: true } : null;
}

@Component({
  selector: 'app-auth-recuperar',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule,
  ],
  templateUrl: './auth-recuperar.component.html',
  styleUrl: './auth-recuperar.component.css',
})
export class AuthRecuperarComponent {
  paso2Visible = false;
  mensajeToken = '';
  cargandoPaso1 = false;
  cargandoPaso2 = false;
  ocultarPassword  = true;
  ocultarConfirmar = true;

  paso1: FormGroup;
  paso2: FormGroup;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private snackBar: MatSnackBar,
    private router: Router
  ) {
    this.paso1 = this.fb.group(
      {
        email:    ['', Validators.email],
        telefono: ['', Validators.pattern(/^\d{9,15}$/)],
      },
      { validators: emailOTelefonoRequerido }
    );

    this.paso2 = this.fb.group(
      {
        token:             ['', Validators.required],
        nuevaPassword:     ['', [Validators.required, Validators.minLength(6)]],
        confirmarPassword: ['', Validators.required],
      },
      { validators: passwordsIguales }
    );
  }

  solicitarToken(): void {
    this.paso1.markAllAsTouched();
    if (this.paso1.invalid) {
      this.snackBar.open('Ingresa tu correo o número de teléfono', 'Cerrar', { duration: 3000 });
      return;
    }

    this.cargandoPaso1 = true;
    const datos = {
      email:    this.paso1.value.email?.trim()    || undefined,
      telefono: this.paso1.value.telefono?.trim() || undefined,
    };

    this.authService.recuperarPassword(datos).subscribe({
      next: (respuesta) => {
        this.cargandoPaso1 = false;
        this.mensajeToken  = respuesta;
        this.paso2Visible  = true;
      },
      error: (err) => {
        this.cargandoPaso1 = false;
        const msg = err.error?.mensaje ?? 'No se encontró una cuenta con ese dato';
        this.snackBar.open(msg, 'Cerrar', { duration: 4000 });
      },
    });
  }

  restablecerPassword(): void {
    this.paso2.markAllAsTouched();
    if (this.paso2.invalid) return;

    this.cargandoPaso2 = true;
    const { token, nuevaPassword } = this.paso2.value;

    this.authService.resetPassword({ token, nuevaPassword }).subscribe({
      next: () => {
        this.cargandoPaso2 = false;
        this.snackBar.open(
          'Contraseña actualizada. Ya puedes iniciar sesión.',
          'Cerrar',
          { duration: 5000 }
        );
        this.router.navigate(['/login']);
      },
      error: (err) => {
        this.cargandoPaso2 = false;
        const msg = err.error?.mensaje ?? 'Token inválido o expirado';
        this.snackBar.open(msg, 'Cerrar', { duration: 4000 });
      },
    });
  }

  volver(): void {
    this.paso2Visible = false;
    this.mensajeToken = '';
    this.paso2.reset();
  }
}
