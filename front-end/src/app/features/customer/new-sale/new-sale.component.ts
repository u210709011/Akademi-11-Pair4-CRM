import { NgComponentOutlet } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, Injectable, Type, computed, effect, inject, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { BasketItemRequest, ItemCharValsRequest, OrderConfigurationRequest, OrderItemSummaryResponse, OrderService } from '../../../core/order';
import { AddressResponse, CustomerService } from '../../../core/customer';
import { I18nService } from '../../../core/i18n';
import { LookupService } from '../../../core/lookup';
import { ProductOfferingCharUse } from '../../../core/product';
import { ConfigurationStepComponent } from './steps/configuration/configuration-step.component';
import { OfferSelectionComponent } from './steps/offer-selection/offer-selection.component';
import { ReviewStepComponent } from './steps/review/review-step.component';

type NewSaleStep = 'offer' | 'configuration' | 'review';

interface StepDefinition {
  key: NewSaleStep;
  labelKey: string;
  component: Type<unknown>;
}

// Basket satiri - Offer Selection'da toplanir, Configuration'da charVals dolar, Review'da gosterilir.
export interface BasketLine {
  prodOfrId: number;
  offerName: string;
  price: number;
  cmpgId: number | null;
  cmpgName: string | null;
  // Sadece Catalog tab'inden eklenirse biliniyor (secili katalog) - Campaign tab'inden eklenirse null.
  catalogName: string | null;
  // product-offering-relations'tan (mandatory=true) otomatik eklenen zorunlu urunler icin.
  isAutoAdded: boolean;
  triggeredBy: number | null;
}

interface RequiredOffering {
  productOfferingId: number;
  name: string;
  price: number;
}

// Adim component'leri NgComponentOutlet ile degistigi icin state kendi icinde degil, bu serviste tutulur.
@Injectable()
export class NewSaleFormStateService {
  private readonly lookupService = inject(LookupService);
  // Hangi charId'lerin secim listesi (GNL_CHAR_VAL) oldugunu belirler - saveConfiguration
  // isteginde charValId mi yoksa serbest metin (val) mi gonderilecegine karar vermek icin.
  private readonly selectableCharIds = signal<ReadonlySet<number>>(new Set());

  constructor() {
    this.lookupService.getCharacteristicValues().subscribe(values => {
      this.selectableCharIds.set(new Set(values.map(v => v.charId)));
    });
  }

  readonly basket = signal<BasketLine[]>([]);
  readonly custOrdId = signal<number | null>(null);
  readonly orderItems = signal<OrderItemSummaryResponse[]>([]);
  readonly totalAmount = signal(0);

  readonly isValidatingBasket = signal(false);
  readonly basketError = signal<string | null>(null);

  // Offer Selection yuklenirken bir kere doldurulur (mandatory=true, active=true iliskiler,
  // hedef offering'in ad/fiyatiyla zenginlestirilmis) - sepete zorunlu urun otomatik eklemek icin.
  readonly requiredOfferingsMap = signal<Record<number, RequiredOffering[]>>({});

  // Configuration adiminda basket'teki her offering icin bir kere cekilip burada saklanir -
  // Review adimi da isConfigured() uzerinden ayni veriye erismesi gerektigi icin.
  readonly charUsesByOffering = signal<Record<number, ProductOfferingCharUse[]>>({});

  // Configuration adiminda Service Address karti icin - custId sahibinin adres listesi ve secili adres.
  readonly custId = signal(0);
  readonly addresses = signal<AddressResponse[]>([]);
  readonly selectedAddressId = signal<number | null>(null);

  // Review & Submit ekraninda gosterilir - NgComponentOutlet ile ayrilan adim component'leri
  // arasinda paylasilmasi gerektigi icin burada tutulur.
  readonly customerName = signal('');
  readonly billingAccountNo = signal('');
  readonly isSubmittingOrder = signal(false);
  readonly submitError = signal<string | null>(null);

  // Review'daki "duzenle" kalemi gibi yerlerden bir onceki adima donmek icin - adim gecisi
  // NewSaleComponent'te (activeStep) yonetildigi icin bu sinyal uzerinden istek iletilir.
  readonly requestedStep = signal<NewSaleStep | null>(null);

  // Karakteristik degerleri - Configuration adiminda toplanip Review'da da gosterildigi icin
  // (adim component'leri NgComponentOutlet ile yok edildigi icin) burada tutulur.
  // prodOfrId -> charId(string) -> secilen deger (select icin charValId'nin string hali, text icin ham metin).
  readonly charValues = signal<Record<number, Record<string, string>>>({});

  isConfigured(prodOfrId: number): boolean {
    const charUses = this.charUsesByOffering()[prodOfrId];
    if (!charUses || charUses.length === 0) {
      return false;
    }
    const mandatory = charUses.filter(cu => cu.mandatory && cu.active);
    const values = this.charValues()[prodOfrId] ?? {};
    return mandatory.every(cu => (values[String(cu.characteristicId)] ?? '').trim().length > 0);
  }

  // Donus degeri: bu ekleme sonucu otomatik eklenen zorunlu urunlerin isimleri (toast mesaji icin).
  addToBasket(line: BasketLine): string[] {
    if (this.basket().some(item => item.prodOfrId === line.prodOfrId)) {
      return [];
    }
    this.basket.update(items => [...items, line]);

    const addedRequiredNames: string[] = [];
    const requiredOfferings = this.requiredOfferingsMap()[line.prodOfrId] ?? [];
    for (const required of requiredOfferings) {
      if (this.basket().some(item => item.prodOfrId === required.productOfferingId)) {
        continue;
      }
      this.basket.update(items => [
        ...items,
        {
          prodOfrId: required.productOfferingId,
          offerName: required.name,
          price: required.price,
          cmpgId: null,
          cmpgName: null,
          catalogName: line.catalogName,
          isAutoAdded: true,
          triggeredBy: line.prodOfrId
        }
      ]);
      addedRequiredNames.push(required.name);
    }
    return addedRequiredNames;
  }

  removeFromBasket(prodOfrId: number): void {
    this.basket.update(items => {
      const remaining = items.filter(item => item.prodOfrId !== prodOfrId);
      // Kaldirilan urun tetikledigi zorunlu urunu, baska hicbir kalan urun hala gerektirmiyorsa kaldir.
      return remaining.filter(item => {
        if (!item.isAutoAdded || item.triggeredBy !== prodOfrId) {
          return true;
        }
        return remaining.some(other =>
          !other.isAutoAdded &&
          (this.requiredOfferingsMap()[other.prodOfrId] ?? []).some(r => r.productOfferingId === item.prodOfrId)
        );
      });
    });
  }

  clearBasket(): void {
    this.basket.set([]);
  }

  toBasketItemRequests(): BasketItemRequest[] {
    return this.basket().map(line => ({ prodOfrId: line.prodOfrId, cmpgId: line.cmpgId, charVals: [] }));
  }

  // Configuration adiminda toplanan karakteristik degerlerini saveConfiguration icin hazirlar.
  // custOrdItemId eslesmesi orderItems'tan (createOrder yanitindan) gelir.
  toOrderConfigurationRequest(): OrderConfigurationRequest {
    const items: ItemCharValsRequest[] = this.orderItems().map(item => {
      const charUses = this.charUsesByOffering()[item.prodOfrId] ?? [];
      const values = this.charValues()[item.prodOfrId] ?? {};
      const selectable = this.selectableCharIds();
      const charVals = charUses
        .filter(cu => (values[String(cu.characteristicId)] ?? '').trim().length > 0)
        .map(cu => {
          const raw = values[String(cu.characteristicId)];
          const isSelect = selectable.has(cu.characteristicId);
          return {
            charId: cu.characteristicId,
            charValId: isSelect ? Number(raw) : null,
            val: isSelect ? null : raw
          };
        });
      return { custOrdItemId: item.custOrdItemId, charVals };
    });

    return {
      items,
      addressId: this.selectedAddressId(),
      newAddress: null
    };
  }
}

@Component({
  selector: 'app-new-sale',
  imports: [NgComponentOutlet, RouterLink],
  templateUrl: './new-sale.component.html',
  styleUrl: './new-sale.component.scss',
  changeDetection: ChangeDetectionStrategy.Eager,
  providers: [NewSaleFormStateService]
})
export class NewSaleComponent {
  protected readonly i18n = inject(I18nService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly customerService = inject(CustomerService);
  private readonly orderService = inject(OrderService);
  protected readonly formState = inject(NewSaleFormStateService);

  protected readonly custId = Number(this.route.snapshot.paramMap.get('custId'));
  protected readonly custAcctId = Number(this.route.snapshot.paramMap.get('custAcctId'));

  protected readonly steps: StepDefinition[] = [
    { key: 'offer', labelKey: 'newSale.stepOfferSelection', component: OfferSelectionComponent },
    { key: 'configuration', labelKey: 'newSale.stepConfiguration', component: ConfigurationStepComponent },
    { key: 'review', labelKey: 'newSale.stepReview', component: ReviewStepComponent }
  ];

  protected readonly activeStep = signal<NewSaleStep>('offer');
  private readonly unlockedIndex = signal(0);

  protected readonly activeStepIndex = computed(() => this.steps.findIndex(step => step.key === this.activeStep()));

  protected readonly activeStepComponent = computed(
    () => this.steps.find(step => step.key === this.activeStep())?.component ?? null
  );

  private readonly isSavingConfiguration = signal(false);

  protected readonly isNextDisabled = computed(() => {
    if (this.formState.isValidatingBasket() || this.formState.isSubmittingOrder() || this.isSavingConfiguration()) {
      return true;
    }
    if (this.activeStep() === 'offer') {
      return this.formState.basket().length === 0;
    }
    return false;
  });

  protected readonly nextButtonLabel = computed(() =>
    this.activeStep() === 'review' ? this.i18n.t('newSale.submitBtn') : this.i18n.t('newSale.nextBtn')
  );

  constructor() {
    this.formState.custId.set(this.custId);

    this.customerService.getById(this.custId).subscribe(detail => {
      const account = detail.accounts.find(acc => acc.custAcctId === this.custAcctId);
      this.formState.billingAccountNo.set(account?.accountNo ?? '');
      this.formState.selectedAddressId.set(account?.addressId ?? null);
    });

    this.customerService.getIndividual(this.custId).subscribe(individual => {
      this.formState.customerName.set([individual.firstName, individual.lastName].filter(Boolean).join(' '));
    });

    this.customerService.getAddresses(this.custId).subscribe(addresses => {
      this.formState.addresses.set(addresses);
    });

    effect(() => {
      const requested = this.formState.requestedStep();
      if (requested) {
        this.activeStep.set(requested);
        this.formState.requestedStep.set(null);
      }
    });
  }

  protected stepState(step: NewSaleStep): 'done' | 'active' | 'pending' {
    const index = this.steps.findIndex(s => s.key === step);
    if (index < this.activeStepIndex()) {
      return 'done';
    }
    return index === this.activeStepIndex() ? 'active' : 'pending';
  }

  protected next(): void {
    if (this.activeStep() === 'offer') {
      this.validateBasketThenCreateOrder();
      return;
    }
    if (this.activeStep() === 'configuration') {
      this.saveConfigurationThenAdvance();
      return;
    }
    if (this.activeStep() === 'review') {
      this.submitOrder();
      return;
    }
    this.advanceStep();
  }

  protected previous(): void {
    const currentIndex = this.activeStepIndex();
    const previousStep = this.steps[currentIndex - 1];
    if (previousStep) {
      this.activeStep.set(previousStep.key);
    }
  }

  private validateBasketThenCreateOrder(): void {
    this.formState.isValidatingBasket.set(true);
    this.formState.basketError.set(null);

    const items = this.formState.toBasketItemRequests();

    this.orderService.validateBasket({ custId: this.custId, custAcctId: this.custAcctId, items }).subscribe({
      next: () => {
        this.orderService.createOrder({ custId: this.custId, custAcctId: this.custAcctId, items }).subscribe({
          next: response => {
            this.formState.isValidatingBasket.set(false);
            this.formState.custOrdId.set(response.custOrdId);
            this.formState.orderItems.set(response.items);
            this.formState.totalAmount.set(response.totalAmount);
            this.advanceStep();
          },
          error: (httpError: HttpErrorResponse) => {
            this.formState.isValidatingBasket.set(false);
            this.formState.basketError.set(this.extractErrorMessage(httpError));
          }
        });
      },
      error: (httpError: HttpErrorResponse) => {
        this.formState.isValidatingBasket.set(false);
        this.formState.basketError.set(this.extractErrorMessage(httpError));
      }
    });
  }

  private saveConfigurationThenAdvance(): void {
    const custOrdId = this.formState.custOrdId();
    if (!custOrdId) {
      return;
    }

    this.isSavingConfiguration.set(true);
    this.formState.basketError.set(null);

    this.orderService.saveConfiguration(custOrdId, this.formState.toOrderConfigurationRequest()).subscribe({
      next: response => {
        this.isSavingConfiguration.set(false);
        this.formState.totalAmount.set(response.totalAmount);
        this.advanceStep();
      },
      error: (httpError: HttpErrorResponse) => {
        this.isSavingConfiguration.set(false);
        this.formState.basketError.set(this.extractErrorMessage(httpError));
      }
    });
  }

  private submitOrder(): void {
    const custOrdId = this.formState.custOrdId();
    if (!custOrdId) {
      return;
    }

    this.formState.isSubmittingOrder.set(true);
    this.formState.submitError.set(null);

    this.orderService.finishOrder(custOrdId).subscribe({
      next: () => {
        this.formState.isSubmittingOrder.set(false);
        this.router.navigate(['/detail-customer', this.custId]);
      },
      error: (httpError: HttpErrorResponse) => {
        this.formState.isSubmittingOrder.set(false);
        this.formState.submitError.set(this.extractErrorMessage(httpError));
      }
    });
  }

  private extractErrorMessage(httpError: HttpErrorResponse): string {
    return (httpError.error as { message?: string } | null)?.message ?? this.i18n.t('newSale.basketValidationError');
  }

  private advanceStep(): void {
    const currentIndex = this.activeStepIndex();
    const nextStep = this.steps[currentIndex + 1];
    if (nextStep) {
      this.activeStep.set(nextStep.key);
      this.unlockedIndex.update(index => Math.max(index, currentIndex + 1));
    }
  }

  protected cancel(): void {
    this.router.navigate(['/detail-customer', this.custId]);
  }
}
