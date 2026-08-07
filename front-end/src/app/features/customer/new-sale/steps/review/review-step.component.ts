import { DecimalPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { AddressResponse } from '../../../../../core/customer';
import { I18nService } from '../../../../../core/i18n';
import { GnlType, LOOKUP_GROUPS, LookupService } from '../../../../../core/lookup';
import { NewSaleFormStateService } from '../../new-sale.component';
import { MOCK_ACTIVATION_FEE, MOCK_ONE_TIME_OFFERING_IDS, MOCK_TAX_RATE } from '../../mock/new-sale-mock.data';

const UNKNOWN = '—';

// Business Interaction ID mockup'ta var ama OrderSummaryResponse'da hic donmuyor (BsnInter entity
// backend'de olusuyor ama disariya expose edilmiyor) - formState.bsnInterId sadece mock modda
// rastgele uretilir (bkz. generateMockBsnInterId, new-sale.component.ts).
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

  // GECICI MOCK - Monthly/One-time ayrimi ve vergi/aktivasyon ucreti backend'de hesaplanmiyor,
  // sadece mock modda Price Summary karti icin (bkz. new-sale-mock.data.ts).
  protected readonly monthlyCharges = computed(() =>
    this.formState.basket().reduce((sum, line) => (MOCK_ONE_TIME_OFFERING_IDS.has(line.prodOfrId) ? sum : sum + line.price), 0)
  );

  protected readonly oneTimeCharges = computed(() =>
    this.formState.basket().reduce((sum, line) => (MOCK_ONE_TIME_OFFERING_IDS.has(line.prodOfrId) ? sum + line.price : sum), 0)
  );

  protected readonly discounts = computed(() => 0);
  protected readonly activationFee = MOCK_ACTIVATION_FEE;

  protected readonly taxes = computed(
    () => (this.monthlyCharges() + this.oneTimeCharges() + this.activationFee) * MOCK_TAX_RATE
  );

  protected readonly grandTotal = computed(
    () => this.monthlyCharges() + this.oneTimeCharges() - this.discounts() + this.taxes() + this.activationFee
  );

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
