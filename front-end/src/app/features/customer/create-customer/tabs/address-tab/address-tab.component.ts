import { Component, effect, inject, signal, ChangeDetectionStrategy } from '@angular/core';
import { form, FormField, required } from '@angular/forms/signals';
import { AddressInfo } from '../../../../../core/customer';
import { I18nService } from '../../../../../core/i18n';
import { AddressFormModel, CreateCustomerFormStateService } from '../../create-customer.component';

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
  private readonly formState = inject(CreateCustomerFormStateService);

  protected readonly maxAddresses = 5;
  // eklenen adresler sekmeler arasi gecince kaybolmamasi icin CreateCustomerFormStateService'te tutulur
  protected readonly addresses = this.formState.addresses;
  protected readonly isAddAddressModalOpen = signal(false);

  // "yeni adres ekle" modalindaki taslak veri gecicidir, kaydedilmeden sekme degisirse kaybolmasi beklenir
  protected readonly addressModel = signal<AddressFormModel>({ ...EMPTY_ADDRESS });

  protected readonly addressForm = form(this.addressModel, path => {
    required(path.city);
    required(path.street);
    required(path.houseNumber);
    required(path.description);
  });

  constructor() {
    // ACC-011: en az bir adres eklenmeden sonraki adima gecilemez - sihirbazin ortak state'ine yansitilir.
    effect(() => {
      const addresses = this.addresses();
      this.formState.addressesValid.set(addresses.length > 0);
      this.formState.addressesValue.set(addresses.map(toAddressInfo));
    });
  }

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

function toAddressInfo(address: AddressFormModel): AddressInfo {
  return {
    cityId: Number(address.city),
    streetName: address.street,
    buildingName: address.houseNumber,
    addressDesc: address.description
  };
}
