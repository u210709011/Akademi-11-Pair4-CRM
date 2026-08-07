import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Campaign, CampaignOffering, ProductCatalog, ProductCatalogOffering, ProductOffering } from './product.model';

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
    return this.http.get<ProductOffering[]>(`${environment.apiGatewayUrl}/api/v1/product-procutOfferings`);
  }

  getCampaigns(): Observable<Campaign[]> {
    return this.http.get<Campaign[]>(`${environment.apiGatewayUrl}/api/v1/product-campaigns`);
  }

  getCampaignOfferings(): Observable<CampaignOffering[]> {
    return this.http.get<CampaignOffering[]>(`${environment.apiGatewayUrl}/api/v1/campaign-offerings`);
  }
}
