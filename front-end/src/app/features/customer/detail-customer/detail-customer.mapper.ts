// customer-service is the single front door for customer detail data - it internally proxies
// /individual to party-service and /contact to contact-info-service, so this file only
// talks to data-access/customer types. This mapper just reshapes those responses into the flat
// view-model the detail page renders, keeping that transformation out of the component.
import { AddressResponse, ContactInfo, CustomerDetailResponse, IndividualResponse } from '../data-access/customer';
import { CustOrdItemResponse } from '../data-access/order';

const UNKNOWN = '—';

export interface CustomerDetail {
  customerId: string;
  fullName: string;
  active: boolean;
  accountsCount: number;
  addressCount: number;
  maxAddresses: number;
  primaryCity: string;
  firstName: string;
  middleName: string;
  lastName: string;
  dateOfBirth: string;
  genderId: number;
  fatherName: string;
  motherName: string;
  nationalId: string;
}

export interface AccountProduct {
  productId: string;
  // Backend'de sifirla soldan 6 haneye doldurulmus (CUST_ACCT.ACCT_NO ile ayni kural) - PRD-
  // prefix'i sadece gosterimde eklenir; iliski/routing icin hala productId (gercek PK) kullanilir.
  productNo: string;
  productName: string;
  campaignName: string;
  campaignId: string;
  campaignNo: string;
}

export interface CustomerAccount {
  id: number;
  accountNumber: string;
  accountName: string;
  accountType: string;
  status: string;
  active: boolean;
  addressId: number | null;
  products: AccountProduct[];
}

// order-service'ten gelen fulfilled order line item'larini urun tablosu satirina cevirir
// (bkz. detail-customer.component.ts - her aktif billing account icin ayri cekilir).
export function mapToAccountProducts(items: CustOrdItemResponse[]): AccountProduct[] {
  return items.map(item => ({
    productId: String(item.prodId),
    productNo: item.prodNo,
    productName: item.prodName,
    campaignName: item.cmpgName ?? UNKNOWN,
    campaignId: item.cmpgId !== null ? String(item.cmpgId) : UNKNOWN,
    campaignNo: item.cmpgNo ?? UNKNOWN
  }));
}

export interface CustomerContact {
  email: string;
  mobilePhone: string;
  homePhone: string;
  fax: string;
}

export function mapToCustomerDetail(
  customerDetail: CustomerDetailResponse,
  individual: IndividualResponse,
  addresses: AddressResponse[],
  cityNames: Record<number, string>,
  billingAccountTypeId: number
): CustomerDetail {
  const primaryAddress = addresses.find(address => address.primary) ?? addresses[0];
  const billingAccountsCount = customerDetail.accounts.filter(account => account.accountTpId === billingAccountTypeId).length;

  return {
    // custNo backend'den zaten sifirla soldan doldurulmus gelir (bkz. CustomerMapper.formatCustNo) -
    // CUST- burada sadece gorsel, stored/hesaplanan deger degil.
    customerId: `CUST-${customerDetail.custNo}`,
    fullName: `${individual.firstName} ${individual.lastName}`,
    active: customerDetail.active,
    accountsCount: billingAccountsCount,
    addressCount: addresses.length,
    maxAddresses: 5,
    primaryCity: primaryAddress ? cityNames[primaryAddress.cityId] ?? UNKNOWN : UNKNOWN,
    firstName: individual.firstName,
    middleName: individual.middleName ?? UNKNOWN,
    lastName: individual.lastName,
    dateOfBirth: individual.birthDate,
    genderId: individual.genderId,
    fatherName: individual.fatherName ?? UNKNOWN,
    motherName: individual.motherName ?? UNKNOWN,
    nationalId: individual.nationalId
  };
}

export function mapToCustomerAccounts(customerDetail: CustomerDetailResponse, billingAccountTypeId: number): CustomerAccount[] {
  return customerDetail.accounts
    .filter(account => account.accountTpId === billingAccountTypeId)
    .map(account => ({
      id: account.custAcctId,
      // account.accountNo backend'den zaten sifirla soldan doldurulmus, oneksiz gelir (bkz.
      // AccountDefaults) - ACC- burada da customerId'deki CUST- gibi sadece gorsel.
      accountNumber: `ACC-${account.accountNo}`,
      accountName: account.accountName ?? UNKNOWN,
      accountType: account.accountDesc ?? UNKNOWN,
      status: account.active ? 'Active' : 'Inactive',
      active: account.active,
      addressId: account.addressId,
      products: []
    }));
}

export function mapToCustomerContact(contact: ContactInfo): CustomerContact {
  return {
    email: contact.email,
    mobilePhone: contact.mobilePhone,
    homePhone: contact.homePhone ?? UNKNOWN,
    fax: contact.fax ?? UNKNOWN
  };
}
