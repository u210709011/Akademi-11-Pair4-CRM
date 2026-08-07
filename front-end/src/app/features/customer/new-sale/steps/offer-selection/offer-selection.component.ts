import { DecimalPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { I18nService } from '../../../../../core/i18n';
import { Campaign, CampaignOffering, ProductCatalog, ProductCatalogOffering, ProductOffering, ProductService } from '../../../../../core/product';
import { BasketLine, NewSaleFormStateService } from '../../new-sale.component';
import { NEW_SALE_MOCK_MODE } from '../../mock/new-sale-mock.config';
import {
  MOCK_CAMPAIGN_OFFERINGS,
  MOCK_CAMPAIGNS,
  MOCK_CATALOG_OFFERINGS,
  MOCK_CATALOGS,
  MOCK_OFFERINGS
} from '../../mock/new-sale-mock.data';

type OfferTab = 'catalog' | 'campaigns';

interface CatalogResultRow {
  productOfferingId: number;
  name: string;
  descr: string;
  price: number;
}

interface CampaignResultRow {
  campaignId: number;
  campaignCode: string;
  name: string;
  offerings: { productOfferingId: number; name: string; price: number }[];
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
  protected readonly formState = inject(NewSaleFormStateService);

  protected readonly activeTab = signal<OfferTab>('catalog');

  private readonly catalogs = signal<ProductCatalog[]>([]);
  private readonly campaigns = signal<Campaign[]>([]);
  private readonly offerings = signal<ProductOffering[]>([]);
  private readonly catalogOfferings = signal<ProductCatalogOffering[]>([]);
  private readonly campaignOfferings = signal<CampaignOffering[]>([]);

  protected readonly isLoadingCatalogData = signal(true);
  protected readonly loadError = signal(false);

  protected readonly catalogForm = this.formBuilder.nonNullable.group({
    catalogId: '',
    offerId: '',
    offerName: ''
  });

  protected readonly campaignForm = this.formBuilder.nonNullable.group({
    campaignId: '',
    campaignRef: '',
    campaignName: ''
  });

  protected readonly hasSearchedCatalog = signal(false);
  protected readonly hasSearchedCampaign = signal(false);
  protected readonly catalogResults = signal<CatalogResultRow[]>([]);
  protected readonly campaignResults = signal<CampaignResultRow[]>([]);
  protected readonly expandedCampaignId = signal<number | null>(null);

  protected readonly toastMessage = signal<string | null>(null);
  protected readonly toastType = signal<'success' | 'error'>('success');
  private toastTimeoutId?: ReturnType<typeof setTimeout>;

  protected readonly basketQuantity = computed(() => this.formState.basket().length);
  protected readonly basketTotal = computed(() =>
    this.formState.basket().reduce((sum, line) => sum + line.price, 0)
  );
  protected readonly selectedLines = computed(() => this.formState.basket().filter(line => !line.isAutoAdded));
  protected readonly autoAddedLines = computed(() => this.formState.basket().filter(line => line.isAutoAdded));

  constructor() {
    // GECICI MOCK MODU - backend product-service (gateway route + seed data) hazir olana kadar.
    // Kaldirmak icin: bu if bloguyla mock/ klasorunu silin, alttaki forkJoin cagrisi tek basina kalsin.
    if (NEW_SALE_MOCK_MODE) {
      this.catalogs.set(MOCK_CATALOGS);
      this.campaigns.set(MOCK_CAMPAIGNS);
      this.offerings.set(MOCK_OFFERINGS);
      this.catalogOfferings.set(MOCK_CATALOG_OFFERINGS);
      this.campaignOfferings.set(MOCK_CAMPAIGN_OFFERINGS);
      this.isLoadingCatalogData.set(false);
      return;
    }

    forkJoin({
      catalogs: this.productService.getCatalogs(),
      campaigns: this.productService.getCampaigns(),
      offerings: this.productService.getOfferings(),
      catalogOfferings: this.productService.getCatalogOfferings(),
      campaignOfferings: this.productService.getCampaignOfferings()
    }).subscribe({
      next: result => {
        this.catalogs.set(result.catalogs);
        this.campaigns.set(result.campaigns);
        this.offerings.set(result.offerings);
        this.catalogOfferings.set(result.catalogOfferings);
        this.campaignOfferings.set(result.campaignOfferings);
        this.isLoadingCatalogData.set(false);
      },
      error: () => {
        this.isLoadingCatalogData.set(false);
        this.loadError.set(true);
      }
    });
  }

  protected selectTab(tab: OfferTab): void {
    this.activeTab.set(tab);
  }

  protected get catalogOptions(): ProductCatalog[] {
    return this.catalogs();
  }

  protected get campaignOptions(): Campaign[] {
    return this.campaigns();
  }

  // Bir katalog secildiginde, o kataloga ait teklifler otomatik olarak listelenir.
  protected onCatalogSelected(): void {
    if (this.catalogForm.controls.catalogId.value) {
      this.searchCatalog();
    }
  }

  protected onCampaignCategorySelected(): void {
    if (this.campaignForm.controls.campaignId.value) {
      this.searchCampaigns();
    }
  }

  protected searchCatalog(): void {
    const { catalogId, offerId, offerName } = this.catalogForm.getRawValue();
    this.hasSearchedCatalog.set(true);

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
        name: offer.name,
        descr: offer.descr,
        price: offer.totalPrice
      }));

    this.catalogResults.set(results);
  }

  protected searchCampaigns(): void {
    const { campaignId, campaignRef, campaignName } = this.campaignForm.getRawValue();
    this.hasSearchedCampaign.set(true);

    const offeringPriceById = new Map(this.offerings().map(o => [o.productOfferingId, o.totalPrice]));

    const filteredCampaigns = this.campaigns()
      .filter(campaign => !campaignId || campaign.campaignId === Number(campaignId))
      .filter(campaign => !campaignRef || String(campaign.campaignId).includes(campaignRef.trim()))
      .filter(campaign => !campaignName || campaign.name.toLowerCase().includes(campaignName.trim().toLowerCase()));

    const results: CampaignResultRow[] = filteredCampaigns.map(campaign => {
      const offerings = this.campaignOfferings()
        .filter(co => co.campaignId === campaign.campaignId)
        .map(co => ({
          productOfferingId: co.productOfferingId,
          name: co.productOfferingName,
          price: offeringPriceById.get(co.productOfferingId) ?? 0
        }));

      return {
        campaignId: campaign.campaignId,
        campaignCode: campaign.campaignCode,
        name: campaign.name,
        offerings,
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

  protected toggleCampaignExpanded(campaignId: number): void {
    this.expandedCampaignId.update(current => (current === campaignId ? null : campaignId));
  }

  // Bir katalogdan (Mobile/Internet/TV) ayni anda sadece bir urun sepette olabilir.
  private catalogNameForOffering(productOfferingId: number): string | null {
    const catalogOffering = this.catalogOfferings().find(co => co.productOfferingId === productOfferingId);
    if (!catalogOffering) {
      return null;
    }
    return this.catalogs().find(c => c.productCatalogId === catalogOffering.productCatalogId)?.name ?? null;
  }

  protected addOfferToBasket(offer: CatalogResultRow): void {
    const catalogName = this.catalogNameForOffering(offer.productOfferingId);

    const conflictingLine = catalogName
      ? this.selectedLines().find(
          line => line.catalogName === catalogName && line.prodOfrId !== offer.productOfferingId
        )
      : undefined;

    if (conflictingLine) {
      this.showToast(this.i18n.t('newSale.categoryConflictError'), 'error');
      return;
    }

    const line: BasketLine = {
      prodOfrId: offer.productOfferingId,
      offerName: offer.name,
      price: offer.price,
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
    let addedRequiredCount = 0;
    for (const offering of campaign.offerings) {
      const line: BasketLine = {
        prodOfrId: offering.productOfferingId,
        offerName: offering.name,
        price: offering.price,
        cmpgId: campaign.campaignId,
        cmpgName: campaign.name,
        catalogName: null,
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
