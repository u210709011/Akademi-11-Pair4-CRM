import { Component, inject, signal, ChangeDetectionStrategy } from '@angular/core';
import { form, FormField, required } from '@angular/forms/signals';
import { I18nService } from '../../../../../core/i18n';

interface AddressFormModel {
  city: string;
  street: string;
  houseNumber: string;
  description: string;
}

const EMPTY_ADDRESS: AddressFormModel = { city: '', street: '', houseNumber: '', description: '' };

@Component({
  selector: 'app-address-tab',
  imports: [FormField],
  templateUrl: './address-tab.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './address-tab.component.scss'
})
export class AddressTabComponent {
  protected readonly i18n = inject(I18nService);

  protected readonly maxAddresses = 5;
  protected readonly addresses = signal<AddressFormModel[]>([]);
  protected readonly isAddAddressModalOpen = signal(false);

  protected readonly addressModel = signal<AddressFormModel>({ ...EMPTY_ADDRESS });

  protected readonly addressForm = form(this.addressModel, path => {
    required(path.city);
    required(path.street);
    required(path.houseNumber);
    required(path.description);
  });

  protected openAddAddressModal(): void {
    this.isAddAddressModalOpen.set(true);
  }

  protected closeAddAddressModal(): void {
    this.isAddAddressModalOpen.set(false);
    this.addressForm().reset({ ...EMPTY_ADDRESS });
  }

  protected saveAddress(): void {
    if (this.addressForm().invalid()) {
      return;
    }
    this.addresses.update(list => [...list, this.addressModel()]);
    this.closeAddAddressModal();
  }
}
