import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { SesionService } from '../services/sesion.service';

export function rolGuard(roles: string[]): CanActivateFn {
  return () => {
    const sesion = inject(SesionService);
    const router = inject(Router);
    const usuario = sesion.obtener();
    if (usuario && usuario.rol && roles.includes(usuario.rol)) return true;
    return router.createUrlTree(['/dashboard']);
  };
}
