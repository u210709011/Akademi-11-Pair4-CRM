import { Component, HostListener, computed, effect, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { email, form, FormField, maxLength, pattern, required } from '@angular/forms/signals';
import {
  AddressEditRequest,
  AddressResponse,
  ContactInfo,
  CreateBillingAccountRequest,
  CustomerDetailResponse,
  CustomerService,
  IndividualResponse
} from '../../../core/customer';
import { I18nService } from '../../../core/i18n';
import { OrderService } from '../../../core/order';
import {
  CITY_NAMES,
  CustomerAccount,
  CustomerContact,
  CustomerDetail,
  mapToAccountProducts,
  mapToCustomerAccounts,
  mapToCustomerContact,
  mapToCustomerDetail
} from './detail-customer.mapper';

interface AddressFormModel {
  city: string;
  street: string;
  houseNumber: string;
  description: string;
}

const EMPTY_ADDRESS_FORM: AddressFormModel = { city: '', street: '', houseNumber: '', description: '' };

interface ContactFormModel {
  email: string;
  mobilePhone: string;
  homePhone: string;
  fax: string;
}

const EMPTY_CONTACT_FORM: ContactFormModel = { email: '', mobilePhone: '', homePhone: '', fax: '' };

type ContactPhoneFieldName = 'homePhone' | 'mobilePhone' | 'fax';

// backend sozlesmesi: mobilePhone ^5[0-9]{9}$ (tam 10 hane, +90 haric); homePhone/fax opsiyonel ^[0-9]{10,11}$.
const CONTACT_PHONE_FIELDS: ContactPhoneFieldName[] = ['mobilePhone', 'homePhone', 'fax'];
const CONTACT_PHONE_MAX_DIGITS: Record<ContactPhoneFieldName, number> = { mobilePhone: 10, homePhone: 11, fax: 11 };
const MOBILE_PHONE_PATTERN = /^5[0-9]{9}$/;
const HOME_OR_FAX_PHONE_PATTERN = /^[0-9]{10,11}$/;
const DIGITS_ONLY_ERROR_TIMEOUT_MS = 2000;

interface CreateAccountFormModel {
  accountName: string;
  accountDesc: string;
  addressId: string;
}

const EMPTY_CREATE_ACCOUNT_FORM: CreateAccountFormModel = { accountName: '', accountDesc: '', addressId: '' };

type DetailTab = 'information' | 'accounts' | 'address' | 'contact';

const ACCOUNTS_PAGE_SIZE = 5;

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
  private readonly orderService = inject(OrderService);

  protected readonly isLoading = signal(true);
  protected readonly loadError = signal(false);

  protected readonly customer = signal<CustomerDetail>(EMPTY_CUSTOMER_DETAIL);
  protected readonly accounts = signal<CustomerAccount[]>([]);
  protected readonly contact = signal<CustomerContact>(EMPTY_CUSTOMER_CONTACT);
  protected readonly addresses = signal<AddressResponse[]>([]);
  protected readonly maxAddresses = 5;
  protected readonly openAddressMenuId = signal<number | null>(null);
  protected readonly expandedAccountId = signal<number | null>(null);

  // Billing Accounts tablosu istemci tarafinda sayfalanir - accounts() zaten getById() ile tam
  // yuklu (bkz. search-customer.component.ts'teki sunucu-tarafli pagination'in ayni sekli,
  // burada sadece dilimleme var, ekstra istek yok).
  protected readonly accountsPage = signal(0);
  protected readonly pagedAccounts = computed(() =>
    this.accounts().slice(this.accountsPage() * ACCOUNTS_PAGE_SIZE, this.accountsPage() * ACCOUNTS_PAGE_SIZE + ACCOUNTS_PAGE_SIZE)
  );
  protected readonly accountsTotalPages = computed(() => Math.max(1, Math.ceil(this.accounts().length / ACCOUNTS_PAGE_SIZE)));
  protected readonly accountsRangeStart = computed(() =>
    this.accounts().length === 0 ? 0 : this.accountsPage() * ACCOUNTS_PAGE_SIZE + 1
  );
  protected readonly accountsRangeEnd = computed(() =>
    Math.min(this.accounts().length, (this.accountsPage() + 1) * ACCOUNTS_PAGE_SIZE)
  );
  protected readonly accountsRangeLabel = computed(() => `${this.accountsRangeStart()}-${this.accountsRangeEnd()} of ${this.accounts().length}`);
  protected readonly accountsPageNumbers = computed(() => Array.from({ length: this.accountsTotalPages() }, (_, i) => i));

  protected readonly isAddressModalOpen = signal(false);
  protected readonly editingAddressId = signal<number | null>(null);
  protected readonly isSavingAddress = signal(false);
  protected readonly addressSaveError = signal<string | null>(null);
  protected readonly addressActionError = signal<string | null>(null);

  protected readonly isDeleteConfirmOpen = signal(false);
  protected readonly isDeletingCustomer = signal(false);
  protected readonly deleteError = signal<string | null>(null);

  protected readonly addressToDelete = signal<AddressResponse | null>(null);
  protected readonly isDeletingAddress = signal(false);
  protected readonly deleteAddressError = signal<string | null>(null);

  protected readonly isContactModalOpen = signal(false);
  protected readonly isSavingContact = signal(false);
  protected readonly contactSaveError = signal<string | null>(null);
  // sag ust, mockup'taki gercek toast tasarimi - hem contact save hem de billing account
  // create basari mesajlari bunu kullanir (bkz. showToast).
  protected readonly toastMessage = signal<string | null>(null);
  private toastTimeoutId?: ReturnType<typeof setTimeout>;

  // harf/gecersiz karakter yazilmaya calisildiginda ilgili alanin altinda gecici uyari gostermek icin (bkz. contact-tab.component.ts, onboarding).
  protected readonly digitsOnlyErrorField = signal<ContactPhoneFieldName | null>(null);
  private digitsOnlyErrorTimeoutId?: ReturnType<typeof setTimeout>;

  protected readonly isCreateAccountModalOpen = signal(false);
  protected readonly isSavingAccount = signal(false);
  protected readonly createAccountError = signal<string | null>(null);
  // false: mevcut adres dropdown'i; true: inline "+ Add New Address" formu.
  protected readonly isAddingNewAddressForAccount = signal(false);

  protected readonly addressModel = signal<AddressFormModel>({ ...EMPTY_ADDRESS_FORM });

  protected readonly addressForm = form(this.addressModel, path => {
    required(path.city);
    required(path.street);
    maxLength(path.street, 200);
    required(path.houseNumber);
    required(path.description);
  });

  protected readonly contactModel = signal<ContactFormModel>({ ...EMPTY_CONTACT_FORM });

  protected readonly contactForm = form(this.contactModel, path => {
    required(path.email);
    email(path.email);
    required(path.mobilePhone);
    maxLength(path.mobilePhone, 10);
    pattern(path.mobilePhone, MOBILE_PHONE_PATTERN);
    maxLength(path.homePhone, 11);
    pattern(path.homePhone, HOME_OR_FAX_PHONE_PATTERN, { when: ({ value }) => value() !== '' });
    maxLength(path.fax, 11);
    pattern(path.fax, HOME_OR_FAX_PHONE_PATTERN, { when: ({ value }) => value() !== '' });
  });

  protected readonly accountModel = signal<CreateAccountFormModel>({ ...EMPTY_CREATE_ACCOUNT_FORM });

  // addressId zorunlulugu buradan degil, mevcut-adres/yeni-adres mod toggle'ina gore hesaplanan
  // accountFormValid computed'undan gelir (bkz. (e) dilimi) - "tam olarak biri" kurali capraz alan.
  protected readonly accountForm = form(this.accountModel, path => {
    required(path.accountName);
  });

  // "+ Add New Address" ile acilan inline form - addressForm'un validasyon kurallarinin birebir
  // klonu (ayri model: adres sekmesindeki duzenleme akisiyla state'i karismasin diye).
  protected readonly newAccountAddressModel = signal<AddressFormModel>({ ...EMPTY_ADDRESS_FORM });

  protected readonly newAccountAddressForm = form(this.newAccountAddressModel, path => {
    required(path.city);
    required(path.street);
    maxLength(path.street, 200);
    required(path.houseNumber);
    required(path.description);
  });

  // addressId/newAddress'ten tam olarak biri kuralini capraz-alan olarak burada uyguluyoruz -
  // accountForm sadece accountName'i (schema-only alan) dogrular.
  protected readonly accountFormValid = computed(() => {
    if (this.accountForm().invalid()) {
      return false;
    }
    return this.isAddingNewAddressForAccount()
      ? !this.newAccountAddressForm().invalid()
      : this.accountModel().addressId !== '';
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
  // contact() gosterim icin null alanlari '—' ile degistiriyor; duzenleme formunu gercek (nullable) degerlerle
  // doldurmak icin ham yanit ayrica saklanir.
  private contactResponse!: ContactInfo;

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
        this.contactResponse = contact;
        this.customer.set(mapToCustomerDetail(customerDetail, individual, addresses));
        this.accounts.set(mapToCustomerAccounts(customerDetail));
        this.contact.set(mapToCustomerContact(contact));
        this.addresses.set(addresses);
        this.isLoading.set(false);
        this.loadAccountProducts();
      },
      error: () => {
        this.loadError.set(true);
        this.isLoading.set(false);
      }
    });

    // her telefon alani icin rakam disi karakterleri temizle (baslangic hanesi kontrolu pattern validator'da).
    for (const field of CONTACT_PHONE_FIELDS) {
      effect(() => this.sanitizeContactPhoneField(field));
    }

    // hesap silindiginde (ileride) mevcut sayfa bosalirsa son gecerli sayfaya klemplenir -
    // accounts().length degisen her durumu kapsar, sadece silme akisina bagli degildir.
    effect(() => {
      const maxPage = this.accountsTotalPages() - 1;
      if (this.accountsPage() > maxPage) {
        this.accountsPage.set(maxPage);
      }
    });
  }

  // order-service'te custAcctId'ye gore filtrelenen tek bir toplu endpoint yok, o yuzden
  // aktif her hesap icin ayri istek atilir; hesap ID'sine gore ilgili satir guncellenir.
  private loadAccountProducts(): void {
    for (const account of this.accounts()) {
      if (!account.active) {
        continue;
      }
      this.orderService.getByCustAcctId(account.id).subscribe(items => {
        const products = mapToAccountProducts(items);
        this.accounts.update(accounts =>
          accounts.map(candidate => (candidate.id === account.id ? { ...candidate, products } : candidate))
        );
      });
    }
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

  protected openDeleteConfirm(): void {
    this.deleteError.set(null);
    this.isDeleteConfirmOpen.set(true);
  }

  protected closeDeleteConfirm(): void {
    this.isDeleteConfirmOpen.set(false);
  }

  protected confirmDeleteCustomer(): void {
    this.isDeletingCustomer.set(true);
    this.deleteError.set(null);

    this.customerService.deleteCustomer(this.custId).subscribe({
      next: () => {
        this.isDeletingCustomer.set(false);
        this.isDeleteConfirmOpen.set(false);
        this.router.navigateByUrl('/search-customer');
      },
      error: (httpError: HttpErrorResponse) => {
        this.isDeletingCustomer.set(false);
        this.deleteError.set(
          (httpError.error as { message?: string } | null)?.message ?? this.i18n.t('detail.deleteError')
        );
      }
    });
  }

  protected genderLabel(genderId: number): string {
    return genderId === 1 ? this.i18n.t('create.genderMale') : this.i18n.t('create.genderFemale');
  }

  protected cityName(cityId: number): string {
    return CITY_NAMES[cityId] ?? UNKNOWN;
  }

  protected toggleAddressMenu(addressId: number, event: Event): void {
    event.stopPropagation();
    this.openAddressMenuId.set(this.openAddressMenuId() === addressId ? null : addressId);
  }

  @HostListener('document:click')
  protected closeAddressMenu(): void {
    this.openAddressMenuId.set(null);
  }

  protected toggleAccountRow(accountId: number): void {
    this.expandedAccountId.set(this.expandedAccountId() === accountId ? null : accountId);
  }

  protected goToAccountsPage(page: number): void {
    if (page < 0 || page >= this.accountsTotalPages() || page === this.accountsPage()) {
      return;
    }
    this.accountsPage.set(page);
  }

  protected openCreateAccountModal(): void {
    this.createAccountError.set(null);
    this.isAddingNewAddressForAccount.set(false);
    this.accountForm().reset({ ...EMPTY_CREATE_ACCOUNT_FORM });
    this.newAccountAddressForm().reset({ ...EMPTY_ADDRESS_FORM });
    this.isCreateAccountModalOpen.set(true);
  }

  protected closeCreateAccountModal(): void {
    this.isCreateAccountModalOpen.set(false);
  }

  protected toggleAddNewAddressForAccount(): void {
    const switchingToNewAddress = !this.isAddingNewAddressForAccount();
    this.isAddingNewAddressForAccount.set(switchingToNewAddress);

    if (switchingToNewAddress) {
      // mevcut adres secimi istekte gonderilmesin diye temizlenir - addressId/newAddress'ten
      // tam olarak biri gider (bkz. saveAccount).
      this.accountForm.addressId().value.set('');
    } else {
      this.newAccountAddressForm().reset({ ...EMPTY_ADDRESS_FORM });
    }
  }

  protected saveAccount(): void {
    if (!this.accountFormValid()) {
      return;
    }

    this.isSavingAccount.set(true);
    this.createAccountError.set(null);

    this.customerService.createBillingAccount(this.custId, this.toCreateBillingAccountRequest()).subscribe({
      next: () => {
        this.isSavingAccount.set(false);
        this.isCreateAccountModalOpen.set(false);
        this.showToast(this.i18n.t('detail.createAccountSuccess'));
        this.accountsPage.set(0);
        this.refreshAccounts();
      },
      error: (httpError: HttpErrorResponse) => {
        this.isSavingAccount.set(false);
        this.createAccountError.set(
          (httpError.error as { message?: string } | null)?.message ?? this.i18n.t('detail.createAccountError')
        );
      }
    });
  }

  private toCreateBillingAccountRequest(): CreateBillingAccountRequest {
    const value = this.accountModel();
    const request: CreateBillingAccountRequest = {
      accountName: value.accountName,
      accountDesc: value.accountDesc || null
    };

    if (this.isAddingNewAddressForAccount()) {
      const newAddress = this.newAccountAddressModel();
      request.newAddress = {
        cityId: Number(newAddress.city),
        streetName: newAddress.street,
        buildingName: newAddress.houseNumber,
        addressDesc: newAddress.description
      };
    } else {
      request.addressId = Number(value.addressId);
    }

    return request;
  }

  protected serviceAddressLine(addressId: number | null): string {
    const address = this.addresses().find(candidate => candidate.id === addressId);
    if (!address) {
      return UNKNOWN;
    }
    return `${address.addrDesc} — ${address.streetName} ${address.houseName}, ${this.cityName(address.cityId)}`;
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

  protected openDeleteAddressConfirm(address: AddressResponse): void {
    this.openAddressMenuId.set(null);
    this.deleteAddressError.set(null);
    this.addressToDelete.set(address);
  }

  protected closeDeleteAddressConfirm(): void {
    this.addressToDelete.set(null);
  }

  protected confirmDeleteAddress(): void {
    const address = this.addressToDelete();
    if (!address) {
      return;
    }

    this.isDeletingAddress.set(true);
    this.deleteAddressError.set(null);

    this.customerService.deleteAddress(this.custId, address.id).subscribe({
      next: () => {
        this.isDeletingAddress.set(false);
        this.addressToDelete.set(null);
        this.refreshAddresses();
      },
      error: (httpError: HttpErrorResponse) => {
        this.isDeletingAddress.set(false);
        this.deleteAddressError.set(
          (httpError.error as { message?: string } | null)?.message ?? this.i18n.t('detail.addressSaveError')
        );
      }
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
      description: address.addrDesc
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

  protected openEditContactModal(): void {
    this.contactSaveError.set(null);
    this.contactForm().reset({
      email: this.contactResponse.email,
      mobilePhone: this.contactResponse.mobilePhone,
      homePhone: this.contactResponse.homePhone ?? '',
      fax: this.contactResponse.fax ?? ''
    });
    this.isContactModalOpen.set(true);
  }

  protected closeContactModal(): void {
    this.isContactModalOpen.set(false);
  }

  protected saveContact(): void {
    if (this.contactForm().invalid()) {
      return;
    }

    this.isSavingContact.set(true);
    this.contactSaveError.set(null);

    this.customerService.updateContact(this.custId, this.toContactInfo()).subscribe({
      next: response => {
        this.isSavingContact.set(false);
        this.isContactModalOpen.set(false);
        this.contactResponse = response;
        this.contact.set(mapToCustomerContact(response));
        this.showToast(this.i18n.t('detail.contactSaveSuccess'));
      },
      error: (httpError: HttpErrorResponse) => {
        this.isSavingContact.set(false);
        this.contactSaveError.set(
          (httpError.error as { message?: string } | null)?.message ?? this.i18n.t('detail.contactSaveError')
        );
      }
    });
  }

  private toContactInfo(): ContactInfo {
    const value = this.contactModel();
    return {
      email: value.email,
      mobilePhone: value.mobilePhone,
      homePhone: value.homePhone || null,
      fax: value.fax || null
    };
  }

  protected showToast(message: string): void {
    clearTimeout(this.toastTimeoutId);
    this.toastMessage.set(message);
    this.toastTimeoutId = setTimeout(() => this.toastMessage.set(null), 3000);
  }

  protected dismissToast(): void {
    clearTimeout(this.toastTimeoutId);
    this.toastMessage.set(null);
  }

  // rakam disindaki karakterlerin ekrana hic yazilmamasi icin (yapistirma dahil) tus/insert seviyesinde engelle.
  protected blockContactPhoneInput(event: InputEvent, field: ContactPhoneFieldName): void {
    if (event.data != null && /\D/.test(event.data)) {
      event.preventDefault();
      this.showDigitsOnlyError(field);
    }
  }

  private sanitizeContactPhoneField(field: ContactPhoneFieldName): void {
    const raw = this.contactForm[field]().value();
    const digitsOnly = raw.replace(/\D/g, '').slice(0, CONTACT_PHONE_MAX_DIGITS[field]);

    if (digitsOnly !== raw) {
      this.contactForm[field]().value.set(digitsOnly);
      // beforeinput engellemeden kacan durumlar icin (yapistirma, otomatik doldurma vb.) yedek uyari.
      if (/\D/.test(raw)) {
        this.showDigitsOnlyError(field);
      }
    }
  }

  private showDigitsOnlyError(field: ContactPhoneFieldName): void {
    clearTimeout(this.digitsOnlyErrorTimeoutId);
    this.digitsOnlyErrorField.set(field);
    this.digitsOnlyErrorTimeoutId = setTimeout(
      () => this.digitsOnlyErrorField.set(null),
      DIGITS_ONLY_ERROR_TIMEOUT_MS
    );
  }

  private refreshAddresses(): void {
    this.customerService.getAddresses(this.custId).subscribe(addresses => {
      this.addresses.set(addresses);
      this.customer.set(mapToCustomerDetail(this.customerDetailResponse, this.individualResponse, addresses));
    });
  }

  private refreshAccounts(): void {
    this.customerService.getById(this.custId).subscribe(customerDetail => {
      this.customerDetailResponse = customerDetail;
      this.accounts.set(mapToCustomerAccounts(customerDetail));
      this.customer.set(mapToCustomerDetail(customerDetail, this.individualResponse, this.addresses()));
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
      addressDesc: value.description,
      primary
    };
  }
}
