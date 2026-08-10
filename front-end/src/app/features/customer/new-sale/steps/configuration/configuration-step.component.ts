import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, computed, effect, inject, signal } from '@angular/core';
import { form, FormField, required } from '@angular/forms/signals';
import { AddressEditRequest, AddressResponse, CustomerService } from '../../../../../core/customer';
import { I18nService } from '../../../../../core/i18n';
import { CharacteristicValue, GnlType, LOOKUP_GROUPS, LookupService } from '../../../../../core/lookup';
import { ProductService } from '../../../../../core/product';
import { BasketLine, NewSaleFormStateService } from '../../new-sale.component';

const UNKNOWN = '—';

interface AddressFormModel {
  city: string;
  street: string;
  houseNumber: string;
  description: string;
}

const EMPTY_ADDRESS_FORM: AddressFormModel = { city: '', street: '', houseNumber: '', description: '' };

interface CharacteristicOption {
  value: string;
  label: string;
}

// Configuration adiminda gosterilen bir karakteristik alani - product-offering-char-uses (sema)
// ve characteristic-values (secim listesi varsa) birlestirilerek olusturulur.
interface CharacteristicField {
  key: string;
  label: string;
  type: 'select' | 'text';
  required: boolean;
  options?: CharacteristicOption[];
  placeholder?: string;
}

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
  private readonly lookupService = inject(LookupService);
  private readonly productService = inject(ProductService);
  protected readonly formState = inject(NewSaleFormStateService);

  protected readonly cities = signal<GnlType[]>([]);
  private readonly characteristicValues = signal<CharacteristicValue[]>([]);
  private readonly loadedCharUseOfferingIds = new Set<number>();

  constructor() {
    this.lookupService.getTypesByGroup(LOOKUP_GROUPS.CITY).subscribe(cities => this.cities.set(cities));
    this.lookupService.getCharacteristicValues().subscribe(values => this.characteristicValues.set(values));

    // Sepetteki her offering icin karakteristik semasini bir kere ceker (formState.charUsesByOffering'e
    // yazar ki Review adimi da ayni veriyi tekrar cekmeden kullanabilsin).
    effect(() => {
      for (const line of this.configurableLines()) {
        if (this.loadedCharUseOfferingIds.has(line.prodOfrId)) {
          continue;
        }
        this.loadedCharUseOfferingIds.add(line.prodOfrId);
        this.productService.getCharUsesByOffering(line.prodOfrId).subscribe(charUses => {
          this.formState.charUsesByOffering.update(all => ({ ...all, [line.prodOfrId]: charUses }));
        });
      }
    });
  }

  protected readonly selectedAddress = computed<AddressResponse | null>(
    () => this.formState.addresses().find(address => address.id === this.formState.selectedAddressId()) ?? null
  );

  // Zorunlu urun olarak otomatik eklenenler (ör. Wi-Fi Router) icin ayri config karti gosterilmiyor.
  protected readonly configurableLines = computed(() => this.formState.basket().filter(line => !line.isAutoAdded));

  protected readonly collapsedProductIds = signal<Set<number>>(new Set());

  protected fieldsFor(prodOfrId: number): CharacteristicField[] {
    const charUses = this.formState.charUsesByOffering()[prodOfrId] ?? [];
    return charUses
      .filter(cu => cu.active)
      .map(cu => {
        const values = this.characteristicValues().filter(v => v.charId === cu.characteristicId && v.active);
        return {
          key: String(cu.characteristicId),
          label: cu.characteristicName,
          required: cu.mandatory,
          type: values.length > 0 ? 'select' : 'text',
          options: values.map(v => ({ value: String(v.charValId), label: v.val }))
        };
      });
  }

  protected fieldValue(prodOfrId: number, key: string): string {
    return this.formState.charValues()[prodOfrId]?.[key] ?? '';
  }

  protected setFieldValue(prodOfrId: number, key: string, value: string): void {
    this.formState.charValues.update(all => ({
      ...all,
      [prodOfrId]: { ...all[prodOfrId], [key]: value }
    }));
  }

  // Mockup: tum zorunlu alanlar doldurulunca kart otomatik daralir (collapse).
  // Text alanlarda (input) yerine (blur) ile cagrilir - yoksa son zorunlu alana
  // yazilan ilk karakterde henuz yazma bitmeden kart kapanirdi.
  protected checkAutoCollapse(prodOfrId: number): void {
    if (this.formState.isConfigured(prodOfrId)) {
      this.collapsedProductIds.update(current => new Set(current).add(prodOfrId));
    }
  }

  protected isConfigured(line: BasketLine): boolean {
    return this.formState.isConfigured(line.prodOfrId);
  }

  protected isCollapsed(prodOfrId: number): boolean {
    return this.collapsedProductIds().has(prodOfrId);
  }

  protected toggleCollapsed(prodOfrId: number): void {
    this.collapsedProductIds.update(current => {
      const next = new Set(current);
      if (next.has(prodOfrId)) {
        next.delete(prodOfrId);
      } else {
        next.add(prodOfrId);
      }
      return next;
    });
  }

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
    return this.cities().find(city => city.gnlTpId === cityId)?.name ?? UNKNOWN;
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
          (httpError.error as { message?: string } | null)?.message ??
            (httpError.status === 409 ? this.i18n.t('detail.maxAddressesReached') : this.i18n.t('detail.addressSaveError'))
        );
      }
    });
  }
}
