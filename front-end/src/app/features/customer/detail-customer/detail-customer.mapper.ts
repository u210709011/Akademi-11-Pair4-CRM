// customer-service is the single front door for customer detail data - it internally proxies
// /individual to party-service and /contact to contact-info-service, so this file only
// talks to core/customer types. This mapper just reshapes those responses into the flat
// view-model the detail page renders, keeping that transformation out of the component.
import { AddressResponse, ContactInfo, CustomerDetailResponse, IndividualResponse } from '../../../core/customer';

const UNKNOWN = '—';

// lookup-service CITY grubunda seed'de tek deger var: 201=Ankara.
export const CITY_NAMES: Record<number, string> = { 201: 'Ankara' };

// GET /individual birthDate'i ISO (yyyy-MM-dd) formatinda donuyor, ekranda dd/MM/yyyy gosterilir.
function formatBirthDate(isoDate: string): string {
  const [year, month, day] = isoDate.split('-');
  return `${day}/${month}/${year}`;
}

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

export interface CustomerAccount {
  accountNumber: string;
  accountName: string;
  accountType: string;
  status: string;
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
  addresses: AddressResponse[]
): CustomerDetail {
  const primaryAddress = addresses.find(address => address.primary) ?? addresses[0];

  return {
    customerId: `CUST-${customerDetail.custId}`,
    fullName: `${individual.firstName} ${individual.lastName}`,
    active: customerDetail.active,
    accountsCount: customerDetail.accounts.length,
    addressCount: addresses.length,
    maxAddresses: 5,
    primaryCity: primaryAddress ? CITY_NAMES[primaryAddress.cityId] ?? UNKNOWN : UNKNOWN,
    firstName: individual.firstName,
    middleName: individual.middleName ?? UNKNOWN,
    lastName: individual.lastName,
    dateOfBirth: formatBirthDate(individual.birthDate),
    genderId: individual.genderId,
    fatherName: individual.fatherName ?? UNKNOWN,
    motherName: individual.motherName ?? UNKNOWN,
    nationalId: individual.nationalId
  };
}

export function mapToCustomerAccounts(customerDetail: CustomerDetailResponse, individual: IndividualResponse): CustomerAccount[] {
  return customerDetail.accounts.map(account => ({
    accountNumber: account.accountNo,
    accountName: account.accountName ?? `${individual.firstName} ${individual.lastName}`,
    accountType: account.accountDesc ?? UNKNOWN,
    status: account.active ? 'Active' : 'Inactive'
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
