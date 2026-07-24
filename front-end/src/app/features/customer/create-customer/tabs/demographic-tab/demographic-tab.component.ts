import { ChangeDetectionStrategy, Component, effect, inject, signal } from '@angular/core';
import { form, FormField, maxLength, required } from '@angular/forms/signals';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { IndividualInfo } from '../../../../../core/customer';
import { I18nService } from '../../../../../core/i18n';
import { DatePickerHeaderComponent } from '../../../../../shared/components/date-picker-header/date-picker-header.component';
import { CreateCustomerFormStateService } from '../../create-customer.component';

type LetterFieldName = 'firstName' | 'middleName' | 'lastName' | 'fatherName' | 'motherName';

const LETTER_FIELDS: LetterFieldName[] = ['firstName', 'middleName', 'lastName', 'fatherName', 'motherName'];

@Component({
  selector: 'app-demographic-tab',
  imports: [FormField, MatDatepickerModule, MatFormFieldModule, MatInputModule],
  templateUrl: './demographic-tab.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './demographic-tab.component.scss'
})
export class DemographicTabComponent {
  protected readonly i18n = inject(I18nService);
  protected readonly formState = inject(CreateCustomerFormStateService);

  protected readonly nationalIdError = signal(false);
  protected readonly today = new Date();
  protected readonly datePickerHeader = DatePickerHeaderComponent;

  protected readonly letterFieldErrors = signal<Record<LetterFieldName, boolean>>({
    firstName: false,
    middleName: false,
    lastName: false,
    fatherName: false,
    motherName: false
  });

  // model, sekmeler arasi gecince kaybolmamasi icin CreateCustomerFormStateService'te tutulur
  protected readonly demographicModel = this.formState.demographicModel;

  protected readonly demographicForm = form(this.demographicModel, path => {
    required(path.firstName);
    required(path.lastName);
    required(path.birthDate);
    required(path.gender);
    required(path.nationalId);
    maxLength(path.nationalId, 11);
  });

  constructor() {
    // form gecerliligi/degeri degistikce sihirbazin ortak state'ine (CreateCustomerFormStateService) yansitilir
    effect(() => {
      const valid = this.demographicForm().valid();
      this.formState.demographicValid.set(valid);
      this.formState.demographicValue.set(valid ? this.toIndividualInfo() : null);
    });

    // her harf alani icin: rakam girilirse anlik olarak temizle ve hata bayragini kaldir
    for (const field of LETTER_FIELDS) {
      effect(() => this.sanitizeLetterField(field));
    }

    // TC kimlik no icin: rakam olmayan karakterleri temizle, 11 haneyle sinirla
    effect(() => {
      const raw = this.demographicForm.nationalId().value();
      const digitsOnly = raw.replace(/\D/g, '').slice(0, 11);
      if (digitsOnly !== raw) {
        this.demographicForm.nationalId().value.set(digitsOnly);
        this.nationalIdError.set(true);
      }
    });
  }

  // formdaki Date/string gender'i backend'in beklediği IndividualInfo şekline (dd/MM/yyyy, numeric genderId) çevirdi
  private toIndividualInfo(): IndividualInfo {
    const value = this.demographicModel();
    const birthDate = value.birthDate as Date;

    const day = String(birthDate.getDate()).padStart(2, '0');
    const month = String(birthDate.getMonth() + 1).padStart(2, '0');
    const year = birthDate.getFullYear();

    return {
      firstName: value.firstName,
      middleName: value.middleName || null,
      lastName: value.lastName,
      birthDate: `${day}/${month}/${year}`,
      genderId: Number(value.gender),
      motherName: value.motherName || null,
      fatherName: value.fatherName || null,
      nationalId: value.nationalId
    };
  }

  protected setLetterFieldError(field: LetterFieldName, hasError: boolean): void {
    this.letterFieldErrors.update(errors => ({ ...errors, [field]: hasError }));
  }

  private sanitizeLetterField(field: LetterFieldName): void {
    const raw = this.demographicForm[field]().value();
    const lettersOnly = raw.replace(/[^a-zA-ZçÇğĞıİöÖşŞüÜ\s]/g, '');
    if (lettersOnly !== raw) {
      this.demographicForm[field]().value.set(lettersOnly);
      this.setLetterFieldError(field, true);
    }
  }
}
