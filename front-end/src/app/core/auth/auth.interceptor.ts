import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthService } from './auth.service';
//her http isteğini tutup üzerinde değişiklik yapabiliyoruz, localstorageda tokenı okuyup
//headera ekliyoruz, sonra requesti devam ettiriyoruz ki her service istek attığımızda this.http.get(url,{headers yapmayalım})
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // 2 kere istek atıyordu  jwt token eski olduğunda bi 200 bi 401 dönüyordu, header eklemek bu istekleri gereksiz yere 401'letir.
  if (req.url.startsWith(environment.authApiUrl)) {
    return next(req);
  }

  const accessToken = localStorage.getItem('accessToken');
  const router = inject(Router);
  const authService = inject(AuthService);

  const request = accessToken
    ? req.clone({ setHeaders: { Authorization: `Bearer ${accessToken}` } })
    : req;

  return next(request).pipe(
    catchError((err: unknown) => {
      // Oturum suresi dolmus/token gecersiz - kullaniciyi otomatik Login ekranina
      // yonlendir ve gecersiz token'i temizle (bkz. FR-001 ACC-011).
      if (err instanceof HttpErrorResponse && err.status === 401) {
        authService.clearSession();
        router.navigateByUrl('/login');
      }
      return throwError(() => err);
    })
  );
};
