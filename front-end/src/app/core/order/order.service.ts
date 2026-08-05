import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CustOrdItemResponse } from './order.model';

@Injectable({ providedIn: 'root' })
export class OrderService {
  private readonly http = inject(HttpClient);

  // Products/campaigns currently linked to a billing account, sourced from fulfilled order line items.
  getByCustAcctId(custAcctId: number): Observable<CustOrdItemResponse[]> {
    const params = new HttpParams().set('custAcctId', custAcctId);
    return this.http.get<CustOrdItemResponse[]>(`${environment.apiGatewayUrl}/api/v1/orders`, { params });
  }
}
