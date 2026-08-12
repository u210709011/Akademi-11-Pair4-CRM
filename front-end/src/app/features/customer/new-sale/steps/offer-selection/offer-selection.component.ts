import { DecimalPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, effect, inject, signal, untracked } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { I18nService } from '../../../../../core/i18n';
import { OrderService } from '../../../../../core/order';
import {
  Campaign,
  CampaignOffering,
  ProductCatalog,
  ProductCatalogOffering,
  ProductOffering,
  ProductOfferingRelation,
  ProductService
} from '../../../../../core/product';
import { BasketLine, NewSaleFormStateService } from '../../new-sale.component';

type OfferTab = 'catalog' | 'campaigns';

// FR-013 ACC-014/Alt Senaryo 5: ilk sayfada en fazla 5 kayit, kalani sayfalama ile.
const RESULTS_PAGE_SIZE = 5;
// FR-013 validasyon tablosu: Prod Offer ID / Campaign ID yalnizca rakam, en fazla 20 hane;
// Prod Offer Name / Campaign Name en fazla 50 karakter.
const ID_FIELD_MAX_LENGTH = 20;
const NAME_FIELD_MAX_LENGTH = 50;

interface CatalogResultRow {
  productOfferingId: number;
  productOfferingNo: string;
  name: string;
  descr: string;
  price: number;
}

interface CampaignOfferingRow {
  productOfferingId: number;
  productOfferingNo: string;
  name: string;
  originalPrice: number;
  price: number;
  discountPct: number;
}

interface CampaignResultRow {
  campaignId: number;
  campaignNo: string;
  campaignCode: string;
  name: string;
  offerings: CampaignOfferingRow[];
  totalOriginalPrice: number;
  totalPrice: number;
}

// product-service'te filtreli/arama destekleyen bir GET endpoint yok (sadece getAll/getById),
// bu yuzden tum listeler bir kere cekilip Search butonuna basildiginda burada filtreleniyor.
@Component({
  selector: 'app-offer-selection',
  imports: [ReactiveFormsModule, DecimalPipe],
  templateUrl: './offer-selection.component.html',
  styleUrl: './offer-selection.component.scss',
  changeDetection: ChangeDetectionStrategy.Eager
})
export class OfferSelectionComponent {
  protected readonly i18n = inject(I18nService);
  private readonly formBuilder = inject(FormBuilder);
  private readonly productService = inject(ProductService);
  private readonly orderService = inject(OrderService);
  protected readonly formState = inject(NewSaleFormStateService);

  protected readonly activeTab = signal<OfferTab>('catalog');

  private readonly catalogs = signal<ProductCatalog[]>([]);
  private readonly campaigns = signal<Campaign[]>([]);
  private readonly offerings = signal<ProductOffering[]>([]);
  private readonly catalogOfferings = signal<ProductCatalogOffering[]>([]);
  private readonly campaignOfferings = signal<CampaignOffering[]>([]);

  protected readonly isLoadingCatalogData = signal(true);
  protected readonly loadError = signal(false);

  // FR-014 ACC-011/BR-05: bu hesabin zaten aktif sahip oldugu tekliflerin prodOfrId'leri -
  // "Already Active" rozeti icin.
  private readonly activeOfferingIds = signal<ReadonlySet<number>>(new Set());

  // FR-014 ACC-012/BR-04: bir offering'in cakisan (exclusive=true, active) oldugu diger
  // offering'lerin id'leri - sepete eklerken cakisma kontrolu icin.
  private readonly excludedOfferingIdsMap = signal<Record<number, ReadonlySet<number>>>({});

  protected readonly catalogForm = this.formBuilder.nonNullable.group({
    catalogId: '',
    offerId: '',
    offerName: ''
  });

  protected readonly campaignForm = this.formBuilder.nonNullable.group({
    catalogId: '',
    campaignRef: '',
    campaignName: ''
  });

  protected readonly catalogFieldErrors = signal({ offerId: false, offerName: false });
  protected readonly campaignFieldErrors = signal({ campaignRef: false, campaignName: false });

  // FR-013 ACC-005/009/Alt Senaryo 1: en az bir kriter doldurulmadan Search aktif olmamali;
  // doldurulmus alanlardan biri gecersizse de (ör. rakam-disi ID) aktif olmamali.
  protected readonly isCatalogSearchDisabled = computed(() => {
    const value = this.catalogFormValue();
    const hasCriteria = !!(value.catalogId || value.offerId || value.offerName);
    return !hasCriteria || Object.values(this.catalogFieldErrors()).some(hasError => hasError);
  });

  protected readonly isCampaignSearchDisabled = computed(() => {
    const value = this.campaignFormValue();
    const hasCriteria = !!(value.catalogId || value.campaignRef || value.campaignName);
    return !hasCriteria || Object.values(this.campaignFieldErrors()).some(hasError => hasError);
  });

  // Reactive Forms sinyal degil - degisiklikleri computed'lara yansitmak icin valueChanges'i sinyale cevirir.
  private readonly catalogFormValue = signal(this.catalogForm.getRawValue());
  private readonly campaignFormValue = signal(this.campaignForm.getRawValue());

  protected readonly hasSearchedCatalog = signal(false);
  protected readonly hasSearchedCampaign = signal(false);
  protected readonly catalogResults = signal<CatalogResultRow[]>([]);
  protected readonly campaignResults = signal<CampaignResultRow[]>([]);
  protected readonly expandedCampaignId = signal<number | null>(null);

  protected readonly catalogPage = signal(0);
  protected readonly pagedCatalogResults = computed(() =>
    this.catalogResults().slice(this.catalogPage() * RESULTS_PAGE_SIZE, this.catalogPage() * RESULTS_PAGE_SIZE + RESULTS_PAGE_SIZE)
  );
  protected readonly catalogTotalPages = computed(() => Math.max(1, Math.ceil(this.catalogResults().length / RESULTS_PAGE_SIZE)));
  protected readonly catalogPageNumbers = computed(() => Array.from({ length: this.catalogTotalPages() }, (_, i) => i));
  protected readonly catalogRangeLabel = computed(() => this.rangeLabel(this.catalogResults().length, this.catalogPage()));

  protected readonly campaignPage = signal(0);
  protected readonly pagedCampaignResults = computed(() =>
    this.campaignResults().slice(this.campaignPage() * RESULTS_PAGE_SIZE, this.campaignPage() * RESULTS_PAGE_SIZE + RESULTS_PAGE_SIZE)
  );
  protected readonly campaignTotalPages = computed(() => Math.max(1, Math.ceil(this.campaignResults().length / RESULTS_PAGE_SIZE)));
  protected readonly campaignPageNumbers = computed(() => Array.from({ length: this.campaignTotalPages() }, (_, i) => i));
  protected readonly campaignRangeLabel = computed(() => this.rangeLabel(this.campaignResults().length, this.campaignPage()));

  private rangeLabel(total: number, page: number): string {
    const start = total === 0 ? 0 : page * RESULTS_PAGE_SIZE + 1;
    const end = Math.min(total, (page + 1) * RESULTS_PAGE_SIZE);
    return `${start}-${end} of ${total}`;
  }

  protected readonly toastMessage = signal<string | null>(null);
  protected readonly toastType = signal<'success' | 'error'>('success');
  private toastTimeoutId?: ReturnType<typeof setTimeout>;

  protected readonly basketQuantity = computed(() => this.formState.basket().length);
  protected readonly basketTotal = computed(() =>
    this.formState.basket().reduce((sum, line) => sum + line.price, 0)
  );
  protected readonly autoAddedLines = computed(() => this.formState.basket().filter(line => line.isAutoAdded));

  constructor() {
    this.catalogForm.valueChanges.subscribe(() => this.catalogFormValue.set(this.catalogForm.getRawValue()));
    this.campaignForm.valueChanges.subscribe(() => this.campaignFormValue.set(this.campaignForm.getRawValue()));

    // Katalog/kampanya adlari backend-driven ve dile gore cevrilir (bkz. Accept-Language
    // interceptor) - SPA'da sayfa yenilenmedigi icin dil degistiginde katalog verisi yeniden
    // cekilir. untracked() ile sarilir ki loadCatalogData() ici (searchCatalog/searchCampaigns'in
    // okudugu form sinyalleri) bu effect'i fazladan tetiklemesin - SADECE i18n.lang() degisince calisir.
    effect(() => {
      this.i18n.lang();
      untracked(() => this.loadCatalogData());
    });

    this.orderService.getActiveOffers(this.formState.custAcctId()).subscribe(activeOffers => {
      this.activeOfferingIds.set(new Set(activeOffers.map(offer => offer.prodOfrId)));
    });
  }

  private loadCatalogData(): void {
    this.isLoadingCatalogData.set(true);
    forkJoin({
      catalogs: this.productService.getCatalogs(),
      campaigns: this.productService.getCampaigns(),
      offerings: this.productService.getOfferings(),
      catalogOfferings: this.productService.getCatalogOfferings(),
      campaignOfferings: this.productService.getCampaignOfferings(),
      relations: this.productService.getOfferingRelations()
    }).subscribe({
      next: result => {
        this.catalogs.set(result.catalogs);
        this.campaigns.set(result.campaigns);
        this.offerings.set(result.offerings);
        this.catalogOfferings.set(result.catalogOfferings);
        this.campaignOfferings.set(result.campaignOfferings);
        this.formState.requiredOfferingsMap.set(this.buildRequiredOfferingsMap(result.relations, result.offerings));
        this.excludedOfferingIdsMap.set(this.buildExcludedOfferingsMap(result.relations));
        this.isLoadingCatalogData.set(false);
        // Dil degisikligiyle yeniden yuklendiyse, ekranda zaten gosterilen arama sonuclarini da
        // (eski dildeki isim/aciklamalarla donmus olabilir) tazelenmis veriyle yeniden hesapla.
        if (this.hasSearchedCatalog()) {
          this.searchCatalog();
        }
        if (this.hasSearchedCampaign()) {
          this.searchCampaigns();
        }
      },
      error: () => {
        this.isLoadingCatalogData.set(false);
        this.loadError.set(true);
      }
    });
  }

  // Zorunlu (mandatory=true, active) iliskileri, hedef offering'in ad/fiyatiyla zenginlestirip
  // formState.addToBasket'in kullanacagi prodOfrId -> zorunlu offering listesine cevirir.
  private buildRequiredOfferingsMap(
    relations: ProductOfferingRelation[],
    offerings: ProductOffering[]
  ): Record<number, { productOfferingId: number; productOfferingNo: string; name: string; price: number }[]> {
    const map: Record<number, { productOfferingId: number; productOfferingNo: string; name: string; price: number }[]> = {};
    for (const relation of relations) {
      if (!relation.mandatory || !relation.active) {
        continue;
      }
      const target = offerings.find(o => o.productOfferingId === relation.productOfferingId2);
      if (!target) {
        continue;
      }
      (map[relation.productOfferingId1] ??= []).push({
        productOfferingId: target.productOfferingId,
        productOfferingNo: target.productOfferingNo,
        name: target.name,
        price: target.totalPrice
      });
    }
    return map;
  }

  // Cakisan (exclusive=true, active) iliskileri iki yonlu bir id -> id set haritasina cevirir.
  private buildExcludedOfferingsMap(relations: ProductOfferingRelation[]): Record<number, ReadonlySet<number>> {
    const map: Record<number, Set<number>> = {};
    for (const relation of relations) {
      if (!relation.exclusive || !relation.active) {
        continue;
      }
      (map[relation.productOfferingId1] ??= new Set()).add(relation.productOfferingId2);
      (map[relation.productOfferingId2] ??= new Set()).add(relation.productOfferingId1);
    }
    return map;
  }

  // Sepette, verilen offering ile cakisan (EXCL) bir satir var mi doner.
  private conflictingBasketLine(productOfferingId: number): BasketLine | undefined {
    const excludedIds = this.excludedOfferingIdsMap()[productOfferingId];
    if (!excludedIds) {
      return undefined;
    }
    return this.formState.basket().find(line => excludedIds.has(line.prodOfrId));
  }

  protected selectTab(tab: OfferTab): void {
    this.activeTab.set(tab);
  }

  protected get catalogOptions(): ProductCatalog[] {
    return this.catalogs();
  }

  // Bir katalog secildiginde, o kataloga ait teklifler otomatik olarak listelenir.
  protected onCatalogSelected(): void {
    if (this.catalogForm.controls.catalogId.value) {
      this.searchCatalog();
    }
  }

  protected onCampaignCategorySelected(): void {
    if (this.campaignForm.controls.catalogId.value) {
      this.searchCampaigns();
    }
  }

  protected sanitizeOfferId(event: Event): void {
    const input = event.target as HTMLInputElement;
    const digitsOnly = input.value.replace(/\D/g, '').slice(0, ID_FIELD_MAX_LENGTH);
    this.setCatalogFieldError('offerId', input.value !== digitsOnly);
    this.catalogForm.controls.offerId.setValue(digitsOnly);
  }

  protected sanitizeOfferName(event: Event): void {
    const input = event.target as HTMLInputElement;
    const truncated = input.value.slice(0, NAME_FIELD_MAX_LENGTH);
    this.setCatalogFieldError('offerName', input.value !== truncated);
    this.catalogForm.controls.offerName.setValue(truncated);
  }

  protected sanitizeCampaignRef(event: Event): void {
    const input = event.target as HTMLInputElement;
    const digitsOnly = input.value.replace(/\D/g, '').slice(0, ID_FIELD_MAX_LENGTH);
    this.setCampaignFieldError('campaignRef', input.value !== digitsOnly);
    this.campaignForm.controls.campaignRef.setValue(digitsOnly);
  }

  protected sanitizeCampaignName(event: Event): void {
    const input = event.target as HTMLInputElement;
    const truncated = input.value.slice(0, NAME_FIELD_MAX_LENGTH);
    this.setCampaignFieldError('campaignName', input.value !== truncated);
    this.campaignForm.controls.campaignName.setValue(truncated);
  }

  private setCatalogFieldError(field: 'offerId' | 'offerName', hasError: boolean): void {
    this.catalogFieldErrors.update(errors => ({ ...errors, [field]: hasError }));
  }

  private setCampaignFieldError(field: 'campaignRef' | 'campaignName', hasError: boolean): void {
    this.campaignFieldErrors.update(errors => ({ ...errors, [field]: hasError }));
  }

  protected goToCatalogPage(page: number): void {
    if (page < 0 || page >= this.catalogTotalPages()) {
      return;
    }
    this.catalogPage.set(page);
  }

  protected goToCampaignPage(page: number): void {
    if (page < 0 || page >= this.campaignTotalPages()) {
      return;
    }
    this.campaignPage.set(page);
  }

  protected searchCatalog(): void {
    const { catalogId, offerId, offerName } = this.catalogForm.getRawValue();
    this.hasSearchedCatalog.set(true);
    this.catalogPage.set(0);

    const offeringIdsInCatalog = catalogId
      ? new Set(
          this.catalogOfferings()
            .filter(co => co.productCatalogId === Number(catalogId))
            .map(co => co.productOfferingId)
        )
      : null;

    const results = this.offerings()
      .filter(offer => !offeringIdsInCatalog || offeringIdsInCatalog.has(offer.productOfferingId))
      .filter(offer => !offerId || String(offer.productOfferingId).includes(offerId.trim()))
      .filter(offer => !offerName || offer.name.toLowerCase().includes(offerName.trim().toLowerCase()))
      .map(offer => ({
        productOfferingId: offer.productOfferingId,
        productOfferingNo: offer.productOfferingNo,
        name: offer.name,
        descr: offer.descr,
        price: offer.totalPrice
      }));

    this.catalogResults.set(results);
  }

  protected searchCampaigns(): void {
    const { catalogId, campaignRef, campaignName } = this.campaignForm.getRawValue();
    this.hasSearchedCampaign.set(true);
    this.campaignPage.set(0);

    const offeringPriceById = new Map(this.offerings().map(o => [o.productOfferingId, o.totalPrice]));
    const offeringNoById = new Map(this.offerings().map(o => [o.productOfferingId, o.productOfferingNo]));

    // Kategori (Internet/Mobile/TV) secildiyse, o kataloga ait teklif id'lerinden EN AZ birini
    // iceren kampanyalar eslesir - "bu tur teklifi iceren kampanyalar" arama mantigi.
    const offeringIdsInCatalog = catalogId
      ? new Set(
          this.catalogOfferings()
            .filter(co => co.productCatalogId === Number(catalogId))
            .map(co => co.productOfferingId)
        )
      : null;

    const filteredCampaigns = this.campaigns()
      .filter(
        campaign =>
          !offeringIdsInCatalog ||
          this.campaignOfferings().some(
            co => co.campaignId === campaign.campaignId && offeringIdsInCatalog.has(co.productOfferingId)
          )
      )
      .filter(campaign => !campaignRef || String(campaign.campaignId).includes(campaignRef.trim()))
      .filter(campaign => !campaignName || campaign.name.toLowerCase().includes(campaignName.trim().toLowerCase()));

    const results: CampaignResultRow[] = filteredCampaigns.map(campaign => {
      const offerings: CampaignOfferingRow[] = this.campaignOfferings()
        .filter(co => co.campaignId === campaign.campaignId)
        .map(co => ({
          productOfferingId: co.productOfferingId,
          productOfferingNo: offeringNoById.get(co.productOfferingId) ?? '',
          name: co.productOfferingName,
          originalPrice: offeringPriceById.get(co.productOfferingId) ?? 0,
          price: co.discountedPrice,
          discountPct: co.discountPct
        }));

      return {
        campaignId: campaign.campaignId,
        campaignNo: campaign.campaignNo,
        campaignCode: campaign.campaignCode,
        name: campaign.name,
        offerings,
        totalOriginalPrice: offerings.reduce((sum, o) => sum + o.originalPrice, 0),
        totalPrice: offerings.reduce((sum, o) => sum + o.price, 0)
      };
    });

    this.campaignResults.set(results);
  }

  protected isInBasket(productOfferingId: number): boolean {
    return this.formState.basket().some(line => line.prodOfrId === productOfferingId);
  }

  protected isCampaignInBasket(campaign: CampaignResultRow): boolean {
    return campaign.offerings.every(offering => this.isInBasket(offering.productOfferingId));
  }

  // Aktif tekliflerin ait oldugu katalog kategorileri (Internet/Mobile/TV) - musteride o
  // kategoriden zaten aktif bir urun varsa, farkli bir teklif olsa bile eklenememeli.
  private readonly activeCatalogNames = computed(() => {
    const names = new Set<string>();
    for (const prodOfrId of this.activeOfferingIds()) {
      const catalogName = this.catalogNameForOffering(prodOfrId);
      if (catalogName) {
        names.add(catalogName);
      }
    }
    return names;
  });

  protected isOfferingAlreadyActive(productOfferingId: number): boolean {
    if (this.activeOfferingIds().has(productOfferingId)) {
      return true;
    }
    const catalogName = this.catalogNameForOffering(productOfferingId);
    return catalogName !== null && this.activeCatalogNames().has(catalogName);
  }

  protected isCampaignAlreadyActive(campaign: CampaignResultRow): boolean {
    return campaign.offerings.some(offering => this.isOfferingAlreadyActive(offering.productOfferingId));
  }

  protected toggleCampaignExpanded(campaignId: number): void {
    this.expandedCampaignId.update(current => (current === campaignId ? null : campaignId));
  }

  // Basket/Configuration/Review'da katalog ikonu gostermek icin (bkz. BasketLine.catalogName) -
  // cakisma kontrolu artik conflictingBasketLine/excludedOfferingIdsMap ile (EXCL iliskisi) yapiliyor.
  private catalogNameForOffering(productOfferingId: number): string | null {
    const catalogOffering = this.catalogOfferings().find(co => co.productOfferingId === productOfferingId);
    if (!catalogOffering) {
      return null;
    }
    return this.catalogs().find(c => c.productCatalogId === catalogOffering.productCatalogId)?.name ?? null;
  }

  protected addOfferToBasket(offer: CatalogResultRow): void {
    if (this.conflictingBasketLine(offer.productOfferingId)) {
      this.showToast(this.i18n.t('newSale.categoryConflictError'), 'error');
      return;
    }

    const catalogName = this.catalogNameForOffering(offer.productOfferingId);
    const line: BasketLine = {
      prodOfrId: offer.productOfferingId,
      prodOfrNo: offer.productOfferingNo,
      offerName: offer.name,
      price: offer.price,
      originalPrice: null,
      cmpgId: null,
      cmpgName: null,
      catalogName,
      isAutoAdded: false,
      triggeredBy: null
    };
    const addedRequiredNames = this.formState.addToBasket(line);
    this.showToast(this.toastMessageFor(offer.name, addedRequiredNames.length));
  }

  protected addCampaignToBasket(campaign: CampaignResultRow): void {
    const hasConflict = campaign.offerings.some(offering => this.conflictingBasketLine(offering.productOfferingId));
    if (hasConflict) {
      this.showToast(this.i18n.t('newSale.categoryConflictError'), 'error');
      return;
    }

    let addedRequiredCount = 0;
    for (const offering of campaign.offerings) {
      const line: BasketLine = {
        prodOfrId: offering.productOfferingId,
        prodOfrNo: offering.productOfferingNo,
        offerName: offering.name,
        price: offering.price,
        originalPrice: offering.originalPrice,
        cmpgId: campaign.campaignId,
        cmpgName: campaign.name,
        catalogName: this.catalogNameForOffering(offering.productOfferingId),
        isAutoAdded: false,
        triggeredBy: null
      };
      addedRequiredCount += this.formState.addToBasket(line).length;
    }
    this.showToast(this.toastMessageFor(campaign.name, addedRequiredCount));
  }

  private toastMessageFor(name: string, requiredCount: number): string {
    if (requiredCount === 0) {
      return this.i18n.t('newSale.addedToBasket').replace('{name}', name);
    }
    return this.i18n
      .t('newSale.addedToBasketWithRequired')
      .replace('{name}', name)
      .replace('{count}', String(requiredCount));
  }

  protected removeFromBasket(prodOfrId: number): void {
    this.formState.removeFromBasket(prodOfrId);
  }

  protected removeCampaignFromBasket(cmpgId: number): void {
    this.formState.removeCampaignFromBasket(cmpgId);
  }

  protected clearBasket(): void {
    this.formState.clearBasket();
  }

  private showToast(message: string, type: 'success' | 'error' = 'success'): void {
    clearTimeout(this.toastTimeoutId);
    this.toastType.set(type);
    this.toastMessage.set(message);
    this.toastTimeoutId = setTimeout(() => this.toastMessage.set(null), 3000);
  }

  protected dismissToast(): void {
    clearTimeout(this.toastTimeoutId);
    this.toastMessage.set(null);
  }
}
