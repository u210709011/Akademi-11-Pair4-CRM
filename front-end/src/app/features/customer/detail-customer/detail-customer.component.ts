import { Component, HostListener, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { form, FormField, required } from '@angular/forms/signals';
import {
  AddressEditRequest,
  AddressResponse,
  CustomerDetailResponse,
  CustomerService,
  IndividualResponse
} from '../../../core/customer';
import { I18nService } from '../../../core/i18n';
import {
  CITY_NAMES,
  CustomerAccount,
  CustomerContact,
  CustomerDetail,
  mapToCustomerAccounts,
  mapToCustomerContact,
  mapToCustomerDetail
} from './detail-customer.mapper';

interface AddressFormModel {
  city: string;
  street: string;
  houseNumber: string;
  description: string;
  details: string;
}

const EMPTY_ADDRESS_FORM: AddressFormModel = { city: '', street: '', houseNumber: '', description: '', details: '' };

// Backend'in addrDesc alani tek bir string - "Address Name" ve "Address Details" iki ayri
// form alani olsa da tek alanda bu ayirici ile birlestirilip saklanir, duzenlemede geri ayrilir.
const ADDRESS_DESC_SEPARATOR = ' | ';

function splitAddressDesc(addrDesc: string): { description: string; details: string } {
  const separatorIndex = addrDesc.indexOf(ADDRESS_DESC_SEPARATOR);
  return separatorIndex === -1
    ? { description: addrDesc, details: '' }
    : { description: addrDesc.slice(0, separatorIndex), details: addrDesc.slice(separatorIndex + ADDRESS_DESC_SEPARATOR.length) };
}

type DetailTab = 'information' | 'accounts' | 'address' | 'contact';

const UNKNOWN = '—';

const EMPTY_CUSTOMER_DETAIL: CustomerDetail = {
  customerId: UNKNOWN,
  fullName: UNKNOWN,
  active: false,
  accountsCount: 0,
  addressCount: 0,
  maxAddresses: 5,
  primaryCity: UNKNOWN,
  firstName: UNKNOWN,
  middleName: UNKNOWN,
  lastName: UNKNOWN,
  dateOfBirth: UNKNOWN,
  genderId: 0,
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
  imports: [RouterLink, FormField],
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
  protected readonly addresses = signal<AddressResponse[]>([]);
  protected readonly maxAddresses = 5;
  protected readonly openAddressMenuId = signal<number | null>(null);

  protected readonly isAddressModalOpen = signal(false);
  protected readonly editingAddressId = signal<number | null>(null);
  protected readonly isSavingAddress = signal(false);
  protected readonly addressSaveError = signal<string | null>(null);
  protected readonly addressActionError = signal<string | null>(null);

  protected readonly addressModel = signal<AddressFormModel>({ ...EMPTY_ADDRESS_FORM });

  protected readonly addressForm = form(this.addressModel, path => {
    required(path.city);
    required(path.street);
    required(path.houseNumber);
    required(path.description);
    required(path.details);
  });

  protected readonly tabs: { key: DetailTab; labelKey: string }[] = [
    { key: 'information', labelKey: 'detail.tabInformation' },
    { key: 'accounts', labelKey: 'detail.tabAccounts' },
    { key: 'address', labelKey: 'detail.tabAddress' },
    { key: 'contact', labelKey: 'detail.tabContact' }
  ];

  protected readonly activeTab = signal<DetailTab>('information');

  private readonly custId = Number(this.route.snapshot.paramMap.get('custId'));

  // adres eklendiginde/guncellendiginde customer()'i (addressCount/primaryCity) yeniden hesaplamak icin saklanir.
  private customerDetailResponse!: CustomerDetailResponse;
  private individualResponse!: IndividualResponse;

  // customer-service tek giris noktasi - /individual'i party-service'e, /contact'i contact-info-service'e
  // kendi icinde proxy'liyor, o yuzden ucu de dogrudan custId ile paralel cekilebiliyor.
  constructor() {
    forkJoin({
      customerDetail: this.customerService.getById(this.custId),
      individual: this.customerService.getIndividual(this.custId),
      contact: this.customerService.getContact(this.custId),
      addresses: this.customerService.getAddresses(this.custId)
    }).subscribe({
      next: ({ customerDetail, individual, contact, addresses }) => {
        this.customerDetailResponse = customerDetail;
        this.individualResponse = individual;
        this.customer.set(mapToCustomerDetail(customerDetail, individual, addresses));
        this.accounts.set(mapToCustomerAccounts(customerDetail, individual));
        this.contact.set(mapToCustomerContact(contact));
        this.addresses.set(addresses);
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

  protected goToUpdate(): void {
    this.router.navigate(['/detail-customer', this.custId, 'update']);
  }

  protected genderLabel(genderId: number): string {
    return genderId === 1 ? this.i18n.t('create.genderMale') : this.i18n.t('create.genderFemale');
  }

  protected cityName(cityId: number): string {
    return CITY_NAMES[cityId] ?? UNKNOWN;
  }

  protected addressName(addrDesc: string): string {
    return splitAddressDesc(addrDesc).description;
  }

  protected toggleAddressMenu(addressId: number, event: Event): void {
    event.stopPropagation();
    this.openAddressMenuId.set(this.openAddressMenuId() === addressId ? null : addressId);
  }

  @HostListener('document:click')
  protected closeAddressMenu(): void {
    this.openAddressMenuId.set(null);
  }

  protected linkedAccountCount(addressId: number): number {
    return this.customerDetailResponse.accounts.filter(account => account.addressId === addressId).length;
  }

  protected linkedAccountLabel(addressId: number): string {
    return this.i18n.t('detail.linkedToBillingAccount').replace('{count}', String(this.linkedAccountCount(addressId)));
  }

  protected setAsPrimary(address: AddressResponse): void {
    this.openAddressMenuId.set(null);
    this.addressActionError.set(null);

    const request: AddressEditRequest = {
      cityId: address.cityId,
      streetName: address.streetName,
      buildingName: address.houseName,
      addressDesc: address.addrDesc,
      primary: true
    };

    this.customerService.updateAddress(this.custId, address.id, request).subscribe({
      next: () => this.refreshAddresses(),
      error: () => this.addressActionError.set(this.i18n.t('detail.addressSaveError'))
    });
  }

  protected openAddAddressModal(): void {
    this.openAddressMenuId.set(null);
    this.editingAddressId.set(null);
    this.addressSaveError.set(null);
    this.addressActionError.set(null);
    this.addressForm().reset({ ...EMPTY_ADDRESS_FORM });
    this.isAddressModalOpen.set(true);
  }

  protected openEditAddressModal(address: AddressResponse): void {
    this.openAddressMenuId.set(null);
    this.editingAddressId.set(address.id);
    this.addressSaveError.set(null);
    this.addressActionError.set(null);
    this.addressForm().reset({
      city: String(address.cityId),
      street: address.streetName,
      houseNumber: address.houseName,
      ...splitAddressDesc(address.addrDesc)
    });
    this.isAddressModalOpen.set(true);
  }

  protected closeAddressModal(): void {
    this.isAddressModalOpen.set(false);
  }

  protected saveAddress(): void {
    if (this.addressForm().invalid()) {
      return;
    }

    this.isSavingAddress.set(true);
    this.addressSaveError.set(null);

    const request = this.toAddressEditRequest();
    const editingId = this.editingAddressId();
    const save$ = editingId
      ? this.customerService.updateAddress(this.custId, editingId, request)
      : this.customerService.addAddress(this.custId, request);

    save$.subscribe({
      next: () => {
        this.isSavingAddress.set(false);
        this.isAddressModalOpen.set(false);
        this.refreshAddresses();
      },
      error: (httpError: HttpErrorResponse) => {
        this.isSavingAddress.set(false);
        this.addressSaveError.set(
          httpError.status === 409 ? this.i18n.t('detail.maxAddressesReached') : this.i18n.t('detail.addressSaveError')
        );
      }
    });
  }

  private refreshAddresses(): void {
    this.customerService.getAddresses(this.custId).subscribe(addresses => {
      this.addresses.set(addresses);
      this.customer.set(mapToCustomerDetail(this.customerDetailResponse, this.individualResponse, addresses));
    });
  }

  private toAddressEditRequest(): AddressEditRequest {
    const value = this.addressModel();
    const editingId = this.editingAddressId();
    // Duzenlemede mevcut primary durumu korunur; yeni eklenen ilk adres otomatik primary olur.
    const primary = editingId
      ? (this.addresses().find(address => address.id === editingId)?.primary ?? false)
      : this.addresses().length === 0;

    return {
      cityId: Number(value.city),
      streetName: value.street,
      buildingName: value.houseNumber,
      addressDesc: `${value.description}${ADDRESS_DESC_SEPARATOR}${value.details}`,
      primary
    };
  }
}
