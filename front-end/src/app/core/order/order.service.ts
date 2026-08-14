import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  ActiveOfferResponse,
  CreateOrderRequest,
  CustOrdItemResponse,
  OrderConfigurationRequest,
  OrderSummaryResponse,
  ValidateBasketRequest
} from './order.model';

@Injectable({ providedIn: 'root' })
export class OrderService {
  private readonly http = inject(HttpClient);

  // Products/campaigns currently linked to a billing account, sourced from fulfilled order line items.
  getByCustAcctId(custAcctId: number): Observable<CustOrdItemResponse[]> {
    const params = new HttpParams().set('custAcctId', custAcctId);
    return this.http.get<CustOrdItemResponse[]>(`${environment.apiGatewayUrl}/api/v1/orders/by-account`, { params });
  }

  // FR-014 ACC-011/BR-05: Offer Selection'da "Already Active" rozeti icin - hesabin zaten sahip oldugu teklifler.
  getActiveOffers(custAcctId: number): Observable<ActiveOfferResponse[]> {
    const params = new HttpParams().set('custAcctId', custAcctId);
    return this.http.get<ActiveOfferResponse[]>(`${environment.apiGatewayUrl}/api/v1/orders/active-offers`, { params });
  }

  // FR-017: Offer Selection'da Next - Product Configuration'a gecmeden once sepeti dogrular.
  validateBasket(request: ValidateBasketRequest): Observable<void> {
    return this.http.post<void>(`${environment.apiGatewayUrl}/api/v1/orders/validate-basket`, request);
  }

  // FR-012/FR-013: sepet dogrulandiktan sonra siparisi WAIT durumunda acar.
  createOrder(request: CreateOrderRequest): Observable<OrderSummaryResponse> {
    return this.http.post<OrderSummaryResponse>(`${environment.apiGatewayUrl}/api/v1/orders`, request);
  }

  // FR-015: Product Configuration ekraninda karakteristik/adres girildikce (autosave) cagrilir.
  saveConfiguration(custOrdId: number, request: OrderConfigurationRequest): Observable<OrderSummaryResponse> {
    return this.http.put<OrderSummaryResponse>(
      `${environment.apiGatewayUrl}/api/v1/orders/${custOrdId}/configuration`,
      request
    );
  }

  getOrder(custOrdId: number): Observable<OrderSummaryResponse> {
    return this.http.get<OrderSummaryResponse>(`${environment.apiGatewayUrl}/api/v1/orders/${custOrdId}`);
  }

  // FR-021: Review & Confirm'de Finish - WAIT'ten MIDLWARE'e gecirir ve OrderSubmittedEvent'i yayinlar.
  finishOrder(custOrdId: number): Observable<OrderSummaryResponse> {
    return this.http.post<OrderSummaryResponse>(`${environment.apiGatewayUrl}/api/v1/orders/${custOrdId}/finish`, {});
  }

  // Review & Confirm'de Cancel - WAIT'teki siparisi REJECTED'e cevirir.
  cancelOrder(custOrdId: number): Observable<OrderSummaryResponse> {
    return this.http.post<OrderSummaryResponse>(`${environment.apiGatewayUrl}/api/v1/orders/${custOrdId}/cancel`, {});
  }
}
