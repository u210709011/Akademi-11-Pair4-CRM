import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { I18nService } from '../../../../../core/i18n';

@Component({
  selector: 'app-address-tab',
  imports: [ReactiveFormsModule],
  templateUrl: './address-tab.component.html',
  styleUrl: './address-tab.component.scss'
})
export class AddressTabComponent {
  protected readonly i18n = inject(I18nService);
  private readonly formBuilder = inject(FormBuilder);

  protected readonly maxAddresses = 5;
  protected readonly addresses = signal<unknown[]>([]);
  protected readonly isAddAddressModalOpen = signal(false);

  protected readonly addressForm = this.formBuilder.nonNullable.group({
    city: ['', Validators.required],
    street: ['', Validators.required],
    houseNumber: ['', Validators.required],
    description: ['', Validators.required]
  });

  protected openAddAddressModal(): void {
    this.isAddAddressModalOpen.set(true);
  }

  protected closeAddAddressModal(): void {
    this.isAddAddressModalOpen.set(false);
    this.addressForm.reset();
  }

  protected saveAddress(): void {
    if (this.addressForm.invalid) {
      return;
    }
    this.addresses.update(list => [...list, this.addressForm.getRawValue()]);
    this.closeAddAddressModal();
  }
}
