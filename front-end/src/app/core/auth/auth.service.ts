import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';

const MOCK_USERNAME = 'salesperson';
const MOCK_PASSWORD = 'password';

@Injectable({ providedIn: 'root' })
export class AuthService {
  login(username: string, password: string): Observable<boolean> {
    const isValid = username.toLowerCase() === MOCK_USERNAME && password === MOCK_PASSWORD;
    return of(isValid);
  }
}
