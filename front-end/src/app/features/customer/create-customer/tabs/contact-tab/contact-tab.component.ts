import { ChangeDetectionStrategy, Component, effect, inject, signal } from '@angular/core';
import { email, form, FormField, maxLength, required } from '@angular/forms/signals';
import { I18nService } from '../../../../../core/i18n';

type PhoneFieldName = 'homePhone' | 'mobilePhone' | 'fax';

const PHONE_FIELDS: PhoneFieldName[] = ['homePhone', 'mobilePhone', 'fax'];

interface ContactFormModel {
  email: string;
  homePhone: string;
  mobilePhone: string;
  fax: string;
}

@Component({
  selector: 'app-contact-tab',
  imports: [FormField],
  templateUrl: './contact-tab.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './contact-tab.component.scss'
})
export class ContactTabComponent {
  protected readonly i18n = inject(I18nService);

  protected readonly contactModel = signal<ContactFormModel>({
    email: '',
    homePhone: '',
    mobilePhone: '',
    fax: ''
  });

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
