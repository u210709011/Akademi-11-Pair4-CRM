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

  protected readonly autoAddedLines = computed(() => this.formState.basket().filter(line => line.isAutoAdded));

  protected readonly selectedAddress = computed<AddressResponse | null>(
    () => this.formState.addresses().find(address => address.id === this.formState.selectedAddressId()) ?? null
  );

  // Backend OrderSummaryResponse sadece tek bir totalAmount donduruyor - One-time/Taxes/Activation
  // Fee gibi bir kirilim yok. Discount ise kampanya satirlarinin originalPrice/price farkindan
  // (frontend'de) hesaplanir - order-service da ayni indirimli fiyati zaten uyguladigi icin
  // totalAmount ile bu hesaplamanin toplami birbirini tutar.
  protected readonly grandTotal = computed(() => this.formState.totalAmount());

  protected readonly totalDiscount = computed(() =>
    this.formState
      .selectedLines()
      .filter(line => line.originalPrice !== null)
      .reduce((sum, line) => sum + (line.originalPrice! - line.price), 0)
  );

  protected readonly totalBeforeDiscount = computed(() => this.grandTotal() + this.totalDiscount());

  protected isConfigured(prodOfrId: number): boolean {
    return this.formState.isConfigured(prodOfrId);
  }

  // Kampanya ile eklenen offering'lerin tablo satirlarinin ustune bir kere baslik gostermek
  // icin - satirlar sepette ardisik geldigi icin index bazli "grup degisti mi" kontrolu yeterli.
  protected isFirstInCampaignGroup(index: number): boolean {
    const lines = this.formState.selectedLines();
    const line = lines[index];
    return line.cmpgId !== null && (index === 0 || lines[index - 1].cmpgId !== line.cmpgId);
  }

  protected cityName(cityId: number): string {
    return this.cities().find(city => city.gnlTpId === cityId)?.name ?? UNKNOWN;
  }

  protected editServiceAddress(): void {
    this.formState.requestedStep.set('configuration');
  }
}
