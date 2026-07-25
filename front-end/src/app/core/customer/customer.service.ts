import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  AddressEditRequest,
  AddressResponse,
  ContactInfo,
  CustomerDetailResponse,
  CustomerSearchCriteria,
  CustomerSearchResult,
  IdentityVerificationResponse,
  IndividualInfo,
  IndividualResponse,
  OnboardCustomerRequest,
  OnboardCustomerResponse
} from './customer.model';
// to align with the object logic implemented on the backend (Spring Data Page).
interface PagedResponse<T> {
  content: T[];
  totalElements: number;
}

export interface CustomerSearchPage {
  results: CustomerSearchResult[];
  totalElements: number;
}

@Injectable({ providedIn: 'root' })
export class CustomerService {
  private readonly http = inject(HttpClient);

  // page/size: FR-002 ilk sayfada 10 kayit gosterir (bkz. search-customer.component.ts), kalani sayfalama ile.
  search(criteria: CustomerSearchCriteria, page: number, size: number): Observable<CustomerSearchPage> {
    let params = new HttpParams().set('page', page).set('size', size);

    for (const [key, value] of Object.entries(criteria)) {
      if (value) {
        params = params.set(key, value);
      }
    }

    return this.http
      .get<PagedResponse<CustomerSearchResult>>(`${environment.apiGatewayUrl}/api/v1/customers/search`, {
        params
      })
      .pipe(map(response => ({ results: response.content, totalElements: response.totalElements })));
  }
  //get result by customer id, connects to the detail-customer page
  getById(custId: number): Observable<CustomerDetailResponse> {
    return this.http.get<CustomerDetailResponse>(`${environment.apiGatewayUrl}/api/v1/customers/${custId}`);
  }

  // KPS identity check, does not persist anything, just validates before moving to the next tab.
  verifyIdentity(individual: IndividualInfo): Observable<IdentityVerificationResponse> {
    return this.http.post<IdentityVerificationResponse>(
      `${environment.apiGatewayUrl}/api/v1/customers/onboarding/verify-identity`,
      individual
    );
  }

  // customer-service proxies this to party-service internally - frontend never calls party-service directly.
  getIndividual(custId: number): Observable<IndividualResponse> {
    return this.http.get<IndividualResponse>(`${environment.apiGatewayUrl}/api/v1/customers/${custId}/individual`);
  }

  // FR-004: Customer Info Update. Backend expects the same shape as onboarding's IndividualInfo.
  updateIndividual(custId: number, individual: IndividualInfo): Observable<IndividualResponse> {
    return this.http.put<IndividualResponse>(
      `${environment.apiGatewayUrl}/api/v1/customers/${custId}/individual`,
      individual
    );
  }

  // customer-service proxies this to contact-info-service internally.
  getContact(custId: number): Observable<ContactInfo> {
    return this.http.get<ContactInfo>(`${environment.apiGatewayUrl}/api/v1/customers/${custId}/contact`);
  }

  // FR-005: customer-service proxies this to contact-info-service internally (max 5 per customer).
  getAddresses(custId: number): Observable<AddressResponse[]> {
    return this.http.get<AddressResponse[]>(`${environment.apiGatewayUrl}/api/v1/customers/${custId}/addresses`);
  }

  // FR-005: adds a new address. Backend enforces the max-5-per-customer rule (409 on the 6th).
  addAddress(custId: number, request: AddressEditRequest): Observable<AddressResponse> {
    return this.http.post<AddressResponse>(
      `${environment.apiGatewayUrl}/api/v1/customers/${custId}/addresses`,
      request
    );
  }

  // FR-005: updates an existing address, including making it primary.
  updateAddress(custId: number, addressId: number, request: AddressEditRequest): Observable<AddressResponse> {
    return this.http.put<AddressResponse>(
      `${environment.apiGatewayUrl}/api/v1/customers/${custId}/addresses/${addressId}`,
      request
    );
  }

  // FR-005: deletes an address. Backend returns 409 if the address is primary or linked to a billing account.
  deleteAddress(custId: number, addressId: number): Observable<void> {
    return this.http.delete<void>(`${environment.apiGatewayUrl}/api/v1/customers/${custId}/addresses/${addressId}`);
  }

  // ACC-023: Create butonu - tek istekte party+customer(+hesap)+contact/adres yazar (saga, backend tarafında geri alinir).
  onboard(request: OnboardCustomerRequest): Observable<OnboardCustomerResponse> {
    return this.http.post<OnboardCustomerResponse>(`${environment.apiGatewayUrl}/api/v1/customers/onboarding`, request);
  }

  // FR-007: musteri + hesaplarini soft-delete eder. Aktif hesap guard'i backend'de henuz yok (bkz. backend notlari).
  deleteCustomer(custId: number): Observable<void> {
    return this.http.delete<void>(`${environment.apiGatewayUrl}/api/v1/customers/${custId}`);
  }
}
