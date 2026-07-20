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

// onboarding/verify-identity request body - birthDate must be "dd/MM/yyyy".
export interface IndividualInfo {
  firstName: string;
  middleName: string | null;
  lastName: string;
  birthDate: string;
  genderId: number;
  motherName: string | null;
  fatherName: string | null;
  nationalId: string;
}

export interface IdentityVerificationResponse {
  verified: boolean;
  message: string;
}

// GET /api/v1/customers/{custId}/individual - customer-service internally proxies this to party-service.
export interface IndividualResponse {
  firstName: string;
  middleName: string | null;
  lastName: string;
  birthDate: string;
  genderId: number;
  motherName: string | null;
  fatherName: string | null;
  nationalId: string;
}

// GET /api/v1/customers/{custId}/contact - customer-service internally proxies this to contact-info-service.
export interface ContactInfo {
  email: string;
  mobilePhone: string;
  homePhone: string | null;
  fax: string | null;
}
