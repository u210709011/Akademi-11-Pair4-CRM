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

  clearSession(): void {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
  }
}
