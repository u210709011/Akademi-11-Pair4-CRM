import { Component, ElementRef, EventEmitter, HostListener, Output, inject, signal, ChangeDetectionStrategy } from '@angular/core';
import { Router } from '@angular/router';
import { TranslateService, TranslatePipe } from '@ngx-translate/core';
import { AuthService } from '../../../core/auth';
import { persistLang } from '../../../core/i18n/lang-storage';

@Component({
  selector: 'app-navbar',
  imports: [TranslatePipe],
  templateUrl: './navbar.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './navbar.component.scss'
})
export class NavbarComponent {
  @Output() readonly menuToggle = new EventEmitter<void>();

  protected readonly translate = inject(TranslateService);
  private readonly elementRef = inject(ElementRef<HTMLElement>);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly profileMenuOpen = signal(false);

  protected toggleLangMenu(): void {
    const next = this.translate.currentLang() === 'en' ? 'tr' : 'en';
    this.translate.use(next);
    persistLang(next);
  }

  protected toggleProfileMenu(): void {
    this.profileMenuOpen.update(open => !open);
  }

  // UC-EACRML-001 Alt Senaryo 5: cikis - oturumu sonlandirir ve Login ekranina doner.
  protected logout(): void {
    this.authService.logout().subscribe(() => {
      this.profileMenuOpen.set(false);
      this.router.navigateByUrl('/login');
    });
  }

  @HostListener('document:click', ['$event'])
  protected onDocumentClick(event: MouseEvent): void {
    if (!this.elementRef.nativeElement.contains(event.target as Node)) {
      this.profileMenuOpen.set(false);
    }
  }
}
