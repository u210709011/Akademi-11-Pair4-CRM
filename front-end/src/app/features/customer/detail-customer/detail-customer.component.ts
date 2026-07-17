import { Component, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { I18nService } from '../../../core/i18n';

type DetailTab = 'information' | 'accounts' | 'address' | 'contact';

interface CustomerDetail {
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

interface CustomerAccount {
  accountNumber: string;
  accountName: string;
  accountType: string;
  status: string;
}

interface CustomerContact {
  email: string;
  mobilePhone: string;
  homePhone: string;
  fax: string;
}

@Component({
  selector: 'app-detail-customer',
  imports: [],
  templateUrl: './detail-customer.component.html',
  styleUrl: './detail-customer.component.scss',
})
export class DetailCustomerComponent {
  protected readonly i18n = inject(I18nService);
  private readonly router = inject(Router);

  // service gelince bağlanacak 
  protected readonly customer = signal<CustomerDetail>({
    customerId: 'CUST-100234',
    fullName: 'Mert Kaya',
    accountsCount: 1,
    addressCount: 2,
    maxAddresses: 5,
    primaryCity: 'İstanbul',
    firstName: 'Mert',
    middleName: 'Metin',
    lastName: 'Kaya',
    dateOfBirth: '02/11/1996',
    gender: 'Male',
    fatherName: 'Mehmet',
    motherName: 'Dilek',
    nationalId: '12311123111'
  });

  protected readonly accounts = signal<CustomerAccount[]>([
    { accountNumber: 'ACC-2231023', accountName: 'Mert Kaya', accountType: 'Customer Account (223)', status: 'Active' }
  ]);

  protected readonly contact = signal<CustomerContact>({
    email: 'mert.kaya@example.com',
    mobilePhone: '+90 532 123 45 67',
    homePhone: '+90 216 555 00 00',
    fax: ''
  });

  protected readonly tabs: { key: DetailTab; labelKey: string }[] = [
    { key: 'information', labelKey: 'detail.tabInformation' },
    { key: 'accounts', labelKey: 'detail.tabAccounts' },
    { key: 'address', labelKey: 'detail.tabAddress' },
    { key: 'contact', labelKey: 'detail.tabContact' }
  ];

  protected readonly activeTab = signal<DetailTab>('information');

  protected selectTab(tab: DetailTab): void {
    this.activeTab.set(tab);
  }

  protected backToSearch(): void {
    this.router.navigateByUrl('/search-customer');
  }
}
