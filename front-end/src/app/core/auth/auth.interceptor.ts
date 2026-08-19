import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from './auth.service';

// Token artik httpOnly cookie'de (bkz. api-gateway AuthCookieFactory) - tarayici cookie'yi
// kendisi ekliyor, biz Authorization header'ini elle set etmiyoruz. Tek yapmamiz gereken
// her istege withCredentials:true eklemek (cross-origin XHR'da cookie gonderilsin/alinsin diye).
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const authService = inject(AuthService);

  const request = req.clone({ withCredentials: true });

  return next(request).pipe(
    catchError((err: unknown) => {
      // Oturum suresi dolmus/token gecersiz - kullaniciyi otomatik Login ekranina
      // yonlendir ve local oturum durumunu temizle (bkz. FR-001 ACC-011).
      if (err instanceof HttpErrorResponse && err.status === 401) {
        authService.clearSession();
        router.navigateByUrl('/login');
      }
      return throwError(() => err);
    })
  );
};
