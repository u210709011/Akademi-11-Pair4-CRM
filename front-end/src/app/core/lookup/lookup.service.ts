import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, shareReplay } from 'rxjs';
import { environment } from '../../../environments/environment';
import { GnlType } from './lookup.model';

// Grup basina bir kere cekilip (shareReplay) tum caller'lar arasinda paylasilir - dropdown'lari
// dolduran her component kendi HTTP cagrisini tekrar atmaz.
@Injectable({ providedIn: 'root' })
export class LookupService {
  private readonly http = inject(HttpClient);
  private readonly cache = new Map<string, Observable<GnlType[]>>();

  getTypesByGroup(entCodeName: string): Observable<GnlType[]> {
    let cached = this.cache.get(entCodeName);
    if (!cached) {
      cached = this.http
        .get<GnlType[]>(`${environment.apiGatewayUrl}/api/v1/general-types`, { params: { entCodeName } })
        .pipe(shareReplay(1));
      this.cache.set(entCodeName, cached);
    }
    return cached;
  }
}
