import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/auth';
import { I18nService } from '../../../core/i18n';

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

  protected readonly showPassword = signal(false);
  protected readonly loginError = signal(false);

// it only makes trim and length validation in frontend, once keycloak is done it will be connect to the backend
  protected readonly loginForm = this.formBuilder.nonNullable.group({
    username: ['', [Validators.required, Validators.maxLength(50)]],
    password: ['', [Validators.required, Validators.maxLength(50)]]
  });

  protected togglePassword(): void {
    this.showPassword.update(value => !value);
  }

  protected trimUsername(): void {
    const control = this.loginForm.controls.username;
    control.setValue(control.value.trim());
  }

  protected submit(): void {
    if (this.loginForm.invalid) {
      return;
    }

    const { username, password } = this.loginForm.getRawValue();

    this.authService.login(username.trim(), password).subscribe(success => {
      if (success) {
        this.loginError.set(false);
        this.router.navigateByUrl('/search-customer');
      } else {
        this.loginError.set(true);
      }
    });
  }
}
