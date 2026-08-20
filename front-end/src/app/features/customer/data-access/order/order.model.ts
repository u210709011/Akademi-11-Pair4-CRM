// GET /api/v1/orders?custAcctId={custAcctId} response item - order-service.
// One row per fulfilled order line item currently linked to a billing account.
export interface CustOrdItemResponse {
  custOrdItemId: number;
  prodId: number;
  prodNo: string;
  prodName: string;
  cmpgId: number | null;
  cmpgNo: string | null;
  cmpgName: string | null;
  custAcctId: number;
}

// GET /api/v1/orders/active-offers?custAcctId= response item - FR-014 ACC-011/BR-05: hesabin
// zaten aktif (PROCESSING/FINISHED bir siparisten) sahip oldugu teklif.
export interface ActiveOfferResponse {
  prodOfrId: number;
  custOrdItemId: number;
  prodId: number;
}

// New Sale wizard - Offer Selection basket item, sent inside ValidateBasketRequest/CreateOrderRequest.
export interface BasketItemRequest {
  prodOfrId: number;
  cmpgId: number | null;
  charVals: ProdCharValRequest[];
}

export interface ProdCharValRequest {
  charId: number;
  charValId: number | null;
  val: string | null;
}

// POST /api/v1/orders/validate-basket - FR-017, Offer Selection'da Next'te sepeti dogrular.
export interface ValidateBasketRequest {
  custId: number;
  custAcctId: number;
  items: BasketItemRequest[];
}

// POST /api/v1/orders - sepet dogrulandiktan sonra siparisi WAIT durumunda acar.
export interface CreateOrderRequest {
  custId: number;
  custAcctId: number;
  items: BasketItemRequest[];
}

// PUT /api/v1/orders/{custOrdId}/configuration body - Product Configuration adiminda autosave.
export interface AddressInfoRequest {
  cityId: number;
  streetName: string;
  buildingName: string;
  addressDesc: string;
}

// Configuration ekraninda basket item basina bir kart var, her biri kendi karakteristik setini tasir.
export interface ItemCharValsRequest {
  custOrdItemId: number;
  charVals: ProdCharValRequest[];
}

export interface OrderConfigurationRequest {
  items: ItemCharValsRequest[];
  addressId: number | null;
  newAddress: AddressInfoRequest | null;
}

export interface ProdCharValResponse {
  charId: number;
  charValId: number | null;
  val: string | null;
}

export interface OrderItemSummaryResponse {
  custOrdItemId: number;
  prodId: number;
  prodOfrId: number;
  ofrName: string;
  prodName: string;
  cmpgId: number | null;
  cmpgName: string | null;
  price: number;
  charVals: ProdCharValResponse[];
}

export interface AddressSummaryResponse {
  addressId: number;
  cityId: number;
  streetName: string;
  buildingName: string;
  addressDesc: string;
}

// POST/PUT /api/v1/orders(/{custOrdId}/...) response - Review & Confirm ekraninin ana veri kaynagi.
export interface OrderSummaryResponse {
  custOrdId: number;
  bsnInterId: number;
  ordStId: number;
  items: OrderItemSummaryResponse[];
  serviceAddress: AddressSummaryResponse | null;
  totalAmount: number;
}
