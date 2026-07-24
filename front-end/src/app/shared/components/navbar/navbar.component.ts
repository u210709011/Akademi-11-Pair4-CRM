import { Component, ElementRef, EventEmitter, HostListener, Output, inject, signal, ChangeDetectionStrategy } from '@angular/core';
import { I18nService } from '../../../core/i18n';

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

  protected readonly profileMenuOpen = signal(false);

  protected toggleLangMenu(): void {
    this.i18n.setLang(this.i18n.lang() === 'en' ? 'tr' : 'en');
  }

  protected toggleProfileMenu(): void {
    this.profileMenuOpen.update(open => !open);
  }

  @HostListener('document:click', ['$event'])
  protected onDocumentClick(event: MouseEvent): void {
    if (!this.elementRef.nativeElement.contains(event.target as Node)) {
      this.profileMenuOpen.set(false);
    }
  }
}
