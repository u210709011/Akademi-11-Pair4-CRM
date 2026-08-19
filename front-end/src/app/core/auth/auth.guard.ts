import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { map } from 'rxjs';
import { AuthService } from './auth.service';

/** Token artik httpOnly cookie'de - JS'ten "var mi yok mu" diye senkron bakilamaz, bu yuzden
 * guard async: gerekirse (ör. sayfa yenilenmesinden sonra) backend'e /me ile sorup cevap
 * gelene kadar bekler. */
export const authGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return authService.ensureAuthenticated().pipe(
    map(isAuthenticated => {
      if (isAuthenticated) {
        return true;
      }
      router.navigateByUrl('/login');
      return false;
    })
  );
};
