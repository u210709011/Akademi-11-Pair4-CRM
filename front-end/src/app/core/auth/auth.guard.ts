import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';

/** Token yoksa (hic giris yapilmamis ya da oturum sonlandirilmis) korunan rotalara girisi engeller. */
export const authGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isAuthenticated()) {
    return true;
  }

  router.navigateByUrl('/login');
  return false;
};
