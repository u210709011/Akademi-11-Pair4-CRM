import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import { environment } from '../../../../../environments/environment';
import {
  Campaign,
  CampaignOffering,
  Product,
  ProductCatalog,
  ProductCatalogOffering,
  ProductCharacteristicValue,
  ProductOffering,
  ProductOfferingCharUse,
  ProductOfferingRelation
} from './product.model';

// bkz. customer.service.ts PagedResponse - ayni Spring Data Page kontratina karsilik gelir.
interface PagedResponse<T> {
  content: T[];
}

// FR-013: /product-offerings ve /product-campaigns artik sunucu tarafinda sayfalanip
// filtrelenebiliyor (id/name query param + page/size), ama Offer Selection ekrani hala tum
// listeyi tek seferde cekip aramayi/sayfalamayi (RESULTS_PAGE_SIZE=5) kendi icinde yapiyor -
// o yuzden buyuk bir size ile "tumunu getir" davranisi simule edilir (bkz. order-service'in
// customerClient.getAccounts(custId, 1000) ile ayni desen).
const FETCH_ALL_SIZE = 1000;

@Injectable({ providedIn: 'root' })
export class ProductService {
  private readonly http = inject(HttpClient);

  getCatalogs(): Observable<ProductCatalog[]> {
    return this.http.get<ProductCatalog[]>(`${environment.apiGatewayUrl}/api/v1/product-catalogs`);
  }

  getCatalogOfferings(): Observable<ProductCatalogOffering[]> {
    return this.http.get<ProductCatalogOffering[]>(`${environment.apiGatewayUrl}/api/v1/product-catalog-offerings`);
  }

  getOfferings(): Observable<ProductOffering[]> {
    return this.http
      .get<PagedResponse<ProductOffering>>(`${environment.apiGatewayUrl}/api/v1/product-offerings`, {
        params: new HttpParams().set('size', FETCH_ALL_SIZE)
      })
      .pipe(map(response => response.content));
  }

  getCampaigns(): Observable<Campaign[]> {
    return this.http
      .get<PagedResponse<Campaign>>(`${environment.apiGatewayUrl}/api/v1/product-campaigns`, {
        params: new HttpParams().set('size', FETCH_ALL_SIZE)
      })
      .pipe(map(response => response.content));
  }

  getCampaignOfferings(): Observable<CampaignOffering[]> {
    return this.http.get<CampaignOffering[]>(`${environment.apiGatewayUrl}/api/v1/campaign-offerings`);
  }

  // Tum offering-offering iliskileri (orn. Fiber -> Modem, zorunlu/opsiyonel) - New Sale
  // Offer Selection ekrani sepete eklerken zorunlu urunleri otomatik eklemek icin bunu bir kere ceker.
  getOfferingRelations(): Observable<ProductOfferingRelation[]> {
    return this.http.get<ProductOfferingRelation[]>(`${environment.apiGatewayUrl}/api/v1/product-offering-relations`);
  }

  // Bir offering'in Configuration adiminda gosterilmesi gereken karakteristik semasi.
  getCharUsesByOffering(productOfferingId: number): Observable<ProductOfferingCharUse[]> {
    return this.http.get<ProductOfferingCharUse[]>(
      `${environment.apiGatewayUrl}/api/v1/product-offering-char-uses/by-offering/${productOfferingId}`
    );
  }

  // Product Offer Details modali (bkz. detail-customer) - provizyon edilmis gercek urun ornegi.
  getById(productId: number): Observable<Product> {
    return this.http.get<Product>(`${environment.apiGatewayUrl}/api/v1/products/${productId}`);
  }

  getCharacteristicsByProductId(productId: number): Observable<ProductCharacteristicValue[]> {
    return this.http.get<ProductCharacteristicValue[]>(
      `${environment.apiGatewayUrl}/api/v1/product-characteristic-values/by-product/${productId}`
    );
  }
}
