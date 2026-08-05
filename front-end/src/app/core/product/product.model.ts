// GET /api/v1/product-catalogs - katalog listesi (Mobile/Internet/TV gibi kategoriler).
export interface ProductCatalog {
  productCatalogId: number;
  name: string;
  descr: string;
  statusId: number;
  shortCode: string | null;
}

// GET /api/v1/products/{productOfferingId} icindeki teklif - offer selection tablosunda gosterilir.
export interface ProductOffering {
  productOfferingId: number;
  productSpecId: number;
  name: string;
  descr: string;
  parentOfferingId: number | null;
  statusId: number;
  totalPrice: number;
}

// GET /api/v1/product-catalog-offerings - katalog <-> teklif eslemesi, sadece ID'ler donuyor,
// offering detaylari (isim/fiyat) ayrica GET /api/v1/product-procutOfferings ile birlestirilir.
export interface ProductCatalogOffering {
  productCatalogOfferingId: number;
  productCatalogId: number;
  productOfferingId: number;
  statusId: number;
}

// GET /api/v1/product-campaigns - kampanya listesi.
export interface Campaign {
  campaignId: number;
  name: string;
  descr: string;
  campaignCode: string;
  activityEndDate: string | null;
  statusId: number;
  penalty: boolean;
}

// GET /api/v1/campaign-offerings - kampanya <-> teklif eslemesi; productOfferingName donmus haliyle var,
// ama fiyat icin yine GET /api/v1/product-procutOfferings ile birlestirmek gerekiyor.
export interface CampaignOffering {
  campaignOfferingId: number;
  campaignId: number;
  productOfferingId: number;
  productOfferingName: string;
  priority: number;
  startDate: string | null;
  endDate: string | null;
  active: boolean;
}
