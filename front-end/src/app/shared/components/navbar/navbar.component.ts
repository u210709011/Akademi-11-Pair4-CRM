import { Component, ElementRef, EventEmitter, HostListener, Output, inject, signal, ChangeDetectionStrategy } from '@angular/core';
import { I18nService, Lang } from '../../../core/i18n';

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

  protected readonly langMenuOpen = signal(false);
  protected readonly profileMenuOpen = signal(false);

  protected readonly languages: { code: Lang; label: string }[] = [
    { code: 'en', label: 'English' },
    { code: 'tr', label: 'Türkçe' }
  ];

  protected toggleLangMenu(): void {
    this.langMenuOpen.update(open => !open);
  }

  protected selectLang(lang: Lang): void {
    this.i18n.setLang(lang);
    this.langMenuOpen.set(false);
  }

  protected toggleProfileMenu(): void {
    this.profileMenuOpen.update(open => !open);
  }

  @HostListener('document:click', ['$event'])
  protected onDocumentClick(event: MouseEvent): void {
    if (!this.elementRef.nativeElement.contains(event.target as Node)) {
      this.langMenuOpen.set(false);
      this.profileMenuOpen.set(false);
    }
  }
}
