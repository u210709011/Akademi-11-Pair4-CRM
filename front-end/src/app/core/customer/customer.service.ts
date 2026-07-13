import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { CustomerSearchCriteria, CustomerSearchResult } from './customer.model';

// customer-service'in lokal fallback portu (server.port: 8081, application.yml).
// Gateway routing netlesince bu, api-gateway URL'ine cevrilecek.
const CUSTOMER_SERVICE_BASE_URL = 'http://localhost:8081';

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

    return this.http.get<CustomerSearchResult[]>(`${CUSTOMER_SERVICE_BASE_URL}/api/v1/customers/search`, {
      params
    });
  }
}
