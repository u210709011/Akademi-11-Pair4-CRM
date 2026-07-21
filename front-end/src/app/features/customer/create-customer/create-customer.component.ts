import { NgComponentOutlet } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, Injectable, Type, computed, inject, signal, ChangeDetectionStrategy } from '@angular/core';
import { Router } from '@angular/router';
import { AddressInfo, ContactInfo, CustomerService, IndividualInfo } from '../../../core/customer';
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

// tab component'leri NgComponentOutlet ile dinamik olustugu icin, sekmeler arasi gidip gelince eski
// component instance yok edilip yenisi yaratiliyor - bu yuzden asil form verisi (draft'lar degil,
// gonderilecek olan model) component'in kendi icinde degil, bu servis icinde tutulur ki kaybolmasin.
export interface DemographicFormModel {
  firstName: string;
  middleName: string;
  lastName: string;
  birthDate: Date | null;
  gender: string;
  fatherName: string;
  motherName: string;
  nationalId: string;
}

export interface AddressFormModel {
  city: string;
  street: string;
  houseNumber: string;
  description: string;
}

export interface ContactFormModel {
  email: string;
  homePhone: string;
  mobilePhone: string;
  fax: string;
}

const EMPTY_DEMOGRAPHIC: DemographicFormModel = {
  firstName: '',
  middleName: '',
  lastName: '',
  birthDate: null,
  gender: '',
  fatherName: '',
  motherName: '',
  nationalId: ''
};

const EMPTY_CONTACT: ContactFormModel = { email: '', homePhone: '', mobilePhone: '', fax: '' };

@Injectable()
export class CreateCustomerFormStateService {
  readonly demographicModel = signal<DemographicFormModel>({ ...EMPTY_DEMOGRAPHIC });
  readonly demographicValid = signal(false);
  readonly demographicValue = signal<IndividualInfo | null>(null);
  readonly isVerifyingIdentity = signal(false);
  readonly identityVerificationError = signal<string | null>(null);

  readonly addresses = signal<AddressFormModel[]>([]);
  readonly addressesValid = signal(false);
  readonly addressesValue = signal<AddressInfo[]>([]);

  readonly contactModel = signal<ContactFormModel>({ ...EMPTY_CONTACT });
  readonly contactValid = signal(false);
  readonly contactValue = signal<ContactInfo | null>(null);

  readonly isSubmitting = signal(false);
  readonly submitError = signal<string | null>(null);
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
    if (this.formState.isVerifyingIdentity() || this.formState.isSubmitting()) {
      return true;
    }
    switch (this.activeTab()) {
      case 'demographic':
        return !this.formState.demographicValid();
      case 'address':
        return !this.formState.addressesValid();
      case 'contact':
        return !this.formState.contactValid();
    }
  });

  protected readonly nextButtonLabel = computed(() =>
    this.activeTab() === 'contact' ? this.i18n.t('create.createBtn') : this.i18n.t('create.nextBtn')
  );

  protected selectTab(tab: CreateCustomerTab): void {
    this.activeTab.set(tab);
  }

  protected nextTab(): void {
    if (this.activeTab() === 'demographic') {
      this.verifyIdentityThenAdvance();
      return;
    }
    if (this.activeTab() === 'contact') {
      this.submitOnboarding();
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

  // ACC-023: Contact tab'da Next/Create tiklandiginda tum sihirbaz verisini tek istekte backend'e gonderir.
  private submitOnboarding(): void {
    const individual = this.formState.demographicValue();
    const addresses = this.formState.addressesValue();
    const contact = this.formState.contactValue();

    if (!individual || !contact || addresses.length === 0) {
      return;
    }

    this.formState.isSubmitting.set(true);
    this.formState.submitError.set(null);

    this.customerService.onboard({ individual, addresses, contact }).subscribe({
      next: response => {
        this.formState.isSubmitting.set(false);
        this.router.navigate(['/detail-customer', response.custId]);
      },
      error: (httpError: HttpErrorResponse) => {
        this.formState.isSubmitting.set(false);
        this.formState.submitError.set(
          httpError.status === 409
            ? this.i18n.t('create.identityDuplicate')
            : this.i18n.t('create.submitError')
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
