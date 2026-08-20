import { provideHttpClient, withInterceptors, withXhr } from '@angular/common/http';
import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { DateAdapter, MAT_DATE_FORMATS, MAT_NATIVE_DATE_FORMATS, NativeDateAdapter } from '@angular/material/core';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { authInterceptor } from './core/auth';
import { languageInterceptor } from './core/i18n';

//for datepicker input format
class AppDateAdapter extends NativeDateAdapter {
  override format(date: Date): string {
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    return `${day} / ${month} / ${date.getFullYear()}`;
  }

  // Placeholder DD/MM/YYYY diyor ama NativeDateAdapter.parse() miras yoluyla native
  // Date ayristirmasini kullaniyordu, o da "15/06/1990" gibi degerleri MM/DD/YYYY
  // sanip ay=15 icin gecersiz tarih uretiyordu (kullaniciya "This field is required"
  // olarak yansiyordu, cunku model null kaliyordu). DD/MM/YYYY'yi burada acikca
  // ayristiriyoruz; "/" etrafindaki bosluklar da kabul edilir (format()'un kendi
  // ciktisini geri parse edebilmek icin).
  override parse(value: unknown): Date | null {
    if (typeof value === 'string' && value.trim()) {
      const match = value.trim().match(/^(\d{1,2})\s*\/\s*(\d{1,2})\s*\/\s*(\d{4})$/);
      if (match) {
        const [, dayStr, monthStr, yearStr] = match;
        const day = Number(dayStr);
        const month = Number(monthStr);
        const year = Number(yearStr);
        const date = new Date(year, month - 1, day);
        // Date constructor overflow'u sessizce yuvarlar (or. 31/02 -> 03/03);
        // ay/gun geri okunmuyorsa gecersiz say.
        const valid = date.getFullYear() === year && date.getMonth() === month - 1 && date.getDate() === day;
        return valid ? date : new Date(NaN);
      }
    }
    return super.parse(value);
  }
}

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(withXhr(), withInterceptors([authInterceptor, languageInterceptor])),
    provideAnimationsAsync(),
    { provide: DateAdapter, useClass: AppDateAdapter },
    { provide: MAT_DATE_FORMATS, useValue: MAT_NATIVE_DATE_FORMATS }
  ]
};
