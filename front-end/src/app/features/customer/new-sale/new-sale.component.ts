import { NgComponentOutlet } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, Injectable, Type, computed, effect, inject, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { BasketItemRequest, OrderService } from '../../../core/order';
import { AddressResponse, CustomerService } from '../../../core/customer';
import { I18nService } from '../../../core/i18n';
import { ConfigurationStepComponent } from './steps/configuration/configuration-step.component';
import { OfferSelectionComponent } from './steps/offer-selection/offer-selection.component';
import { ReviewStepComponent } from './steps/review/review-step.component';
import { NEW_SALE_MOCK_MODE } from './mock/new-sale-mock.config';
import { MOCK_CHARACTERISTICS_BY_OFFERING, MOCK_OFFERINGS, MOCK_REQUIRED_PRODUCTS } from './mock/new-sale-mock.data';

const MOCK_CUST_ORD_ID = 900001;
// GECICI MOCK - BsnInter backend'de olusuyor ama OrderSummaryResponse hic disariya expose etmiyor,
// bu yuzden Review & Submit'teki Business Interaction ID sadece mock modda rastgele uretilir.
function generateMockBsnInterId(): number {
  return 400000 + Math.floor(Math.random() * 99999);
}

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
  // GECICI MOCK ALANLARI - zorunlu urun otomatik ekleme sadece mock modda calisir (bkz. MOCK_REQUIRED_PRODUCTS).
  isAutoAdded: boolean;
  triggeredBy: number | null;
}

// Adim component'leri NgComponentOutlet ile degistigi icin state kendi icinde degil, bu serviste tutulur.
@Injectable()
export class NewSaleFormStateService {
  readonly basket = signal<BasketLine[]>([]);
  readonly custOrdId = signal<number | null>(null);
  // GECICI MOCK - bkz. generateMockBsnInterId, backend bu alani hic dondurmuyor.
  readonly bsnInterId = signal<number | null>(null);

  readonly isValidatingBasket = signal(false);
  readonly basketError = signal<string | null>(null);

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

  // GECICI MOCK - karakteristik degerleri, Configuration adiminda toplanip Review'da da
  // gosterildigi icin (adim component'leri NgComponentOutlet ile yok edildigi icin) burada tutulur.
  readonly charValues = signal<Record<number, Record<string, string>>>({});

  isConfigured(prodOfrId: number): boolean {
    if (!NEW_SALE_MOCK_MODE) {
      return false;
    }
    const fields = MOCK_CHARACTERISTICS_BY_OFFERING[prodOfrId] ?? [];
    if (fields.length === 0) {
      return false;
    }
    const values = this.charValues()[prodOfrId] ?? {};
    return fields.filter(f => f.required).every(f => (values[f.key] ?? '').trim().length > 0);
  }

  // Donus degeri: bu ekleme sonucu otomatik eklenen zorunlu urunlerin isimleri (toast mesaji icin).
  addToBasket(line: BasketLine): string[] {
    if (this.basket().some(item => item.prodOfrId === line.prodOfrId)) {
      return [];
    }
    this.basket.update(items => [...items, line]);

    const addedRequiredNames: string[] = [];
    if (NEW_SALE_MOCK_MODE) {
      const requiredIds = MOCK_REQUIRED_PRODUCTS[line.prodOfrId] ?? [];
      for (const requiredId of requiredIds) {
        if (this.basket().some(item => item.prodOfrId === requiredId)) {
          continue;
        }
        const requiredOffering = MOCK_OFFERINGS.find(o => o.productOfferingId === requiredId);
        if (!requiredOffering) {
          continue;
        }
        this.basket.update(items => [
          ...items,
          {
            prodOfrId: requiredOffering.productOfferingId,
            offerName: requiredOffering.name,
            price: requiredOffering.totalPrice,
            cmpgId: null,
            cmpgName: null,
            catalogName: line.catalogName,
            isAutoAdded: true,
            triggeredBy: line.prodOfrId
          }
        ]);
        addedRequiredNames.push(requiredOffering.name);
      }
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
          !other.isAutoAdded && (MOCK_REQUIRED_PRODUCTS[other.prodOfrId] ?? []).includes(item.prodOfrId)
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

  protected readonly isNextDisabled = computed(() => {
    if (this.formState.isValidatingBasket() || this.formState.isSubmittingOrder()) {
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

    // GECICI MOCK MODU - backend product-service hazir olana kadar validate-basket/createOrder
    // gercek cagrilari atlanip sahte bir custOrdId ile devam edilir. Kaldirmak icin bu bloğu silin.
    if (NEW_SALE_MOCK_MODE) {
      this.formState.isValidatingBasket.set(false);
      this.formState.custOrdId.set(MOCK_CUST_ORD_ID);
      this.formState.bsnInterId.set(generateMockBsnInterId());
      this.advanceStep();
      return;
    }

    const items = this.formState.toBasketItemRequests();

    this.orderService.validateBasket({ custId: this.custId, custAcctId: this.custAcctId, items }).subscribe({
      next: () => {
        this.orderService.createOrder({ custId: this.custId, custAcctId: this.custAcctId, items }).subscribe({
          next: response => {
            this.formState.isValidatingBasket.set(false);
            this.formState.custOrdId.set(response.custOrdId);
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

  private submitOrder(): void {
    const custOrdId = this.formState.custOrdId();
    if (!custOrdId) {
      return;
    }

    this.formState.isSubmittingOrder.set(true);
    this.formState.submitError.set(null);

    // GECICI MOCK MODU - bkz. validateBasketThenCreateOrder yorumu.
    if (NEW_SALE_MOCK_MODE) {
      this.formState.isSubmittingOrder.set(false);
      this.router.navigate(['/detail-customer', this.custId]);
      return;
    }

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
