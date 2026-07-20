import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { IndividualInfo } from '../../../../../core/customer';
import { I18nService } from '../../../../../core/i18n';
import { DatePickerHeaderComponent } from '../../../../../shared/components/date-picker-header/date-picker-header.component';
import { CreateCustomerFormStateService } from '../../create-customer.component';

type LetterFieldName = 'firstName' | 'middleName' | 'lastName' | 'fatherName' | 'motherName';

@Component({
  selector: 'app-demographic-tab',
  imports: [ReactiveFormsModule, MatDatepickerModule, MatFormFieldModule, MatInputModule],
  templateUrl: './demographic-tab.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './demographic-tab.component.scss'
})
export class DemographicTabComponent {
  protected readonly i18n = inject(I18nService);
  private readonly formBuilder = inject(FormBuilder);
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

  protected readonly createForm = this.formBuilder.nonNullable.group({
    firstName: ['', Validators.required],
    middleName: [''],
    lastName: ['', Validators.required],
    birthDate: this.formBuilder.control<Date | null>(null, Validators.required),
    gender: ['', Validators.required],
    fatherName: [''],
    motherName: [''],
    nationalId: ['', Validators.required]
  });

  constructor() {
    this.syncFormState();
    this.createForm.valueChanges.subscribe(() => this.syncFormState());
    this.createForm.statusChanges.subscribe(() => this.syncFormState());
  }

  private syncFormState(): void {
    this.formState.demographicValid.set(this.createForm.valid);
    this.formState.demographicValue.set(this.createForm.valid ? this.toIndividualInfo() : null);
  }

  // formdaki Date/string gender'i backend'in beklediği IndividualInfo şekline (dd/MM/yyyy, numeric genderId) çevirdi
  private toIndividualInfo(): IndividualInfo {
    const value = this.createForm.getRawValue();
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

  protected sanitizeLetters(event: Event, controlName: LetterFieldName): void {
    const input = event.target as HTMLInputElement;
    const lettersOnly = input.value.replace(/[^a-zA-ZçÇğĞıİöÖşŞüÜ\s]/g, '');
    this.setLetterFieldError(controlName, input.value !== lettersOnly);
    this.createForm.controls[controlName].setValue(lettersOnly);
  }

  protected sanitizeNationalId(event: Event): void {
    const input = event.target as HTMLInputElement;
    const digitsOnly = input.value.replace(/\D/g, '').slice(0, 11);
    this.nationalIdError.set(input.value !== digitsOnly);
    this.createForm.controls.nationalId.setValue(digitsOnly);
  }
}
