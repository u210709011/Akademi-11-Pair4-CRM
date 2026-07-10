import { NgComponentOutlet } from '@angular/common';
import { Component, Type, computed, inject, signal } from '@angular/core';
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

@Component({
  selector: 'app-create-customer',
  imports: [NgComponentOutlet],
  templateUrl: './create-customer.component.html',
  styleUrl: './create-customer.component.scss'
})
export class CreateCustomerComponent {
  protected readonly i18n = inject(I18nService);

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

  protected selectTab(tab: CreateCustomerTab): void {
    this.activeTab.set(tab);
  }
}
