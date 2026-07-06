import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { SesionService } from '../services/sesion.service';

export const authErrorInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const sesionService = inject(SesionService);

  return next(req).pipe(
    catchError(err => {
      if (err.status === 401) {
        sesionService.cerrar();
        router.navigate(['/login']);
      }
      return throwError(() => err);
    })
  );
};
