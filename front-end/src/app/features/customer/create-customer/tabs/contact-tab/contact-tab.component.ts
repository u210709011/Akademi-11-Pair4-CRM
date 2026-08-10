import { ChangeDetectionStrategy, Component, effect, inject, signal } from '@angular/core';
import { email, form, FormField, maxLength, pattern, required } from '@angular/forms/signals';
import { ContactInfo } from '../../../../../core/customer';
import { I18nService } from '../../../../../core/i18n';
import { CreateCustomerFormStateService } from '../../create-customer.component';

type PhoneFieldName = 'homePhone' | 'mobilePhone' | 'fax';

const PHONE_FIELDS: PhoneFieldName[] = ['homePhone', 'mobilePhone', 'fax'];
const PHONE_MAX_DIGITS: Record<PhoneFieldName, number> = { homePhone: 10, mobilePhone: 10, fax: 11 };
const HOME_PHONE_PATTERN = /^2\d{9}$/;
const MOBILE_PHONE_PATTERN = /^5\d{9}$/;
// UC-EACRML-003 validasyon tablosu ornegi: 02121234567 (basinda 0, toplam 11 hane).
const FAX_PATTERN = /^0\d{10}$/;
const DIGITS_ONLY_ERROR_TIMEOUT_MS = 2000;

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

  // harf/gecersiz karakter yazilmaya calisildiginda ilgili alanin altinda gecici uyari gostermek icin
  protected readonly digitsOnlyErrorField = signal<PhoneFieldName | null>(null);
  private digitsOnlyErrorTimeoutId?: ReturnType<typeof setTimeout>;

  // model, sekmeler arasi gecince kaybolmamasi icin CreateCustomerFormStateService'te tutulur
  protected readonly contactModel = this.formState.contactModel;

  protected readonly contactForm = form(this.contactModel, path => {
    required(path.email);
    email(path.email);
    required(path.mobilePhone);
    // Backend (ContactInfo.homePhone/fax) 10-11 haneyi de kabul eder (^[0-9]{10,11}$),
    // mobilePhone ise her zaman tam 10 hane olmali (5 ile baslar).
    maxLength(path.homePhone, 10);
    maxLength(path.mobilePhone, 10);
    maxLength(path.fax, 11);
    pattern(path.homePhone, HOME_PHONE_PATTERN, { when: ({ value }) => value() !== '' });
    pattern(path.mobilePhone, MOBILE_PHONE_PATTERN);
    pattern(path.fax, FAX_PATTERN, { when: ({ value }) => value() !== '' });
  });

  constructor() {
    // her telefon alani icin rakam disi karakterleri temizle (baslangic hanesi kontrolu pattern validator'da)
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
    const digitsOnly = raw.replace(/\D/g, '').slice(0, PHONE_MAX_DIGITS[field]);

    if (digitsOnly !== raw) {
      this.contactForm[field]().value.set(digitsOnly);
      // beforeinput engellemeden kacan durumlar icin (yapistirma, otomatik doldurma vb.) yedek uyari
      if (/\D/.test(raw)) {
        this.showDigitsOnlyError(field);
      }
    }
  }

  // rakam disindaki karakterlerin ekrana hic yazilmamasi icin (yapistirma dahil) tus/insert seviyesinde engelle
  protected blockNonDigitInput(event: InputEvent, field: PhoneFieldName): void {
    if (event.data != null && /\D/.test(event.data)) {
      event.preventDefault();
      this.showDigitsOnlyError(field);
    }
  }

  private showDigitsOnlyError(field: PhoneFieldName): void {
    clearTimeout(this.digitsOnlyErrorTimeoutId);
    this.digitsOnlyErrorField.set(field);
    this.digitsOnlyErrorTimeoutId = setTimeout(
      () => this.digitsOnlyErrorField.set(null),
      DIGITS_ONLY_ERROR_TIMEOUT_MS
    );
  }
}
