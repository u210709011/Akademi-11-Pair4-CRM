import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import { CustomerService } from '../../../core/customer';
import { I18nService } from '../../../core/i18n';
import {
  CustomerAccount,
  CustomerContact,
  CustomerDetail,
  mapToCustomerAccounts,
  mapToCustomerContact,
  mapToCustomerDetail
} from './detail-customer.mapper';

type DetailTab = 'information' | 'accounts' | 'address' | 'contact';

const UNKNOWN = '—';

const EMPTY_CUSTOMER_DETAIL: CustomerDetail = {
  customerId: UNKNOWN,
  fullName: UNKNOWN,
  accountsCount: 0,
  addressCount: 0,
  maxAddresses: 5,
  primaryCity: UNKNOWN,
  firstName: UNKNOWN,
  middleName: UNKNOWN,
  lastName: UNKNOWN,
  dateOfBirth: UNKNOWN,
  gender: UNKNOWN,
  fatherName: UNKNOWN,
  motherName: UNKNOWN,
  nationalId: UNKNOWN
};

const EMPTY_CUSTOMER_CONTACT: CustomerContact = {
  email: UNKNOWN,
  mobilePhone: UNKNOWN,
  homePhone: UNKNOWN,
  fax: UNKNOWN
};

@Component({
  selector: 'app-detail-customer',
  imports: [],
  templateUrl: './detail-customer.component.html',
  styleUrl: './detail-customer.component.scss',
})
export class DetailCustomerComponent {
  protected readonly i18n = inject(I18nService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly customerService = inject(CustomerService);

  protected readonly isLoading = signal(true);
  protected readonly loadError = signal(false);

  protected readonly customer = signal<CustomerDetail>(EMPTY_CUSTOMER_DETAIL);
  protected readonly accounts = signal<CustomerAccount[]>([]);
  protected readonly contact = signal<CustomerContact>(EMPTY_CUSTOMER_CONTACT);

  protected readonly tabs: { key: DetailTab; labelKey: string }[] = [
    { key: 'information', labelKey: 'detail.tabInformation' },
    { key: 'accounts', labelKey: 'detail.tabAccounts' },
    { key: 'address', labelKey: 'detail.tabAddress' },
    { key: 'contact', labelKey: 'detail.tabContact' }
  ];

  protected readonly activeTab = signal<DetailTab>('information');

  // customer-service tek giris noktasi - /individual'i party-service'e, /contact'i contact-info-service'e
  // kendi icinde proxy'liyor, o yuzden ucu de dogrudan custId ile paralel cekilebiliyor.
  constructor() {
    const custId = Number(this.route.snapshot.paramMap.get('custId'));

    forkJoin({
      customerDetail: this.customerService.getById(custId),
      individual: this.customerService.getIndividual(custId),
      contact: this.customerService.getContact(custId)
    }).subscribe({
      next: ({ customerDetail, individual, contact }) => {
        this.customer.set(mapToCustomerDetail(customerDetail, individual));
        this.accounts.set(mapToCustomerAccounts(customerDetail, individual));
        this.contact.set(mapToCustomerContact(contact));
        this.isLoading.set(false);
      },
      error: () => {
        this.loadError.set(true);
        this.isLoading.set(false);
      }
    });
  }

  protected selectTab(tab: DetailTab): void {
    this.activeTab.set(tab);
  }

  protected backToSearch(): void {
    this.router.navigateByUrl('/search-customer');
  }
}
