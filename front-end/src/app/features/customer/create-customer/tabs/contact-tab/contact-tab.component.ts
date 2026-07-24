import { ChangeDetectionStrategy, Component, effect, inject } from '@angular/core';
import { email, form, FormField, maxLength, required } from '@angular/forms/signals';
import { ContactInfo } from '../../../../../core/customer';
import { I18nService } from '../../../../../core/i18n';
import { CreateCustomerFormStateService } from '../../create-customer.component';

type PhoneFieldName = 'homePhone' | 'mobilePhone' | 'fax';

const PHONE_FIELDS: PhoneFieldName[] = ['homePhone', 'mobilePhone', 'fax'];

@Component({
  selector: 'app-contact-tab',
  imports: [FormField],
  templateUrl: './contact-tab.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './contact-tab.component.scss'
})
export class ContactTabComponent {
  protected readonly i18n = inject(I18nService);
  private readonly formState = inject(CreateCustomerFormStateService);

  // model, sekmeler arasi gecince kaybolmamasi icin CreateCustomerFormStateService'te tutulur
  protected readonly contactModel = this.formState.contactModel;

  protected readonly contactForm = form(this.contactModel, path => {
    required(path.email);
    email(path.email);
    required(path.mobilePhone);
    maxLength(path.homePhone, 10);
    maxLength(path.mobilePhone, 10);
    maxLength(path.fax, 10);
  });

  constructor() {
    // her telefon alani icin: rakam disi karakterleri temizle; mobilePhone ayrica hep 5 ile baslamali
    for (const field of PHONE_FIELDS) {
      effect(() => this.sanitizePhoneField(field));
    }

    // form gecerliligi/degeri degistikce sihirbazin ortak state'ine (CreateCustomerFormStateService) yansitilir
    effect(() => {
      const valid = this.contactForm().valid();
      this.formState.contactValid.set(valid);
      this.formState.contactValue.set(valid ? this.toContactInfo() : null);
    });
  }

  private toContactInfo(): ContactInfo {
    const value = this.contactModel();
    return {
      email: value.email,
      mobilePhone: value.mobilePhone,
      homePhone: value.homePhone || null,
      fax: value.fax || null
    };
  }

  private sanitizePhoneField(field: PhoneFieldName): void {
    const raw = this.contactForm[field]().value();
    let digitsOnly = raw.replace(/\D/g, '').slice(0, 10);

    if (field === 'mobilePhone') {
      while (digitsOnly.length > 0 && digitsOnly[0] !== '5') {
        digitsOnly = digitsOnly.slice(1);
      }
    }

    if (digitsOnly !== raw) {
      this.contactForm[field]().value.set(digitsOnly);
    }
  }
}
