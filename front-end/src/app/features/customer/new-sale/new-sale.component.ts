import { NgComponentOutlet } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, Injectable, Type, computed, effect, inject, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { BasketItemRequest, ItemCharValsRequest, OrderConfigurationRequest, OrderItemSummaryResponse, OrderService } from '../data-access/order';
import { AddressResponse, CustomerService } from '../data-access/customer';
import { TranslateService, TranslatePipe } from '@ngx-translate/core';
import { LookupService } from '../../../core/lookup';
import { ProductOfferingCharUse } from '../data-access/product';
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
  // Backend'de sifirla soldan 6 haneye doldurulmus (bkz. ProductServiceDefaults.formatNo) -
  // Configuration/Review'da OFR- prefix'iyle gosterilir, iliski/routing icin prodOfrId kullanilir.
  prodOfrNo: string;
  offerName: string;
  price: number;
  // Kampanya indirimi uygulanmadan onceki fiyat - sadece cmpgId'li satirlarda dolu, Review'da
  // "Discounts" satirini hesaplamak icin. Kampanyasiz satirlarda/otomatik eklenenlerde null.
  originalPrice: number | null;
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
  productOfferingNo: string;
  name: string;
  price: number;
}

// Bir kampanya ile eklenen offering'ler ayri urunler gibi degil, tek bir kampanya biriminin
// icinde (nested) gosterilir - Basket/Configuration/Review'in ucu de aynı gruplamayi kullanir.
// cmpgId === null olan satirlar kendi baslarina birer grup (tek elemanli).
export interface BasketGroup {
  cmpgId: number | null;
  cmpgName: string | null;
  lines: BasketLine[];
  totalPrice: number;
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

  // Zorunlu urun olarak otomatik eklenenler haric, musterinin bilfiil sectigi satirlar.
  readonly selectedLines = computed(() => this.basket().filter(line => !line.isAutoAdded));

  // selectedLines'i kampanyaya gore gruplar - Basket paneli/Configuration/Review'in ucu de
  // ayni gruplamayi paylasir (bkz. BasketGroup yorumu).
  readonly selectedGroups = computed<BasketGroup[]>(() => {
    const groups: BasketGroup[] = [];
    const groupIndexByCmpgId = new Map<number, number>();
    for (const line of this.selectedLines()) {
      if (line.cmpgId === null) {
        groups.push({ cmpgId: null, cmpgName: null, lines: [line], totalPrice: line.price });
        continue;
      }
      const existingIndex = groupIndexByCmpgId.get(line.cmpgId);
      if (existingIndex === undefined) {
        groupIndexByCmpgId.set(line.cmpgId, groups.length);
        groups.push({ cmpgId: line.cmpgId, cmpgName: line.cmpgName, lines: [line], totalPrice: line.price });
      } else {
        const group = groups[existingIndex];
        group.lines.push(line);
        group.totalPrice += line.price;
      }
    }
    return groups;
  });

  readonly custOrdId = signal<number | null>(null);
  // FR-017: Submit sonrasi basari modalinda "Business Interaction Number" gostermek icin.
  readonly bsnInterId = signal<number | null>(null);
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
  // Offer Selection'da "Already Active" kontrolu (ACC-011/BR-05) icin - hangi hesaba satis yapiliyor.
  readonly custAcctId = signal(0);
  readonly addresses = signal<AddressResponse[]>([]);
  readonly selectedAddressId = signal<number | null>(null);

  // Review & Submit ekraninda gosterilir - NgComponentOutlet ile ayrilan adim component'leri
  // arasinda paylasilmasi gerektigi icin burada tutulur.
  readonly customerName = signal('');
  readonly billingAccountNo = signal('');
  // Configuration'da servis adresi degistiginde fatura hesabinin gercek adresini de guncellemek
  // icin (updateBillingAccount accountName/accountDesc'i de istiyor, adresle birlikte gonderilir).
  readonly billingAccountName = signal<string | null>(null);
  readonly billingAccountDesc = signal<string | null>(null);
  readonly isSubmittingOrder = signal(false);
  readonly submitError = signal<string | null>(null);

  // Review'daki "duzenle" kalemi gibi yerlerden bir onceki adima donmek icin - adim gecisi
  // NewSaleComponent'te (activeStep) yonetildigi icin bu sinyal uzerinden istek iletilir.
  readonly requestedStep = signal<NewSaleStep | null>(null);

  // Karakteristik degerleri - Configuration adiminda toplanip Review'da da gosterildigi icin
  // (adim component'leri NgComponentOutlet ile yok edildigi icin) burada tutulur.
  // prodOfrId -> charId(string) -> secilen deger (select icin charValId'nin string hali, text icin ham metin).
  readonly charValues = signal<Record<number, Record<string, string>>>({});

  // FR-015 ACC-009: sepetteki tum konfigure edilebilir urunlerin zorunlu karakteristikleri
  // dolduruldu MU ve bir servis adresi secildi mi - Configuration adiminda Next'i acmak icin.
  readonly isConfigurationComplete = computed(() => {
    const configurableLines = this.basket().filter(line => !line.isAutoAdded);
    return configurableLines.every(line => this.isConfigured(line.prodOfrId)) && this.selectedAddressId() !== null;
  });

  isConfigured(prodOfrId: number): boolean {
    const charUses = this.charUsesByOffering()[prodOfrId];
    if (!charUses) {
      return false; // sema henuz yuklenmedi
    }
    const mandatory = charUses.filter(cu => cu.mandatory && cu.active);
    if (mandatory.length === 0) {
      return true; // zorunlu karakteristigi yok - doldurulacak bir sey yok, zaten "configured"
    }
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
          prodOfrNo: required.productOfferingNo,
          offerName: required.name,
          price: required.price,
          originalPrice: null,
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
    this.removeLines(new Set([prodOfrId]));
  }

  // Bir kampanyanin TUM teklifleri (kampanya sepette tek bir birim gibi davranir) - trash icon
  // kampanya grubunun basinda, tek tek offering'ler icin degil.
  removeCampaignFromBasket(cmpgId: number): void {
    const idsToRemove = new Set(this.basket().filter(item => item.cmpgId === cmpgId).map(item => item.prodOfrId));
    this.removeLines(idsToRemove);
  }

  private removeLines(prodOfrIds: ReadonlySet<number>): void {
    this.basket.update(items => {
      const remaining = items.filter(item => !prodOfrIds.has(item.prodOfrId));
      // Kaldirilan urunlerin tetikledigi zorunlu urunu, baska hicbir kalan urun hala gerektirmiyorsa kaldir.
      return remaining.filter(item => {
        if (!item.isAutoAdded || item.triggeredBy === null || !prodOfrIds.has(item.triggeredBy)) {
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

  // FR-017: basari modalinda "Add New Product" - ayni fatura hesabi icin sifirdan bir siparise
  // baslamak icin sepet/siparis/konfigurasyon state'ini temizler (adres/musteri bilgisi kalir).
  resetForNewOrder(): void {
    this.basket.set([]);
    this.custOrdId.set(null);
    this.bsnInterId.set(null);
    this.orderItems.set([]);
    this.totalAmount.set(0);
    this.charValues.set({});
    this.isValidatingBasket.set(false);
    this.basketError.set(null);
    this.isSubmittingOrder.set(false);
    this.submitError.set(null);
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
  imports: [NgComponentOutlet, RouterLink, TranslatePipe],
  templateUrl: './new-sale.component.html',
  styleUrl: './new-sale.component.scss',
  changeDetection: ChangeDetectionStrategy.Eager,
  providers: [NewSaleFormStateService]
})
export class NewSaleComponent {
  protected readonly translate = inject(TranslateService);
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

  // ACC-016: Cancel butonuna basildiginda dogrudan cikmadan once onay istenir.
  protected readonly isCancelConfirmOpen = signal(false);

  // FR-017 ACC-004: Submit basarili oldugunda yonlendirmeden once basari modali gosterilir.
  protected readonly isOrderSubmittedModalOpen = signal(false);

  protected readonly isNextDisabled = computed(() => {
    if (this.formState.isValidatingBasket() || this.formState.isSubmittingOrder() || this.isSavingConfiguration()) {
      return true;
    }
    if (this.activeStep() === 'offer') {
      return this.formState.basket().length === 0;
    }
    if (this.activeStep() === 'configuration') {
      return !this.formState.isConfigurationComplete();
    }
    return false;
  });

  // Next butonunun spinner gostermesi gereken durumlar - isNextDisabled'daki ilk kosulla ayni.
  protected readonly isNextLoading = computed(
    () => this.formState.isValidatingBasket() || this.formState.isSubmittingOrder() || this.isSavingConfiguration()
  );

  protected readonly nextButtonLabel = computed(() =>
    this.activeStep() === 'review' ? this.translate.instant('newSale.submitBtn') : this.translate.instant('newSale.nextBtn')
  );

  constructor() {
    this.formState.custId.set(this.custId);
    this.formState.custAcctId.set(this.custAcctId);

    this.customerService.getById(this.custId).subscribe(detail => {
      const account = detail.accounts.find(acc => acc.custAcctId === this.custAcctId);
      this.formState.billingAccountNo.set(account?.accountNo ?? '');
      this.formState.billingAccountName.set(account?.accountName ?? null);
      this.formState.billingAccountDesc.set(account?.accountDesc ?? null);
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

  protected goToBillingAccount(): void {
    this.isOrderSubmittedModalOpen.set(false);
    this.router.navigate(['/detail-customer', this.custId]);
  }

  protected startNewOrderForSameAccount(): void {
    this.isOrderSubmittedModalOpen.set(false);
    this.formState.resetForNewOrder();
    this.unlockedIndex.set(0);
    this.activeStep.set('offer');
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
            this.formState.bsnInterId.set(response.bsnInterId);
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
      next: response => {
        this.formState.isSubmittingOrder.set(false);
        this.formState.bsnInterId.set(response.bsnInterId);
        this.isOrderSubmittedModalOpen.set(true);
      },
      error: (httpError: HttpErrorResponse) => {
        this.formState.isSubmittingOrder.set(false);
        this.formState.submitError.set(this.extractErrorMessage(httpError));
      }
    });
  }

  private extractErrorMessage(httpError: HttpErrorResponse): string {
    return (httpError.error as { message?: string } | null)?.message ?? this.translate.instant('newSale.basketValidationError');
  }

  private advanceStep(): void {
    const currentIndex = this.activeStepIndex();
    const nextStep = this.steps[currentIndex + 1];
    if (nextStep) {
      this.activeStep.set(nextStep.key);
      this.unlockedIndex.update(index => Math.max(index, currentIndex + 1));
    }
  }

  protected openCancelConfirm(): void {
    this.isCancelConfirmOpen.set(true);
  }

  protected closeCancelConfirm(): void {
    this.isCancelConfirmOpen.set(false);
  }

  protected confirmCancel(): void {
    this.isCancelConfirmOpen.set(false);
    this.router.navigate(['/detail-customer', this.custId]);
  }
}
