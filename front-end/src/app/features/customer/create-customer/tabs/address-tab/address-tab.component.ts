import { Component, HostListener, computed, effect, inject, signal, ChangeDetectionStrategy } from '@angular/core';
import { form, FormField, maxLength, required } from '@angular/forms/signals';
import { AddressInfo } from '../../../../../core/customer';
import { I18nService } from '../../../../../core/i18n';
import { GnlType, LOOKUP_GROUPS, LookupService } from '../../../../../core/lookup';
import { AddressFormModel, CreateCustomerFormStateService } from '../../create-customer.component';
import { ButtonComponent } from '../../../../../shared/components/button/button.component';

const EMPTY_ADDRESS: AddressFormModel = { city: '', street: '', houseNumber: '', description: '' };

@Component({
  selector: 'app-address-tab',
  imports: [FormField, ButtonComponent],
  templateUrl: './address-tab.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './address-tab.component.scss'
})
export class AddressTabComponent {
  protected readonly i18n = inject(I18nService);
  private readonly formState = inject(CreateCustomerFormStateService);
  private readonly lookupService = inject(LookupService);

  protected readonly cities = signal<GnlType[]>([]);

  protected readonly maxAddresses = 5;
  // eklenen adresler sekmeler arasi gecince kaybolmamasi icin CreateCustomerFormStateService'te tutulur
  protected readonly addresses = this.formState.addresses;
  protected readonly isAddAddressModalOpen = signal(false);
  protected readonly openAddressMenuIndex = signal<number | null>(null);

  // "yeni adres ekle" modalindaki taslak veri gecicidir, kaydedilmeden sekme degisirse kaybolmasi beklenir
  protected readonly addressModel = signal<AddressFormModel>({ ...EMPTY_ADDRESS });

  protected readonly addressForm = form(this.addressModel, path => {
    required(path.city);
    required(path.street);
    maxLength(path.street, 200);
    required(path.houseNumber);
    required(path.description);
  });

  protected readonly addressLimitReached = computed(() => this.addresses().length >= this.maxAddresses);

  constructor() {
    this.lookupService.getTypesByGroup(LOOKUP_GROUPS.CITY).subscribe(cities => this.cities.set(cities));

    // ACC-011: en az bir adres eklenmeden sonraki adima gecilemez - sihirbazin ortak state'ine yansitilir.
    effect(() => {
      const addresses = this.addresses();
      this.formState.addressesValid.set(addresses.length > 0);
      this.formState.addressesValue.set(addresses.map(toAddressInfo));
    });
  }

  protected cityName(cityId: string): string {
    return this.cities().find(city => String(city.gnlTpId) === cityId)?.name ?? '—';
  }

  protected toggleAddressMenu(index: number, event: Event): void {
    event.stopPropagation();
    this.openAddressMenuIndex.set(this.openAddressMenuIndex() === index ? null : index);
  }

  @HostListener('document:click')
  protected closeAddressMenu(): void {
    this.openAddressMenuIndex.set(null);
  }

  protected removeAddress(index: number): void {
    this.openAddressMenuIndex.set(null);
    this.addresses.update(list => list.filter((_, i) => i !== index));
  }

  protected openAddAddressModal(): void {
    if (this.addressLimitReached()) {
      return;
    }
    this.openAddressMenuIndex.set(null);
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
