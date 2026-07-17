import { NgComponentOutlet } from '@angular/common';
import { Component, Injectable, Type, computed, inject, signal, ChangeDetectionStrategy } from '@angular/core';
import { Router } from '@angular/router';
import { I18nService } from '../../../core/i18n';
import { AddressTabComponent } from './tabs/address-tab/address-tab.component';
import { ContactTabComponent } from './tabs/contact-tab/contact-tab.component';
import { DemographicTabComponent } from './tabs/demographic-tab/demographic-tab.component';

type CreateCustomerTab = 'demographic' | 'address' | 'contact';

interface TabDefinition {
  key: CreateCustomerTab;
  labelKey: string;
  component: Type<unknown>;
}

@Injectable()
export class CreateCustomerFormStateService {
  readonly demographicValid = signal(false);
}

@Component({
  selector: 'app-create-customer',
  imports: [NgComponentOutlet],
  templateUrl: './create-customer.component.html',
  styleUrl: './create-customer.component.scss',
  changeDetection: ChangeDetectionStrategy.Eager,
  providers: [CreateCustomerFormStateService]
})
export class CreateCustomerComponent {
  protected readonly i18n = inject(I18nService);
  private readonly router = inject(Router);
  protected readonly formState = inject(CreateCustomerFormStateService);

  protected readonly tabs: TabDefinition[] = [
    { key: 'demographic', labelKey: 'create.tabDemographic', component: DemographicTabComponent },
    { key: 'address', labelKey: 'create.tabAddress', component: AddressTabComponent },
    { key: 'contact', labelKey: 'create.tabContact', component: ContactTabComponent }
  ];

  protected readonly activeTab = signal<CreateCustomerTab>('demographic');

  protected readonly activeTabLabel = computed(() => {
    const labelKey = this.tabs.find(tab => tab.key === this.activeTab())?.labelKey;
    return labelKey ? this.i18n.t(labelKey) : '';
  });

  protected readonly activeTabComponent = computed(
    () => this.tabs.find(tab => tab.key === this.activeTab())?.component ?? null
  );

  protected readonly isNextDisabled = computed(() => {
    if (this.activeTab() === 'demographic') {
      return !this.formState.demographicValid();
    }
    return false;
  });

  protected selectTab(tab: CreateCustomerTab): void {
    this.activeTab.set(tab);
  }

  protected nextTab(): void {
    const currentIndex = this.tabs.findIndex(tab => tab.key === this.activeTab());
    const nextTab = this.tabs[currentIndex + 1];
    if (nextTab) {
      this.activeTab.set(nextTab.key);
    }
  }

  protected cancel(): void {
    this.router.navigateByUrl('/search-customer');
  }
}
