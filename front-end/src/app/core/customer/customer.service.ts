import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CustomerDetailResponse, CustomerSearchCriteria, CustomerSearchResult } from './customer.model';
// to align with the object logic implemented on the backend.
interface PagedResponse<T> {
  content: T[];
}

@Injectable({ providedIn: 'root' })
export class CustomerService {
  private readonly http = inject(HttpClient);

  search(criteria: CustomerSearchCriteria): Observable<CustomerSearchResult[]> {
    let params = new HttpParams();

    for (const [key, value] of Object.entries(criteria)) {
      if (value) {
        params = params.set(key, value);
      }
    }

    return this.http
      .get<PagedResponse<CustomerSearchResult>>(`${environment.apiGatewayUrl}/api/v1/customers/search`, {
        params
      })
      .pipe(map(response => response.content));
  }
  //get result by customer id, connects to the detail-customer page
  getById(custId: number): Observable<CustomerDetailResponse> {
    return this.http.get<CustomerDetailResponse>(`${environment.apiGatewayUrl}/api/v1/customers/${custId}`);
  }
}
