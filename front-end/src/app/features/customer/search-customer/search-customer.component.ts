import { Component, inject, signal, computed, effect, untracked, ChangeDetectionStrategy } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { TranslateService, TranslatePipe } from '@ngx-translate/core';
import { CustomerSearchCriteria, CustomerSearchResult, CustomerService } from '../data-access/customer';

type DigitFieldName = 'natIdNumber' | 'customerId' | 'accountNumber' | 'gsmNumber' | 'orderNumber';
type NameFieldName = 'firstName' | 'lastName';
type ValidatedFieldName = DigitFieldName | NameFieldName;

type SortColumn = 'custId' | 'firstName' | 'middleName' | 'lastName' | 'tcNo' | 'role';
type SortDirection = 'asc' | 'desc';

const PAGE_SIZE = 10;

@Component({
  selector: 'app-search-customer',
  imports: [ReactiveFormsModule, TranslatePipe],
  templateUrl: './search-customer.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './search-customer.component.scss'
})
export class SearchCustomerComponent {
  protected readonly translate = inject(TranslateService);
  private readonly formBuilder = inject(FormBuilder);
  private readonly customerService = inject(CustomerService);
  private readonly router = inject(Router);

  protected readonly hasFilledFilter = signal(false);
  protected readonly isSearching = signal(false);
  protected readonly hasSearched = signal(false);
  protected readonly searchError = signal(false);
  protected readonly searchResults = signal<CustomerSearchResult[]>([]);

  protected readonly pageSize = PAGE_SIZE;
  protected readonly currentPage = signal(0);
  protected readonly totalElements = signal(0);
  protected readonly totalPages = computed(() => Math.max(1, Math.ceil(this.totalElements() / this.pageSize)));
  protected readonly rangeStart = computed(() => this.totalElements() === 0 ? 0 : this.currentPage() * this.pageSize + 1);
  protected readonly rangeEnd = computed(() => Math.min(this.totalElements(), (this.currentPage() + 1) * this.pageSize));
  protected readonly resultsCountLabel = computed(() =>
    this.translate.instant('search.resultsCount', { count: this.totalElements() })
  );
  protected readonly rangeLabel = computed(() => `${this.rangeStart()}-${this.rangeEnd()} of ${this.totalElements()}`);
  protected readonly pageNumbers = computed(() => Array.from({ length: this.totalPages() }, (_, i) => i));

  private lastCriteria: CustomerSearchCriteria | null = null;

  // Backend-driven: sort her zaman tum sonuc kumesi uzerinde uygulanir (bkz. customer.service.ts search()),
  // sadece o an yuklu sayfa uzerinde degil. Kolon degistiginde/yon degistiginde yeni bir search istegi atilir.
  protected readonly sortColumn = signal<SortColumn | null>(null);
  protected readonly sortDirection = signal<SortDirection>('asc');

  protected readonly fieldErrors = signal<Record<ValidatedFieldName, boolean>>({
    natIdNumber: false,
    customerId: false,
    accountNumber: false,
    gsmNumber: false,
    orderNumber: false,
    firstName: false,
    lastName: false
  });
  // Search butonu, herhangi bir alanda gecerli bir hata gosterilirken de aktif olmamali
  // (ör. NAT ID 11 haneden az girilip alandan cikildiginda).
  protected readonly hasFieldErrors = computed(() => Object.values(this.fieldErrors()).some(hasError => hasError));

  protected readonly searchForm = this.formBuilder.nonNullable.group({
    natIdNumber: [''],
    customerId: [''],
    accountNumber: [''],
    gsmNumber: [''],
    firstName: [''],
    lastName: [''],
    orderNumber: ['']
  });

  constructor() {
    this.searchForm.valueChanges.subscribe(value => {
      const anyFilled = Object.values(value).some(fieldValue => !!fieldValue?.trim());
      this.hasFilledFilter.set(anyFilled);
    });

    // role gibi backend-driven alanlar dile gore cevrilir (bkz. Accept-Language interceptor) -
    // ama SPA'da sayfa yenilenmedigi icin dil degistiginde eldeki sonuclar eski dilde kalirdi.
    // runSearch() untracked cagrilir ki currentPage/sortColumn gibi ic okumalari bu effect'i
    // fazladan tetiklemesin (o degisiklikler zaten kendi handler'larinda runSearch() cagiriyor);
    // effect SADECE i18n.lang() degisince tekrar calisir. lastCriteria yoksa runSearch() no-op'tur.
    effect(() => {
      this.translate.currentLang();
      untracked(() => this.runSearch());
    });
  }

  protected clearFilters(): void {
    this.searchForm.reset();
    this.hasSearched.set(false);
    this.searchError.set(false);
    this.searchResults.set([]);
    this.sortColumn.set(null);
    this.sortDirection.set('asc');
    this.currentPage.set(0);
    this.totalElements.set(0);
    this.lastCriteria = null;
  }

  protected search(): void {
    if (!this.hasFilledFilter()) {
      return;
    }

    const { natIdNumber, accountNumber, customerId, gsmNumber, firstName, lastName } = this.searchForm.getRawValue();
    this.lastCriteria = { firstName, lastName, tcNo: natIdNumber, acctNo: accountNumber, custId: customerId, gsm: gsmNumber };
    this.currentPage.set(0);
    this.sortColumn.set(null);
    this.sortDirection.set('asc');
    this.runSearch();
  }

  protected goToPage(page: number): void {
    if (page < 0 || page >= this.totalPages() || page === this.currentPage()) {
      return;
    }
    this.currentPage.set(page);
    this.runSearch();
  }

  private runSearch(): void {
    if (!this.lastCriteria) {
      return;
    }

    this.isSearching.set(true);
    this.searchError.set(false);

    this.customerService
      .search(this.lastCriteria, this.currentPage(), this.pageSize, this.sortColumn(), this.sortDirection())
      .subscribe({
        next: ({ results, totalElements }) => {
          this.searchResults.set(results);
          this.totalElements.set(totalElements);
          this.hasSearched.set(true);
          this.isSearching.set(false);
        },
        error: () => {
          this.searchError.set(true);
          this.hasSearched.set(true);
          this.isSearching.set(false);
        }
      });
  }

  // isim yerine shrtCode'a gore secilir - bkz. demographic-tab.component.ts genderLabel ile ayni
  // desen. Backend'in cevirdigi role/name yazim hatasi tasiyabilir (ör. GNL_TP seed verisindeki
  // "Musteri"), o yuzden sadece shrtCode taniniyorsa sozlukten okunur; taninmayan/eslesmeyen bir
  // kod gelirse (yeni bir rol eklendi ama arayuz henuz guncellenmedi) backend'in name'ine dusulur.
  protected roleLabel(customer: CustomerSearchResult): string {
    switch (customer.roleShrtCode) {
      case 'CUSTOMER': return this.translate.instant('search.roleCustomer');
      case 'PARTNER': return this.translate.instant('search.rolePartner');
      default: return customer.role ?? '-';
    }
  }

  protected viewCustomerDetail(customer: CustomerSearchResult): void {
    this.router.navigate(['/detail-customer', customer.custId], { state: { customer } });
  }

  protected goToCreateCustomer(): void {
    this.router.navigateByUrl('/create-customer');
  }

  // Tek kolonda sort: ilk tik ASC, ikinci tik DESC, farkli kolona tiklamak o kolonu ASC'den baslatir.
  // Backend-driven oldugu icin her tikta sayfa 0'a donup yeni bir search istegi atilir (tum sonuc kumesi
  // uzerinde siralanmis halde geri gelir - bkz. runSearch()).
  protected toggleSort(column: SortColumn): void {
    if (this.sortColumn() !== column) {
      this.sortColumn.set(column);
      this.sortDirection.set('asc');
    } else {
      this.sortDirection.set(this.sortDirection() === 'asc' ? 'desc' : 'asc');
    }
    this.currentPage.set(0);
    this.runSearch();
  }

  protected setFieldError(field: ValidatedFieldName, hasError: boolean): void {
    this.fieldErrors.update(errors => ({ ...errors, [field]: hasError }));
  }

  // NAT ID zorunlu degil ama girildiyse tam 11 hane olmali - sadece gecersiz karakter yazildiginda
  // degil, alandan cikildiginda eksik/uzun hane sayisi da hata olarak gosterilir.
  protected onNatIdBlur(): void {
    const raw: string = this.searchForm.controls.natIdNumber.value;
    this.setFieldError('natIdNumber', raw.length > 0 && raw.length !== 11);
  }

  protected sanitizeDigits(
    event: Event,
    controlName: 'natIdNumber' | 'customerId' | 'accountNumber' | 'orderNumber',
    maxLength?: number
  ): void {
    const input = event.target as HTMLInputElement;
    const digitsOnly = input.value.replace(/\D/g, '').slice(0, maxLength);
    this.setFieldError(controlName, input.value !== digitsOnly);
    this.searchForm.controls[controlName].setValue(digitsOnly);
  }

  protected sanitizeGsm(event: Event): void {
    const input = event.target as HTMLInputElement;
    let digitsOnly = input.value.replace(/\D/g, '').slice(0, 10);

    while (digitsOnly.length > 0 && digitsOnly[0] !== '5') {
      digitsOnly = digitsOnly.slice(1);
    }

    this.setFieldError('gsmNumber', input.value !== digitsOnly);
    this.searchForm.controls.gsmNumber.setValue(digitsOnly);
  }

  // GSM zorunlu degil ama girildiyse tam 10 hane olmali - NAT ID ile ayni mantik:
  // yazarken degil, alandan cikildiginda eksik hane sayisi hata olarak gosterilir.
  protected onGsmBlur(): void {
    const raw: string = this.searchForm.controls.gsmNumber.value;
    this.setFieldError('gsmNumber', raw.length > 0 && raw.length !== 10);
  }

  protected sanitizeLetters(event: Event, controlName: NameFieldName): void {
    const input = event.target as HTMLInputElement;
    const lettersOnly = input.value.replace(/[^a-zA-ZçÇğĞıİöÖşŞüÜ\s]/g, '').slice(0, 50);
    this.setFieldError(controlName, input.value !== lettersOnly);
    this.searchForm.controls[controlName].setValue(lettersOnly);
  }

  // First/Last Name: "Text, max 50" - yalnizca uzunluk kurali var, harf-disi karakterler zaten
  // yazarken sanitizeLetters ile siliniyor. NAT ID/GSM ile ayni desen: blur'da kalan hata temizlenir.
  protected onNameBlur(controlName: NameFieldName): void {
    this.setFieldError(controlName, false);
  }
}
