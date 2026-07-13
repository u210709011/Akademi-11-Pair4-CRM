import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { CustomerSearchResult, CustomerService } from '../../../core/customer';
import { I18nService } from '../../../core/i18n';

type DigitFieldName = 'natIdNumber' | 'customerId' | 'accountNumber' | 'gsmNumber' | 'orderNumber';

@Component({
  selector: 'app-search-customer',
  imports: [ReactiveFormsModule],
  templateUrl: './search-customer.component.html',
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
  }

  protected search(): void {
    if (!this.hasFilledFilter()) {
      return;
    }

    const { natIdNumber, accountNumber, firstName, lastName } = this.searchForm.getRawValue();

    this.isSearching.set(true);
    this.searchError.set(false);

    this.customerService
      .search({ firstName, lastName, tcNo: natIdNumber, acctNo: accountNumber })
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
