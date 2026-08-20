import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Observable, shareReplay } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Characteristic, CharacteristicValue, GnlType } from './lookup.model';

// Grup basina bir kere cekilip (shareReplay) tum caller'lar arasinda paylasilir - dropdown'lari
// dolduran her component kendi HTTP cagrisini tekrar atmaz. Cache key'lerine mevcut dil dahildir -
// aksi halde dil degistirildiginde eski dildeki cevap sonsuza kadar (poisoned cache) donerdi,
// backend zaten locale-aware oldugu icin bu artik yanlis sonuc verirdi.
@Injectable({ providedIn: 'root' })
export class LookupService {
  private readonly http = inject(HttpClient);
  private readonly translate = inject(TranslateService);
  private readonly cache = new Map<string, Observable<GnlType[]>>();
  private readonly characteristicsCache = new Map<string, Observable<Characteristic[]>>();
  private readonly characteristicValuesCache = new Map<string, Observable<CharacteristicValue[]>>();

  getTypesByGroup(entCodeName: string): Observable<GnlType[]> {
    const key = `${this.translate.currentLang() ?? this.translate.fallbackLang() ?? 'en'}:${entCodeName}`;
    let cached = this.cache.get(key);
    if (!cached) {
      cached = this.http
        .get<GnlType[]>(`${environment.apiGatewayUrl}/api/v1/general-types`, { params: { entCodeName } })
        .pipe(shareReplay(1));
      this.cache.set(key, cached);
    }
    return cached;
  }

  getCharacteristics(): Observable<Characteristic[]> {
    const key = this.translate.currentLang() ?? this.translate.fallbackLang() ?? 'en';
    let cached = this.characteristicsCache.get(key);
    if (!cached) {
      cached = this.http
        .get<Characteristic[]>(`${environment.apiGatewayUrl}/api/v1/characteristics`)
        .pipe(shareReplay(1));
      this.characteristicsCache.set(key, cached);
    }
    return cached;
  }

  getCharacteristicValues(): Observable<CharacteristicValue[]> {
    const key = this.translate.currentLang() ?? this.translate.fallbackLang() ?? 'en';
    let cached = this.characteristicValuesCache.get(key);
    if (!cached) {
      cached = this.http
        .get<CharacteristicValue[]>(`${environment.apiGatewayUrl}/api/v1/characteristic-values`)
        .pipe(shareReplay(1));
      this.characteristicValuesCache.set(key, cached);
    }
    return cached;
  }
}
