import { HttpInterceptorFn } from '@angular/common/http';
import { readStoredLang } from './lang-storage';

// Backend-driven dynamic degerler (ör. GNL_TP name/descr) kullanicinin sectigi dile gore
// cevrilebilsin diye her istege Accept-Language eklenir - backend zaten bu header'i
// (LocaleContextHolder) mesaj ceviri metinleri icin okuyor, ayni mekanizma yeniden kullanilir.
// TranslateService buradan enjekte edilmez: onun kendi HTTP loader'i da bu interceptor'dan
// gecer, TranslateService henuz kurulurken kendisini enjekte etmeye calismak NG0200
// (circular dependency) hatasina yol aciyordu - dil bilgisi bunun yerine dogrudan
// localStorage'dan (persistLang'in yazdigi kaynak) okunur.
export const languageInterceptor: HttpInterceptorFn = (req, next) => {
  const request = req.clone({ setHeaders: { 'Accept-Language': readStoredLang() } });
  return next(request);
};
