import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { I18nService } from './i18n.service';

// Backend-driven dynamic degerler (ör. GNL_TP name/descr) kullanicinin sectigi dile gore
// cevrilebilsin diye her istege Accept-Language eklenir - backend zaten bu header'i
// (LocaleContextHolder) mesaj ceviri metinleri icin okuyor, ayni mekanizma yeniden kullanilir.
export const languageInterceptor: HttpInterceptorFn = (req, next) => {
  const i18n = inject(I18nService);
  const request = req.clone({ setHeaders: { 'Accept-Language': i18n.lang() } });
  return next(request);
};
