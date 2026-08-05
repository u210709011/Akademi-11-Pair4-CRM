// GET /api/v1/orders?custAcctId={custAcctId} response item - order-service.
// One row per fulfilled order line item currently linked to a billing account.
export interface CustOrdItemResponse {
  custOrdItemId: number;
  prodId: number;
  prodName: string;
  cmpgId: number | null;
  cmpgName: string | null;
  custAcctId: number;
}
