import { Component, ElementRef, HostListener, inject, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { I18nService, Lang } from '../../core/i18n';

@Component({
  selector: 'app-auth-layout',
  imports: [RouterOutlet],
  templateUrl: './auth-layout.component.html',
  styleUrl: './auth-layout.component.scss'
})
export class AuthLayoutComponent {
  protected readonly i18n = inject(I18nService);
  private readonly elementRef = inject(ElementRef<HTMLElement>);

  protected readonly langMenuOpen = signal(false);

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

  @HostListener('document:click', ['$event'])
  protected onDocumentClick(event: MouseEvent): void {
    if (!this.elementRef.nativeElement.contains(event.target as Node)) {
      this.langMenuOpen.set(false);
    }
  }
}
