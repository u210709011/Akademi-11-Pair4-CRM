import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { Observable, catchError, map, of, switchMap } from 'rxjs';
import { environment } from '../../../environments/environment';

export type LoginResult = 'success' | 'invalidCredentials' | 'accountLocked';

export interface CurrentUser {
  name: string;
  roles: string[];
}

// Token artik httpOnly cookie'de (JS'ten okunamaz, XSS ile calinamaz) - bu servis token'i
// hic gormez. Kullanicinin kim oldugunu (isim/roller) ogrenmek icin backend'deki /me ucuna
// gidilir, cevap bellekte (signal) tutulur - sayfa yenilenince kaybolur, bu yuzden authGuard
// her rota aktivasyonunda ensureAuthenticated() ile (gerekirse) /me'yi tekrar sorar.
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly currentUserSignal = signal<CurrentUser | null>(null);
  private sessionChecked = false;

  login(username: string, password: string): Observable<LoginResult> {
    return this.http.post<void>(`${environment.authApiUrl}/login`, { username, password }).pipe(
      switchMap(() => this.fetchCurrentUser()),
      map(() => 'success' as const),
      // Backend returns 423 Locked only when Keycloak's real brute-force protection
      // (crm-realm.json: 5 attempts / 15 min) has actually kicked in - any other error
      // (wrong password, etc.) is treated as plain invalid credentials.
      catchError((err: HttpErrorResponse) =>
        of(err.status === 423 ? ('accountLocked' as const) : ('invalidCredentials' as const)))
    );
  }

  /** authGuard bunu cagirir: bu SPA calisirken zaten kontrol edildiyse tekrar aga gitmez,
   * ilk cagrida (ör. sayfa yenilenmesinden sonra) cookie hala gecerliyse /me 200 doner. */
  ensureAuthenticated(): Observable<boolean> {
    if (this.sessionChecked) {
      return of(!!this.currentUserSignal());
    }
    return this.fetchCurrentUser().pipe(
      map(() => true),
      catchError(() => {
        this.currentUserSignal.set(null);
        this.sessionChecked = true;
        return of(false);
      })
    );
  }

  getCurrentUser(): CurrentUser | null {
    return this.currentUserSignal();
  }

  clearSession(): void {
    this.currentUserSignal.set(null);
    this.sessionChecked = true;
  }

  // UC-EACRML-001 Alt Senaryo 5: oturumu sonlandirir. Local session backend cagrisindan
  // BAGIMSIZ olarak hemen temizlenir - kullanici agin/backend'in durumundan etkilenmeden
  // her zaman cikis yapabilmeli. Cookie'ler (dolayisiyla Keycloak'taki refresh token) backend
  // tarafinda best-effort temizlenir; basarisiz olsa da kullaniciyi engellemez.
  logout(): Observable<void> {
    this.clearSession();
    return this.http.post<void>(`${environment.authApiUrl}/logout`, {}).pipe(
      map(() => undefined),
      catchError(() => of(undefined))
    );
  }

  private fetchCurrentUser(): Observable<CurrentUser> {
    return this.http.get<CurrentUser>(`${environment.authApiUrl}/me`).pipe(
      map(user => {
        this.currentUserSignal.set(user);
        this.sessionChecked = true;
        return user;
      })
    );
  }
}
