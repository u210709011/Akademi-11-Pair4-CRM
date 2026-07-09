import { Component, DestroyRef, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/auth';
import { I18nService } from '../../../core/i18n';

const MAX_FAILED_ATTEMPTS = 5;
const LOCK_DURATION_MS = 15 * 60 * 1000;

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  protected readonly i18n = inject(I18nService);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly formBuilder = inject(FormBuilder);
  private readonly destroyRef = inject(DestroyRef);

  protected readonly showPassword = signal(false);
  protected readonly loginError = signal(false);
  protected readonly accountLocked = signal(false);

  private failedAttempts = 0;
  private lockTimeoutId?: ReturnType<typeof setTimeout>;

// it only makes trim and length validation in frontend, once keycloak is done it will be connect to the backend
  protected readonly loginForm = this.formBuilder.nonNullable.group({
    username: ['', [Validators.required, Validators.maxLength(50)]],
    password: ['', [Validators.required, Validators.maxLength(50)]]
  });

  constructor() {
    this.loginForm.valueChanges.subscribe(() => this.loginError.set(false));
    this.destroyRef.onDestroy(() => clearTimeout(this.lockTimeoutId));
  }

  protected togglePassword(): void {
    this.showPassword.update(value => !value);
  }

  protected trimUsername(): void {
    const control = this.loginForm.controls.username;
    control.setValue(control.value.trim());
  }

  protected submit(): void {
    if (this.loginForm.invalid || this.accountLocked()) {
      return;
    }

    const { username, password } = this.loginForm.getRawValue();

    this.authService.login(username.trim(), password).subscribe(success => {
      if (success) {
        this.loginError.set(false);
        this.router.navigateByUrl('/search-customer');
        return;
      }

      this.failedAttempts++;

      if (this.failedAttempts >= MAX_FAILED_ATTEMPTS) {
        this.loginError.set(false);
        this.lockAccount();
      } else {
        this.loginError.set(true);
      }
    });
  }

  private lockAccount(): void {
    this.accountLocked.set(true);
    this.lockTimeoutId = setTimeout(() => {
      this.accountLocked.set(false);
      this.failedAttempts = 0;
    }, LOCK_DURATION_MS);
  }
}
