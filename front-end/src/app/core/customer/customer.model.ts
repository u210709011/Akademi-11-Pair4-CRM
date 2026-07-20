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
