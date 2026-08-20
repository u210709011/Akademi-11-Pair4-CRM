import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

// Backend-driven dynamic degerler (ör. GNL_TP name/descr) kullanicinin sectigi dile gore
// cevrilebilsin diye her istege Accept-Language eklenir - backend zaten bu header'i
// (LocaleContextHolder) mesaj ceviri metinleri icin okuyor, ayni mekanizma yeniden kullanilir.
export const languageInterceptor: HttpInterceptorFn = (req, next) => {
  const translate = inject(TranslateService);
  const request = req.clone({ setHeaders: { 'Accept-Language': translate.currentLang() ?? translate.fallbackLang() ?? 'en' } });
  return next(request);
};
