import { NgComponentOutlet } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, Injectable, Type, computed, inject, signal, ChangeDetectionStrategy } from '@angular/core';
import { Router } from '@angular/router';
import { CustomerService, IndividualInfo } from '../../../core/customer';
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

  readonly demographicValue = signal<IndividualInfo | null>(null);
  readonly isVerifyingIdentity = signal(false);
  readonly identityVerificationError = signal<string | null>(null);
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
  private readonly customerService = inject(CustomerService);
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
    if (this.formState.isVerifyingIdentity()) {
      return true;
    }
    if (this.activeTab() === 'demographic') {
      return !this.formState.demographicValid();
    }
    return false;
  });

  protected selectTab(tab: CreateCustomerTab): void {
    this.activeTab.set(tab);
  }

  protected nextTab(): void {
    if (this.activeTab() === 'demographic') {
      this.verifyIdentityThenAdvance();
      return;
    }
    this.advanceTab();
  }

  private verifyIdentityThenAdvance(): void {
    const individual = this.formState.demographicValue();
    if (!individual) {
      return;
    }

    this.formState.isVerifyingIdentity.set(true);
    this.formState.identityVerificationError.set(null);

    this.customerService.verifyIdentity(individual).subscribe({
      next: response => {
        this.formState.isVerifyingIdentity.set(false);
        if (response.verified) {
          this.advanceTab();
        } else {
          this.formState.identityVerificationError.set(response.message);
        }
      },
      error: (httpError: HttpErrorResponse) => {
        this.formState.isVerifyingIdentity.set(false);
        this.formState.identityVerificationError.set(
          httpError.status === 409
            ? this.i18n.t('create.identityDuplicate')
            : this.i18n.t('create.identityError')
        );
      }
    });
  }

  private advanceTab(): void {
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
