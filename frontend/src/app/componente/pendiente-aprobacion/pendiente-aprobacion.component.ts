import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-pendiente-aprobacion',
  standalone: true,
  imports: [RouterLink, MatButtonModule, MatIconModule],
  templateUrl: './pendiente-aprobacion.component.html',
  styleUrl: './pendiente-aprobacion.component.css',
})
export class PendienteAprobacionComponent {}
