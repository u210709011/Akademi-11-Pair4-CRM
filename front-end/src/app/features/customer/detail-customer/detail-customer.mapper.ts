// This mapper combines customer-servie and party-service results and shows in the detail page,
// so the component doesn't have to know or care that the data came from two services.
import { CustomerDetailResponse } from '../../../core/customer';
import { IndividualResponse } from '../../../core/party';

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
