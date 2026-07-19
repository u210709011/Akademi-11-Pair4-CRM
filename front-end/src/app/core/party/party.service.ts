import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { IndividualResponse } from './party.model';

@Injectable({ providedIn: 'root' })
export class PartyService {
  private readonly http = inject(HttpClient);

  getIndividualByPartyRole(partyRoleId: number): Observable<IndividualResponse> {
    return this.http.get<IndividualResponse>(
      `${environment.apiGatewayUrl}/api/v1/individuals/by-party-role/${partyRoleId}`
    );
  }
}
