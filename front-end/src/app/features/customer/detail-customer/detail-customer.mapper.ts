// customer-service is the single front door for customer detail data - it internally proxies
// /individual to party-service and /contact to contact-info-service, so this file only
// talks to core/customer types. This mapper just reshapes those responses into the flat
// view-model the detail page renders, keeping that transformation out of the component.
import { AddressResponse, ContactInfo, CustomerDetailResponse, IndividualResponse } from '../../../core/customer';

const UNKNOWN = '—';

// lookup-service CITY grubunda seed'de tek deger var: 201=Ankara.
export const CITY_NAMES: Record<number, string> = { 201: 'Ankara' };

// lookup-service gnl_tp (ent_code_name=ACCOUNT_TYPE): 223=Musteri Hesap (CUST_ACCT), 224=Fatura Hesap (BILL_ACCT).
// Billing Accounts tab'i sadece gercek fatura hesaplarini (224) gosterir.
const BILLING_ACCOUNT_TYPE_ID = 224;

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
  productName: string;
  campaignName: string;
  campaignId: string;
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

// product-service entegrasyonu henuz yok (bkz. detail-customer.component.ts) - UI'in urun
// tablosunu gosterebilmesi icin aktif hesaplara sabit ornek urunler atanir.
const MOCK_PRODUCTS: AccountProduct[] = [
  { productId: 'PRD-10023', productName: 'Mobile Postpaid 30GB', campaignName: 'Back to School Bundle', campaignId: 'CMP-9010' },
  { productId: 'PRD-10031', productName: 'Home Fiber 100Mbps', campaignName: 'Fiber Loyalty', campaignId: 'CMP-2077' }
];

export interface CustomerContact {
  email: string;
  mobilePhone: string;
  homePhone: string;
  fax: string;
}

export function mapToCustomerDetail(
  customerDetail: CustomerDetailResponse,
  individual: IndividualResponse,
  addresses: AddressResponse[]
): CustomerDetail {
  const primaryAddress = addresses.find(address => address.primary) ?? addresses[0];
  const billingAccountsCount = customerDetail.accounts.filter(account => account.accountTpId === BILLING_ACCOUNT_TYPE_ID).length;

  return {
    customerId: `CUST-${customerDetail.custId}`,
    fullName: `${individual.firstName} ${individual.lastName}`,
    active: customerDetail.active,
    accountsCount: billingAccountsCount,
    addressCount: addresses.length,
    maxAddresses: 5,
    primaryCity: primaryAddress ? CITY_NAMES[primaryAddress.cityId] ?? UNKNOWN : UNKNOWN,
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

export function mapToCustomerAccounts(customerDetail: CustomerDetailResponse): CustomerAccount[] {
  return customerDetail.accounts
    .filter(account => account.accountTpId === BILLING_ACCOUNT_TYPE_ID)
    .map(account => ({
      id: account.custAcctId,
      accountNumber: account.accountNo,
      accountName: account.accountName ?? UNKNOWN,
      accountType: account.accountDesc ?? UNKNOWN,
      status: account.active ? 'Active' : 'Inactive',
      active: account.active,
      addressId: account.addressId,
      products: account.active ? MOCK_PRODUCTS : []
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
