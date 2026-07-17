import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CustomerSearchCriteria, CustomerSearchResult } from './customer.model';

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

    return this.http.get<CustomerSearchResult[]>(`${environment.customerServiceUrl}/api/v1/customers/search`, {
      params
    });
  }
}
