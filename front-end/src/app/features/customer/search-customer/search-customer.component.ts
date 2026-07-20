import { Component, inject, signal, computed, ChangeDetectionStrategy } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { CustomerSearchResult, CustomerService } from '../../../core/customer';
import { I18nService } from '../../../core/i18n';

type DigitFieldName = 'natIdNumber' | 'customerId' | 'accountNumber' | 'gsmNumber' | 'orderNumber';

type SortColumn = 'custId' | 'firstName' | 'middleName' | 'lastName' | 'tcNo' | 'acctNo' | 'role' | 'gsm' | 'status';
type SortDirection = 'asc' | 'desc';

@Component({
  selector: 'app-search-customer',
  imports: [ReactiveFormsModule],
  templateUrl: './search-customer.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './search-customer.component.scss'
})
export class SearchCustomerComponent {
  protected readonly i18n = inject(I18nService);
  private readonly formBuilder = inject(FormBuilder);
  private readonly customerService = inject(CustomerService);

  protected readonly hasFilledFilter = signal(false);
  protected readonly isSearching = signal(false);
  protected readonly hasSearched = signal(false);
  protected readonly searchError = signal(false);
  protected readonly searchResults = signal<CustomerSearchResult[]>([]);

  protected readonly sortColumn = signal<SortColumn | null>(null);
  protected readonly sortDirection = signal<SortDirection>('asc');

  protected readonly sortedResults = computed(() => {
    const column = this.sortColumn();
    const results = this.searchResults();
    if (!column) {
      return results;
    }

    const direction = this.sortDirection() === 'asc' ? 1 : -1;
    return [...results].sort((a, b) => {
      const left = a[column];
      const right = b[column];
      if (left == null && right == null) {
        return 0;
      }
      if (left == null) {
        return direction;
      }
      if (right == null) {
        return -direction;
      }
      return direction * String(left).localeCompare(String(right), undefined, { numeric: true });
    });
  });

  protected readonly fieldErrors = signal<Record<DigitFieldName, boolean>>({
    natIdNumber: false,
    customerId: false,
    accountNumber: false,
    gsmNumber: false,
    orderNumber: false
  });

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
  }

  protected clearFilters(): void {
    this.searchForm.reset();
    this.hasSearched.set(false);
    this.searchError.set(false);
    this.searchResults.set([]);
    this.sortColumn.set(null);
    this.sortDirection.set('asc');
  }

  protected search(): void {
    if (!this.hasFilledFilter()) {
      return;
    }

    const { natIdNumber, accountNumber, customerId, gsmNumber, firstName, lastName } = this.searchForm.getRawValue();

    this.isSearching.set(true);
    this.searchError.set(false);
    this.sortColumn.set(null);
    this.sortDirection.set('asc');

    this.customerService
      .search({ firstName, lastName, tcNo: natIdNumber, acctNo: accountNumber, custId: customerId, gsm: gsmNumber })
      .subscribe({
        next: results => {
          this.searchResults.set(results);
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

  /** Tek kolonda sort: ilk tik ASC, ikinci tik DESC, farkli kolona tiklamak o kolonu ASC'den baslatir. */
  protected toggleSort(column: SortColumn): void {
    if (this.sortColumn() !== column) {
      this.sortColumn.set(column);
      this.sortDirection.set('asc');
      return;
    }
    this.sortDirection.set(this.sortDirection() === 'asc' ? 'desc' : 'asc');
  }

  protected setFieldError(field: DigitFieldName, hasError: boolean): void {
    this.fieldErrors.update(errors => ({ ...errors, [field]: hasError }));
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
    let digitsOnly = input.value.replace(/\D/g, '');

    while (digitsOnly.length > 0 && digitsOnly[0] !== '5') {
      digitsOnly = digitsOnly.slice(1);
    }

    this.setFieldError('gsmNumber', input.value !== digitsOnly);
    this.searchForm.controls.gsmNumber.setValue(digitsOnly);
  }

  protected sanitizeLetters(event: Event, controlName: 'firstName' | 'lastName'): void {
    const input = event.target as HTMLInputElement;
    const lettersOnly = input.value.replace(/[^a-zA-ZçÇğĞıİöÖşŞüÜ\s]/g, '');
    this.searchForm.controls[controlName].setValue(lettersOnly);
  }
}
