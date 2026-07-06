import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-auth-registro',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule,
  ],
  templateUrl: './auth-registro.component.html',
  styleUrl: './auth-registro.component.css',
})
export class AuthRegistroComponent {
  form: FormGroup;
  ocultarPassword = true;
  cargando = false;

  get esCuidador(): boolean { return this.form.get('idTipo')?.value === 2; }

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private snackBar: MatSnackBar,
    private router: Router
  ) {
    this.form = this.fb.group({
      nombres:    ['', [Validators.required, Validators.minLength(2)]],
      apellidos:  ['', [Validators.required, Validators.minLength(2)]],
      email:      ['', [Validators.required, Validators.email]],
      telefono:   ['', [Validators.required, Validators.pattern(/^\d{9,15}$/)]],
      password:   ['', [Validators.required, Validators.minLength(6)]],
      idTipo:     [null, Validators.required],
      motivacion: [''],
    });
  }

  registrar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.snackBar.open('Completa todos los campos correctamente', 'Cerrar', { duration: 3000 });
      return;
    }

    this.cargando = true;
    this.authService.registro(this.form.value).subscribe({
      next: (usuario) => {
        this.cargando = false;
        if (this.form.get('idTipo')?.value === 2) {
          this.snackBar.open(
            `Solicitud enviada, ${usuario.nombres}. El equipo de CareMe revisará tu perfil.`,
            'Cerrar',
            { duration: 5000 }
          );
          this.router.navigate(['/pendiente-aprobacion']);
        } else {
          this.snackBar.open(`¡Bienvenido, ${usuario.nombres}! Cuenta creada exitosamente.`, 'Cerrar', { duration: 4000 });
          this.router.navigate(['/login']);
        }
      },
      error: (err) => {
        this.cargando = false;
        const msg =
          err.error?.mensaje ??
          (err.error?.mensajes as string[] | undefined)?.join(' | ') ??
          'Error al registrar el usuario';
        this.snackBar.open(msg, 'Cerrar', { duration: 4000 });
      },
    });
  }
}
