import { HttpInterceptorFn } from '@angular/common/http';

const STORAGE_KEY = 'careme_usuario';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (raw) {
      const usuario = JSON.parse(raw);
      if (usuario?.token) {
        const cloned = req.clone({
          setHeaders: { Authorization: `Bearer ${usuario.token}` }
        });
        return next(cloned);
      }
    }
  } catch {
    // continue without token
  }
  return next(req);
};
