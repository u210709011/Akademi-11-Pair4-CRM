export interface CustomerSearchCriteria {
  firstName: string;
  lastName: string;
  tcNo: string;
  acctNo: string;
  custId: string;
  gsm: string;
}

export interface CustomerSearchResult {
  custId: number;
  firstName: string;
  middleName: string | null;
  lastName: string;
  tcNo: string;
  acctNo: string;
  role: string | null;
  gsm: string | null;
  status: string;
}
//interfaces for party service response
export interface CustomerAccountSummary {
  custAcctId: number;
  accountNo: string;
  accountName: string | null;
  accountDesc: string | null;
  accountTpId: number | null;
  addressId: number | null;
  active: boolean;
}

export interface CustomerDetailResponse {
  custId: number;
  partyRoleId: number;
  custTpId: number | null;
  active: boolean;
  accounts: CustomerAccountSummary[];
}
