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
  // Backend'de sifirla soldan 6 haneye doldurulmus (ör. "000042") - CUST_ACCT.ACCT_NO ile ayni
  // kural (bkz. ProductServiceDefaults.formatNo). Prefix (OFR-) sadece burada, gorunumde eklenir.
  productOfferingNo: string;
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
  campaignNo: string;
  name: string;
  descr: string;
  campaignCode: string;
  activityEndDate: string | null;
  statusId: number;
  penalty: boolean;
}

// GET /api/v1/campaign-offerings - kampanya <-> teklif eslemesi; productOfferingName donmus haliyle var,
// ama fiyat icin yine GET /api/v1/product-offerings ile birlestirmek gerekiyor.
export interface CampaignOffering {
  campaignOfferingId: number;
  campaignId: number;
  productOfferingId: number;
  productOfferingName: string;
  priority: number;
  // Kampanyanin bu teklife uyguladigi indirim - discountedPrice, offering'in totalPrice'indan
  // hesaplanmis (discountPct%) net fiyat, backend'de order-service da ayni degeri kullanir.
  discountPct: number;
  discountedPrice: number;
  startDate: string | null;
  endDate: string | null;
  active: boolean;
}

// GET /api/v1/product-offering-relations/by-offering/{id} - bir offering'in zorunlu/opsiyonel
// bagli oldugu diger offering'ler (orn. Home Fiber 200Mbps -> Broadband Modem, mandatory=true).
export interface ProductOfferingRelation {
  productOfferingRelationId: number;
  productOfferingId1: number;
  productOfferingId2: number;
  relationTypeId: number;
  mandatory: boolean;
  // FR-014 ACC-012/BR-04: true ise bu iki offering sepette es zamanli olamaz (EXCL).
  exclusive: boolean;
  qty: number;
  active: boolean;
}

// GET /api/v1/product-offering-char-uses/by-offering/{id} - bir offering'in Configuration
// adiminda gosterilmesi gereken karakteristik semasi (hangi GNL_CHAR'lar, zorunlu mu).
export interface ProductOfferingCharUse {
  productOfferingCharUseId: number;
  productOfferingId: number;
  characteristicId: number;
  characteristicName: string;
  mandatory: boolean;
  active: boolean;
}

// GET /api/v1/products/{productId} - siparis tamamlandiginda (finishOrder -> provisionProducts)
// gercekten provizyon edilmis urun ornegi. Product Offer Details modali icin (bkz. detail-customer).
export interface Product {
  productId: number;
  productNo: string;
  parentProductId: number | null;
  productOfferingId: number;
  productOfferingName: string;
  productSpecId: number;
  name: string;
  descr: string;
  campaignId: number | null;
  campaignName: string | null;
  statusId: number;
  serviceStartDate: string | null;
}

// GET /api/v1/product-characteristic-values/by-product/{productId} - siparis Configuration
// adiminda secilmis, provizyon sirasinda urune islenmis karakteristik degerleri.
export interface ProductCharacteristicValue {
  productCharacteristicValueId: number;
  characteristicName: string;
  productId: number;
  characteristicId: number;
  characteristicValueId: number | null;
  characteristicValueName: string | null;
  value: string | null;
  statusId: number;
}
