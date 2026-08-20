import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { Observable, catchError, map, of, switchMap } from 'rxjs';
import { environment } from '../../../environments/environment';

export type LoginResult = 'success' | 'invalidCredentials' | 'accountLocked';

export interface CurrentUser {
  name: string;
  roles: string[];
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly currentUserSignal = signal<CurrentUser | null>(null);
  private sessionChecked = false;

  login(username: string, password: string): Observable<LoginResult> {
    return this.http.post<void>(`${environment.authApiUrl}/login`, { username, password }).pipe(
      switchMap(() => this.fetchCurrentUser()),
      map(() => 'success' as const),

      catchError((err: HttpErrorResponse) =>
        of(err.status === 423 ? ('accountLocked' as const) : ('invalidCredentials' as const)))
    );
  }


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
