import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, catchError, map, of } from 'rxjs';
import { environment } from '../../../environments/environment';

interface TokenResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
}

export type LoginResult = 'success' | 'invalidCredentials' | 'accountLocked';

export interface CurrentUser {
  name: string;
  roles: string[];
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);

  login(username: string, password: string): Observable<LoginResult> {
    return this.http.post<TokenResponse>(`${environment.authApiUrl}/login`, { username, password }).pipe(
      map(response => {
        localStorage.setItem('accessToken', response.accessToken);
        localStorage.setItem('refreshToken', response.refreshToken);
        return 'success' as const;
      }),
      // Backend returns 423 Locked only when Keycloak's real brute-force protection
      // (crm-realm.json: 5 attempts / 15 min) has actually kicked in - any other error
      // (wrong password, etc.) is treated as plain invalid credentials.
      catchError((err: HttpErrorResponse) =>
        of(err.status === 423 ? ('accountLocked' as const) : ('invalidCredentials' as const)))
    );
  }

  isAuthenticated(): boolean {
    return !!localStorage.getItem('accessToken');
  }

  // accessToken is the raw Keycloak-issued JWT (see api-gateway AuthController/AuthService -
  // it's forwarded as-is, never re-signed), so the user's name and roles can be read straight
  // from its claims without an extra backend call.
  getCurrentUser(): CurrentUser | null {
    const token = localStorage.getItem('accessToken');
    const payload = token ? this.decodeJwtPayload(token) : null;
    if (!payload) {
      return null;
    }

    const givenName = typeof payload['given_name'] === 'string' ? payload['given_name'] as string : '';
    const familyName = typeof payload['family_name'] === 'string' ? payload['family_name'] as string : '';
    const fullName = `${givenName} ${familyName}`.trim();
    const name = fullName
      || (typeof payload['name'] === 'string' ? payload['name'] as string : '')
      || (typeof payload['preferred_username'] === 'string' ? payload['preferred_username'] as string : '');
    const roles = (payload['realm_access'] as { roles?: string[] } | undefined)?.roles ?? [];

    return { name, roles };
  }

  private decodeJwtPayload(token: string): Record<string, unknown> | null {
    const payload = token.split('.')[1];
    if (!payload) {
      return null;
    }

    try {
      const base64 = payload.replace(/-/g, '+').replace(/_/g, '/');
      const padded = base64.padEnd(base64.length + (4 - base64.length % 4) % 4, '=');
      const bytes = Uint8Array.from(atob(padded), char => char.charCodeAt(0));
      return JSON.parse(new TextDecoder('utf-8').decode(bytes));
    } catch {
      return null;
    }
  }

  clearSession(): void {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
  }

  // UC-EACRML-001 Alt Senaryo 5: oturumu sonlandirir. Local session backend cagrisindan
  // BAGIMSIZ olarak hemen temizlenir - kullanici agin/backend'in durumundan etkilenmeden
  // her zaman cikis yapabilmeli. Refresh token backend'e (Keycloak) gecersiz kilinmasi icin
  // best-effort gonderilir; token yoksa ya da cagri basarisiz olursa sessizce yutulur.
  logout(): Observable<void> {
    const refreshToken = localStorage.getItem('refreshToken');
    this.clearSession();

    if (!refreshToken) {
      return of(undefined);
    }

    return this.http.post<void>(`${environment.authApiUrl}/logout`, { refreshToken }).pipe(
      map(() => undefined),
      catchError(() => of(undefined))
    );
  }
}
