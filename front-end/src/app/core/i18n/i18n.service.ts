import { Injectable, signal } from '@angular/core';
import { Lang, TRANSLATIONS } from './translations';

const STORAGE_KEY = 'crm-lite-lang';

@Injectable({ providedIn: 'root' })
export class I18nService {
  private readonly currentLang = signal<Lang>(this.readStoredLang());
  readonly lang = this.currentLang.asReadonly();

  setLang(lang: Lang): void {
    this.currentLang.set(lang);
    localStorage.setItem(STORAGE_KEY, lang);
  }

  t(key: string): string {
    return TRANSLATIONS[this.currentLang()][key] ?? key;
  }

  private readStoredLang(): Lang {
    return localStorage.getItem(STORAGE_KEY) === 'tr' ? 'tr' : 'en';
  }
}
