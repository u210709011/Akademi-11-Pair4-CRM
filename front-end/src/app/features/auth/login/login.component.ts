import { Component, DestroyRef, effect, inject, signal, ChangeDetectionStrategy } from '@angular/core';
import { form, FormField, maxLength, required } from '@angular/forms/signals';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/auth';
import { I18nService } from '../../../core/i18n';

const MAX_FAILED_ATTEMPTS = 5;
const LOCK_DURATION_MS = 15 * 60 * 1000;

type LoginErrorKey = 'wrongCredentials' | 'accountLocked';

@Component({
  selector: 'app-login',
  imports: [FormField],
  templateUrl: './login.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  protected readonly i18n = inject(I18nService);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);

  protected readonly showPassword = signal(false);

  protected readonly loginErrors = signal<Record<LoginErrorKey, boolean>>({
    wrongCredentials: false,
    accountLocked: false
  });

  private failedAttempts = 0;
  private lockTimeoutId?: ReturnType<typeof setTimeout>;

  // it only makes trim and length validation in frontend, once keycloak is done it will be connect to the backend
  protected readonly loginModel = signal({ username: '', password: '' });

  protected readonly loginForm = form(this.loginModel, path => {
    required(path.username);
    maxLength(path.username, 50);
    required(path.password);
    maxLength(path.password, 50);
  });

  constructor() {
    effect(() => {
      this.loginModel();
      this.setLoginError('wrongCredentials', false);
    });
    this.destroyRef.onDestroy(() => clearTimeout(this.lockTimeoutId));
  }

  protected togglePassword(): void {
    this.showPassword.update(value => !value);
  }

  protected trimUsername(): void {
    this.loginModel.update(value => ({ ...value, username: value.username.trim() }));
  }

  protected setLoginError(key: LoginErrorKey, hasError: boolean): void {
    this.loginErrors.update(errors => ({ ...errors, [key]: hasError }));
  }

  protected onSubmit(event: Event): void {
    event.preventDefault();
    this.submit();
  }

  private submit(): void {
    if (this.loginForm().invalid() || this.loginErrors().accountLocked) {
      return;
    }

    const { username, password } = this.loginModel();

    this.authService.login(username.trim(), password).subscribe(success => {
      if (success) {
        this.setLoginError('wrongCredentials', false);
        this.router.navigateByUrl('/search-customer');
        return;
      }

      this.failedAttempts++;

      if (this.failedAttempts >= MAX_FAILED_ATTEMPTS) {
        this.setLoginError('wrongCredentials', false);
        this.lockAccount();
      } else {
        this.setLoginError('wrongCredentials', true);
      }
    });
  }

  private lockAccount(): void {
    this.setLoginError('accountLocked', true);
    this.lockTimeoutId = setTimeout(() => {
      this.setLoginError('accountLocked', false);
      this.failedAttempts = 0;
    }, LOCK_DURATION_MS);
  }
}
