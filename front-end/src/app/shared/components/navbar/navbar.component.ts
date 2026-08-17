import { Component, ElementRef, EventEmitter, HostListener, Output, computed, inject, signal, ChangeDetectionStrategy } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/auth';
import { I18nService } from '../../../core/i18n';

const DEFAULT_JOB_TITLE_KEY = 'navbar.jobTitle.default';

@Component({
  selector: 'app-navbar',
  imports: [],
  templateUrl: './navbar.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './navbar.component.scss'
})
export class NavbarComponent {
  @Output() readonly menuToggle = new EventEmitter<void>();

  protected readonly i18n = inject(I18nService);
  private readonly elementRef = inject(ElementRef<HTMLElement>);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  private readonly currentUser = this.authService.getCurrentUser();

  protected readonly profileMenuOpen = signal(false);

  protected readonly userName = computed(() => this.currentUser?.name || this.i18n.t('navbar.unknownUser'));

  protected readonly userTitle = computed(() => {
    const role = this.currentUser?.roles[0];
    const key = role ? `navbar.jobTitle.${role}` : DEFAULT_JOB_TITLE_KEY;
    const translated = this.i18n.t(key);
    return translated === key ? this.i18n.t(DEFAULT_JOB_TITLE_KEY) : translated;
  });

  protected toggleLangMenu(): void {
    this.i18n.setLang(this.i18n.lang() === 'en' ? 'tr' : 'en');
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
