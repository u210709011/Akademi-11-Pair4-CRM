export const LANG_STORAGE_KEY = 'crm-lite-lang';

export function persistLang(lang: string): void {
  localStorage.setItem(LANG_STORAGE_KEY, lang);
}

export function readStoredLang(): 'en' | 'tr' {
  return localStorage.getItem(LANG_STORAGE_KEY) === 'tr' ? 'tr' : 'en';
}
