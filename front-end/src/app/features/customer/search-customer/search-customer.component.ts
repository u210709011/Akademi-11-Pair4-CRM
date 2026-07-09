import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { I18nService } from '../../../core/i18n';

@Component({
  selector: 'app-search-customer',
  imports: [ReactiveFormsModule],
  templateUrl: './search-customer.component.html',
  styleUrl: './search-customer.component.scss'
})
export class SearchCustomerComponent {
  protected readonly i18n = inject(I18nService);
  private readonly formBuilder = inject(FormBuilder);

  protected readonly hasFilledFilter = signal(false);

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
  }

  protected sanitizeDigits(
    event: Event, controlName: 'natIdNumber' | 'customerId' | 'accountNumber', maxLength?: number
  ): void {
    const input = event.target as HTMLInputElement;
    const digitsOnly = input.value.replace(/\D/g, '').slice(0, maxLength);
    this.searchForm.controls[controlName].setValue(digitsOnly);
  }

  protected sanitizeGsm(event: Event): void {
    const input = event.target as HTMLInputElement;
    let digitsOnly = input.value.replace(/\D/g, '');

    while (digitsOnly.length > 0 && digitsOnly[0] !== '5') {
      digitsOnly = digitsOnly.slice(1);
    }

    this.searchForm.controls.gsmNumber.setValue(digitsOnly);
  }

  protected sanitizeLetters(event: Event, controlName: 'firstName' | 'lastName'): void {
    const input = event.target as HTMLInputElement;
    const lettersOnly = input.value.replace(/[^a-zA-ZçÇğĞıİöÖşŞüÜ\s]/g, '');
    this.searchForm.controls[controlName].setValue(lettersOnly);
  }
}
