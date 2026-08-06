import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { form, FormField, required } from '@angular/forms/signals';
import { AddressEditRequest, AddressResponse, CustomerService } from '../../../../../core/customer';
import { CITY_NAMES } from '../../../detail-customer/detail-customer.mapper';
import { I18nService } from '../../../../../core/i18n';
import { NewSaleFormStateService } from '../../new-sale.component';

const UNKNOWN = '—';

interface AddressFormModel {
  city: string;
  street: string;
  houseNumber: string;
  description: string;
}

const EMPTY_ADDRESS_FORM: AddressFormModel = { city: '', street: '', houseNumber: '', description: '' };

// Karakteristik formu (Connection Type/Modem Model vb.) icin product-service'te sema/tanim endpoint'i
// yok, bu yuzden bilincli olarak atlandi - sadece Service Address karti kuruldu (kullanicinin karariyla).
@Component({
  selector: 'app-configuration-step',
  imports: [FormField],
  templateUrl: './configuration-step.component.html',
  styleUrl: './configuration-step.component.scss',
  changeDetection: ChangeDetectionStrategy.Eager
})
export class ConfigurationStepComponent {
  protected readonly i18n = inject(I18nService);
  private readonly customerService = inject(CustomerService);
  protected readonly formState = inject(NewSaleFormStateService);

  protected readonly selectedAddress = computed<AddressResponse | null>(
    () => this.formState.addresses().find(address => address.id === this.formState.selectedAddressId()) ?? null
  );

  protected readonly isChangeAddressModalOpen = signal(false);
  protected readonly isAddAddressModalOpen = signal(false);
  protected readonly isSavingAddress = signal(false);
  protected readonly addressSaveError = signal<string | null>(null);

  protected readonly addressModel = signal<AddressFormModel>({ ...EMPTY_ADDRESS_FORM });
  protected readonly addressForm = form(this.addressModel, path => {
    required(path.city);
    required(path.street);
    required(path.houseNumber);
    required(path.description);
  });

  protected cityName(cityId: number): string {
    return CITY_NAMES[cityId] ?? UNKNOWN;
  }

  protected addressLine(address: AddressResponse): string {
    return `${address.streetName} ${address.houseName}, ${this.cityName(address.cityId)}`;
  }

  protected openChangeAddressModal(): void {
    this.isChangeAddressModalOpen.set(true);
  }

  protected closeChangeAddressModal(): void {
    this.isChangeAddressModalOpen.set(false);
  }

  protected selectAddress(addressId: number): void {
    this.formState.selectedAddressId.set(addressId);
    this.isChangeAddressModalOpen.set(false);
  }

  protected openAddAddressModal(): void {
    this.addressSaveError.set(null);
    this.addressForm().reset({ ...EMPTY_ADDRESS_FORM });
    this.isChangeAddressModalOpen.set(false);
    this.isAddAddressModalOpen.set(true);
  }

  protected closeAddAddressModal(): void {
    this.isAddAddressModalOpen.set(false);
  }

  protected saveNewAddress(): void {
    if (this.addressForm().invalid()) {
      return;
    }

    this.isSavingAddress.set(true);
    this.addressSaveError.set(null);

    const value = this.addressModel();
    const request: AddressEditRequest = {
      cityId: Number(value.city),
      streetName: value.street,
      buildingName: value.houseNumber,
      addressDesc: value.description,
      primary: this.formState.addresses().length === 0
    };

    this.customerService.addAddress(this.formState.custId(), request).subscribe({
      next: address => {
        this.isSavingAddress.set(false);
        this.isAddAddressModalOpen.set(false);
        this.formState.addresses.update(addresses => [...addresses, address]);
        this.formState.selectedAddressId.set(address.id);
      },
      error: (httpError: HttpErrorResponse) => {
        this.isSavingAddress.set(false);
        this.addressSaveError.set(
          httpError.status === 409 ? this.i18n.t('detail.maxAddressesReached') : this.i18n.t('detail.addressSaveError')
        );
      }
    });
  }
}
