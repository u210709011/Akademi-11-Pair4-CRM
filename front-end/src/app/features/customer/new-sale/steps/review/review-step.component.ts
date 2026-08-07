import { DecimalPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { AddressResponse } from '../../../../../core/customer';
import { I18nService } from '../../../../../core/i18n';
import { GnlType, LOOKUP_GROUPS, LookupService } from '../../../../../core/lookup';
import { NewSaleFormStateService } from '../../new-sale.component';

const UNKNOWN = '—';

@Component({
  selector: 'app-review-step',
  imports: [DecimalPipe],
  templateUrl: './review-step.component.html',
  styleUrl: './review-step.component.scss',
  changeDetection: ChangeDetectionStrategy.Eager
})
export class ReviewStepComponent {
  protected readonly i18n = inject(I18nService);
  protected readonly formState = inject(NewSaleFormStateService);
  private readonly lookupService = inject(LookupService);

  protected readonly cities = signal<GnlType[]>([]);

  protected readonly orderDate = new Date().toLocaleDateString('tr-TR');

  constructor() {
    this.lookupService.getTypesByGroup(LOOKUP_GROUPS.CITY).subscribe(cities => this.cities.set(cities));
  }

  protected readonly selectedLines = computed(() => this.formState.basket().filter(line => !line.isAutoAdded));
  protected readonly autoAddedLines = computed(() => this.formState.basket().filter(line => line.isAutoAdded));

  protected readonly selectedAddress = computed<AddressResponse | null>(
    () => this.formState.addresses().find(address => address.id === this.formState.selectedAddressId()) ?? null
  );

  // Backend OrderSummaryResponse sadece tek bir totalAmount donduruyor - Monthly/One-time/Discounts/
  // Taxes/Activation Fee gibi bir kirilim yok, o yuzden Price Summary karti sadece bunu gosterir.
  protected readonly grandTotal = computed(() => this.formState.totalAmount());

  protected isConfigured(prodOfrId: number): boolean {
    return this.formState.isConfigured(prodOfrId);
  }

  protected cityName(cityId: number): string {
    return this.cities().find(city => city.gnlTpId === cityId)?.name ?? UNKNOWN;
  }

  protected editServiceAddress(): void {
    this.formState.requestedStep.set('configuration');
  }
}
