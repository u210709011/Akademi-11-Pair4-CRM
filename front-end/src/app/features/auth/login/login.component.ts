import { Component, DestroyRef, effect, inject, signal, ChangeDetectionStrategy } from '@angular/core';
import { form, FormField, maxLength, pattern, required } from '@angular/forms/signals';
import { Router } from '@angular/router';
import { TranslateService, TranslatePipe } from '@ngx-translate/core';
import { AuthService } from '../../../core/auth';

// Keycloak'in gercek bruteforce penceresiyle ayni (bkz. infra/keycloak/crm-realm.json:
// waitIncrementSeconds=900). Kilit durumunun KENDISI backend'den gelir (bkz. AuthService),
// bu sure sadece kilit acildiktan sonra butonu tekrar aktif etmek icin kullanilan bir
// UX yardimcisidir - "kac kere yanlis girildi" sayaci artik burada TUTULMAZ.
const LOCK_DURATION_MS = 15 * 60 * 1000;

// UC-EACRML-001 validasyon tablosu: username/password bosluk ile baslayip bitemez.
// Tek karakterlik degerleri de kapsamasi icin ikinci grup opsiyonel.
const NO_LEADING_TRAILING_SPACE_PATTERN = /^\S(.*\S)?$/;

type LoginFieldErrorKey = 'fieldRequired' | 'maxLengthError' | 'noLeadingTrailingSpace';

type LoginErrorKey = 'wrongCredentials' | 'accountLocked';

@Component({
  selector: 'app-login',
  imports: [FormField, TranslatePipe],
  templateUrl: './login.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  protected readonly translate = inject(TranslateService);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);

  protected readonly showPassword = signal(false);
  protected readonly isLoggingIn = signal(false);

  protected readonly loginErrors = signal<Record<LoginErrorKey, boolean>>({
    wrongCredentials: false,
    accountLocked: false
  });

  private lockTimeoutId?: ReturnType<typeof setTimeout>;

  // it only makes trim and length validation in frontend, once keycloak is done it will be connect to the backend
  protected readonly loginModel = signal({ username: '', password: '' });

  protected readonly loginForm = form(this.loginModel, path => {
    required(path.username);
    maxLength(path.username, 50);
    pattern(path.username, NO_LEADING_TRAILING_SPACE_PATTERN, { when: ({ value }) => value() !== '' });
    required(path.password);
    maxLength(path.password, 50);
    pattern(path.password, NO_LEADING_TRAILING_SPACE_PATTERN, { when: ({ value }) => value() !== '' });
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

  protected setLoginError(key: LoginErrorKey, hasError: boolean): void {
    this.loginErrors.update(errors => ({ ...errors, [key]: hasError }));
  }

  // UC-EACRML-001: her alan icin uc kural (zorunlu / max 50 / bosluk ile baslayip bitmeme) ayni
  // dokumandaki metinle gosterilir - once "hangi kural ihlal edildi" belirlenir, sonra ilgili i18n
  // key'i doner. Field henuz dokunulmadiysa (touched=false) hicbir mesaj gosterilmez.
  protected usernameErrorKey(): LoginFieldErrorKey | null {
    return this.fieldErrorKey(this.loginForm.username(), this.loginModel().username);
  }

  protected passwordErrorKey(): LoginFieldErrorKey | null {
    return this.fieldErrorKey(this.loginForm.password(), this.loginModel().password);
  }

  private fieldErrorKey(field: { invalid(): boolean; touched(): boolean }, value: string): LoginFieldErrorKey | null {
    if (!field.invalid() || !field.touched()) {
      return null;
    }
    if (value === '') {
      return 'fieldRequired';
    }
    if (value.length > 50) {
      return 'maxLengthError';
    }
    return 'noLeadingTrailingSpace';
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

    this.isLoggingIn.set(true);
    this.authService.login(username.trim(), password).subscribe(result => {
      this.isLoggingIn.set(false);
      if (result === 'success') {
        this.setLoginError('wrongCredentials', false);
        this.setLoginError('accountLocked', false);
        this.router.navigateByUrl('/search-customer');
        return;
      }

      if (result === 'accountLocked') {
        this.setLoginError('wrongCredentials', false);
        this.lockAccount();
      } else {
        this.setLoginError('wrongCredentials', true);
      }
    });
  }

  private lockAccount(): void {
    this.setLoginError('accountLocked', true);
    clearTimeout(this.lockTimeoutId);
    this.lockTimeoutId = setTimeout(() => {
      this.setLoginError('accountLocked', false);
    }, LOCK_DURATION_MS);
  }
}
