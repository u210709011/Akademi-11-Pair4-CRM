export interface CustomerSearchCriteria {
  firstName: string;
  lastName: string;
  tcNo: string;
  acctNo: string;
}

export interface CustomerSearchResult {
  custId: number;
  firstName: string;
  lastName: string;
  tcNo: string;
  acctNo: string;
  status: string;
}
