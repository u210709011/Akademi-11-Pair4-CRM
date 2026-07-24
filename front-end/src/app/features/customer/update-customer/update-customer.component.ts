import { ChangeDetectionStrategy, Component, effect, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { form, FormField, maxLength, required } from '@angular/forms/signals';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { IndividualInfo, CustomerService } from '../../../core/customer';
import { I18nService } from '../../../core/i18n';
import { DatePickerHeaderComponent } from '../../../shared/components/date-picker-header/date-picker-header.component';

type LetterFieldName = 'firstName' | 'middleName' | 'lastName' | 'fatherName' | 'motherName';

const LETTER_FIELDS: LetterFieldName[] = ['firstName', 'middleName', 'lastName', 'fatherName', 'motherName'];

interface UpdateCustomerFormModel {
  firstName: string;
  middleName: string;
  lastName: string;
  birthDate: Date | null;
  gender: string;
  fatherName: string;
  motherName: string;
  nationalId: string;
}

const EMPTY_MODEL: UpdateCustomerFormModel = {
  firstName: '',
  middleName: '',
  lastName: '',
  birthDate: null,
  gender: '',
  fatherName: '',
  motherName: '',
  nationalId: ''
};

// GET /individual, backend'in aksine (PUT istegi dd/MM/yyyy bekliyor), birthDate'i ISO (yyyy-MM-dd) formatinda donuyor.
function parseBirthDate(value: string): Date {
  const [year, month, day] = value.split('-').map(Number);
  return new Date(year, month - 1, day);
}

@Component({
  selector: 'app-update-customer',
  imports: [FormField, RouterLink, MatDatepickerModule, MatFormFieldModule, MatInputModule],
  templateUrl: './update-customer.component.html',
  styleUrl: './update-customer.component.scss',
  changeDetection: ChangeDetectionStrategy.Eager
})
export class UpdateCustomerComponent {
  protected readonly i18n = inject(I18nService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly customerService = inject(CustomerService);

  protected readonly custId = Number(this.route.snapshot.paramMap.get('custId'));

  protected readonly isLoading = signal(true);
  protected readonly loadError = signal(false);
  protected readonly isSaving = signal(false);
  protected readonly saveError = signal<string | null>(null);
  protected readonly customerName = signal('');

  protected readonly today = new Date();
  protected readonly datePickerHeader = DatePickerHeaderComponent;

  protected readonly nationalIdError = signal(false);
  protected readonly letterFieldErrors = signal<Record<LetterFieldName, boolean>>({
    firstName: false,
    middleName: false,
    lastName: false,
    fatherName: false,
    motherName: false
  });

  protected readonly updateModel = signal<UpdateCustomerFormModel>({ ...EMPTY_MODEL });

  protected readonly updateForm = form(this.updateModel, path => {
    required(path.firstName);
    maxLength(path.firstName, 50);
    maxLength(path.middleName, 50);
    required(path.lastName);
    maxLength(path.lastName, 50);
    required(path.birthDate);
    required(path.gender);
    maxLength(path.fatherName, 50);
    maxLength(path.motherName, 50);
    required(path.nationalId);
    maxLength(path.nationalId, 11);
  });

  constructor() {
    this.customerService.getIndividual(this.custId).subscribe({
      next: response => {
        this.customerName.set(`${response.firstName} ${response.lastName}`);
        // .reset(value) kullanilir (duz .set() degil) - matDatepicker gibi ControlValueAccessor
        // uzerinden baglanan alanlar, programatik model degisikliklerini .reset()'in tetikledigi
        // writeValue() cagrisi olmadan gorsel olarak yansitmiyor.
        this.updateForm().reset({
          firstName: response.firstName,
          middleName: response.middleName ?? '',
          lastName: response.lastName,
          birthDate: parseBirthDate(response.birthDate),
          gender: String(response.genderId),
          fatherName: response.fatherName ?? '',
          motherName: response.motherName ?? '',
          nationalId: response.nationalId
        });
        this.isLoading.set(false);
      },
      error: () => {
        this.loadError.set(true);
        this.isLoading.set(false);
      }
    });

    for (const field of LETTER_FIELDS) {
      effect(() => this.sanitizeLetterField(field));
    }

    effect(() => {
      const raw = this.updateForm.nationalId().value();
      const digitsOnly = raw.replace(/\D/g, '').slice(0, 11);
      if (digitsOnly !== raw) {
        this.updateForm.nationalId().value.set(digitsOnly);
        this.nationalIdError.set(true);
      }
    });
  }

  private sanitizeLetterField(field: LetterFieldName): void {
    const raw = this.updateForm[field]().value();
    const lettersOnly = raw.replace(/[^a-zA-ZçÇğĞıİöÖşŞüÜ\s]/g, '');
    if (lettersOnly !== raw) {
      this.updateForm[field]().value.set(lettersOnly);
      this.letterFieldErrors.update(errors => ({ ...errors, [field]: true }));
    }
  }

  protected setLetterFieldError(field: LetterFieldName, hasError: boolean): void {
    this.letterFieldErrors.update(errors => ({ ...errors, [field]: hasError }));
  }

  protected save(): void {
    if (this.updateForm().invalid()) {
      return;
    }

    this.isSaving.set(true);
    this.saveError.set(null);

    this.customerService.updateIndividual(this.custId, this.toIndividualInfo()).subscribe({
      next: () => {
        this.isSaving.set(false);
        this.router.navigate(['/detail-customer', this.custId]);
      },
      error: (httpError: HttpErrorResponse) => {
        this.isSaving.set(false);
        this.saveError.set(
          httpError.status === 409 ? this.i18n.t('create.identityDuplicate') : this.i18n.t('update.error')
        );
      }
    });
  }

  protected previous(): void {
    this.router.navigate(['/detail-customer', this.custId]);
  }

  private toIndividualInfo(): IndividualInfo {
    const value = this.updateModel();
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
}
