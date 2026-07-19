// customer-service is the single front door for customer detail data - it internally proxies
// /individual to party-service and /contact to contact-info-service, so this file only
// talks to core/customer types. This mapper just reshapes those responses into the flat
// view-model the detail page renders, keeping that transformation out of the component.
import { ContactInfo, CustomerDetailResponse, IndividualResponse } from '../../../core/customer';

const UNKNOWN = '—';

export interface CustomerDetail {
  customerId: string;
  fullName: string;
  accountsCount: number;
  addressCount: number;
  maxAddresses: number;
  primaryCity: string;
  firstName: string;
  middleName: string;
  lastName: string;
  dateOfBirth: string;
  gender: string;
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

export function mapToCustomerDetail(customerDetail: CustomerDetailResponse, individual: IndividualResponse): CustomerDetail {
  return {
    customerId: `CUST-${customerDetail.custId}`,
    fullName: `${individual.firstName} ${individual.lastName}`,
    accountsCount: customerDetail.accounts.length,
    addressCount: 0,
    maxAddresses: 5,
    primaryCity: UNKNOWN,
    firstName: individual.firstName,
    middleName: individual.middleName ?? UNKNOWN,
    lastName: individual.lastName,
    dateOfBirth: individual.birthDate,
    gender: `Gender #${individual.genderId}`,
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
