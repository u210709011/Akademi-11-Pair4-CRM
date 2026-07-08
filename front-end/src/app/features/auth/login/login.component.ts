import { Component, inject, signal } from '@angular/core';
import { I18nService } from '../../../core/i18n';

@Component({
  selector: 'app-login',
  imports: [],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  protected readonly i18n = inject(I18nService);
  protected readonly showPassword = signal(false);

  protected togglePassword(): void {
    this.showPassword.update(value => !value);
  }
}
