import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin, of, switchMap } from 'rxjs';
import { CustomerService } from '../../../core/customer';
import { I18nService } from '../../../core/i18n';
import { PartyService } from '../../../core/party';
import { CustomerAccount, CustomerDetail, mapToCustomerAccounts, mapToCustomerDetail } from './detail-customer.mapper';

type DetailTab = 'information' | 'accounts' | 'address' | 'contact';

const UNKNOWN = '—';

interface CustomerContact {
  email: string;
  mobilePhone: string;
  homePhone: string;
  fax: string;
}

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
  private readonly partyService = inject(PartyService);

  protected readonly isLoading = signal(true);
  protected readonly loadError = signal(false);

  protected readonly customer = signal<CustomerDetail>(EMPTY_CUSTOMER_DETAIL);
  protected readonly accounts = signal<CustomerAccount[]>([]);

  // e-posta/telefon henuz contact-info-service'ten cekilmiyor - o entegrasyon gelince eklenecek.
  protected readonly contact = signal<CustomerContact>({
    email: UNKNOWN,
    mobilePhone: UNKNOWN,
    homePhone: UNKNOWN,
    fax: UNKNOWN
  });

  protected readonly tabs: { key: DetailTab; labelKey: string }[] = [
    { key: 'information', labelKey: 'detail.tabInformation' },
    { key: 'accounts', labelKey: 'detail.tabAccounts' },
    { key: 'address', labelKey: 'detail.tabAddress' },
    { key: 'contact', labelKey: 'detail.tabContact' }
  ];

  protected readonly activeTab = signal<DetailTab>('information');

  // cust id result combines with party role id result and shows combined results in detail-customer page
  constructor() {
    const custId = Number(this.route.snapshot.paramMap.get('custId'));

    this.customerService
      .getById(custId)
      .pipe(
        switchMap(customerDetail =>
          forkJoin({
            customerDetail: of(customerDetail),
            individual: this.partyService.getIndividualByPartyRole(customerDetail.partyRoleId)
          })
        )
      )
      .subscribe({
        next: ({ customerDetail, individual }) => {
          this.customer.set(mapToCustomerDetail(customerDetail, individual));
          this.accounts.set(mapToCustomerAccounts(customerDetail, individual));
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
