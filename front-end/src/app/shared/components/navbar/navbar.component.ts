import { Component, ElementRef, EventEmitter, HostListener, Output, computed, inject, signal, ChangeDetectionStrategy } from '@angular/core';
import { Router } from '@angular/router';
import { TranslateService, TranslatePipe } from '@ngx-translate/core';
import { AuthService } from '../../../core/auth';
import { persistLang } from '../../../core/i18n/lang-storage';

const DEFAULT_JOB_TITLE_KEY = 'navbar.jobTitle.default';

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

  private readonly currentUser = this.authService.getCurrentUser();

  protected readonly profileMenuOpen = signal(false);

  protected readonly userName = computed(() => this.currentUser?.name || this.translate.instant('navbar.unknownUser'));

  protected readonly userTitle = computed(() => {
    const role = this.currentUser?.roles[0];
    const key = role ? `navbar.jobTitle.${role}` : DEFAULT_JOB_TITLE_KEY;
    const translated = this.translate.instant(key);
    return translated === key ? this.translate.instant(DEFAULT_JOB_TITLE_KEY) : translated;
  });

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
