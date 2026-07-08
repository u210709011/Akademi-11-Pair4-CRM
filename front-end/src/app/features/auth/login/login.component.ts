<<<<<<< HEAD
import { Component, inject, signal } from '@angular/core';
import { I18nService } from '../../../core/i18n';
=======
import { Component } from '@angular/core';
>>>>>>> e30a3ca57aad24604ec7887d56afe09e94e85c61

@Component({
  selector: 'app-login',
  imports: [],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
<<<<<<< HEAD
  protected readonly i18n = inject(I18nService);
  protected readonly showPassword = signal(false);

  protected togglePassword(): void {
    this.showPassword.update(value => !value);
  }
=======

>>>>>>> e30a3ca57aad24604ec7887d56afe09e94e85c61
}
