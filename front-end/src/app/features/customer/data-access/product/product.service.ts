import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
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

// product-service'te arama/filtreleme destekleyen bir GET endpoint yok (sadece getAll/getById),
// bu yuzden Offer Selection ekrani tum listeleri ceker ve filtrelemeyi kendi icinde yapar.
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
    return this.http.get<ProductOffering[]>(`${environment.apiGatewayUrl}/api/v1/product-offerings`);
  }

  getCampaigns(): Observable<Campaign[]> {
    return this.http.get<Campaign[]>(`${environment.apiGatewayUrl}/api/v1/product-campaigns`);
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
