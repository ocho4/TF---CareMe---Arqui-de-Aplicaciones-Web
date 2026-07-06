import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDividerModule } from '@angular/material/divider';
import { FavoritoService } from '../../services/favorito.service';
import { FamiliarService } from '../../services/familiar.service';
import { SesionService } from '../../services/sesion.service';
import { Cuidador } from '../../model/cuidador.model';

@Component({
  selector: 'app-favoritos',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatSnackBarModule,
    MatProgressSpinnerModule,
    MatDividerModule,
  ],
  templateUrl: './favoritos.component.html',
  styleUrl: './favoritos.component.css',
})
export class FavoritosComponent implements OnInit {
  private favoritoService = inject(FavoritoService);
  private familiarService = inject(FamiliarService);
  private sesionService   = inject(SesionService);
  private snackBar        = inject(MatSnackBar);
  private cdr             = inject(ChangeDetectorRef);

  favoritos: Cuidador[] = [];
  cargando  = true;
  idFamiliar = 0;

  ngOnInit(): void {
    const idUsuario = this.sesionService.obtener()?.idUsuario;
    if (!idUsuario) return;

    this.familiarService.obtenerPerfilPorUsuario(idUsuario).subscribe({
      next: (familiar) => {
        this.idFamiliar = familiar.idFamiliar;
        this.cargar();
      },
      error: () => {
        this.cargando = false;
        this.cdr.markForCheck();
      },
    });
  }

  cargar(): void {
    this.favoritoService.listarPorFamiliar(this.idFamiliar).subscribe({
      next: (lista) => {
        this.favoritos = lista;
        this.cargando = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.cargando = false;
        this.cdr.markForCheck();
        this.snackBar.open('Error al cargar favoritos', 'Cerrar', { duration: 3000 });
      },
    });
  }

  eliminar(cuidador: Cuidador): void {
    const idCuidador = cuidador.idCuidador!;
    this.favoritoService.eliminar(this.idFamiliar, idCuidador).subscribe({
      next: () => {
        this.favoritos = this.favoritos.filter(c => c.idCuidador !== idCuidador);
        this.snackBar.open('Cuidador eliminado de favoritos', 'Cerrar', { duration: 3000 });
        this.cdr.markForCheck();
      },
      error: () => this.snackBar.open('Error al eliminar favorito', 'Cerrar', { duration: 3000 }),
    });
  }

  estrellas(n: number): number[] {
    return Array.from({ length: Math.round(n ?? 0) });
  }
}
