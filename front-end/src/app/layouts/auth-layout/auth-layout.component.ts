import { Component, ElementRef, HostListener, inject, signal, ChangeDetectionStrategy } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { TranslateService, TranslatePipe } from '@ngx-translate/core';
import { persistLang } from '../../core/i18n/lang-storage';

type Lang = 'en' | 'tr';

@Component({
  selector: 'app-auth-layout',
  imports: [RouterOutlet, TranslatePipe],
  templateUrl: './auth-layout.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './auth-layout.component.scss'
})
export class AuthLayoutComponent {
  protected readonly translate = inject(TranslateService);
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
    this.translate.use(lang);
    persistLang(lang);
    this.langMenuOpen.set(false);
  }

  @HostListener('document:click', ['$event'])
  protected onDocumentClick(event: MouseEvent): void {
    if (!this.elementRef.nativeElement.contains(event.target as Node)) {
      this.langMenuOpen.set(false);
    }
  }
}
