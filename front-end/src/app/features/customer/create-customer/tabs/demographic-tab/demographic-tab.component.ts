import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { I18nService } from '../../../../../core/i18n';

type LetterFieldName = 'firstName' | 'middleName' | 'lastName' | 'fatherName' | 'motherName';

@Component({
  selector: 'app-demographic-tab',
  imports: [ReactiveFormsModule],
  templateUrl: './demographic-tab.component.html',
  styleUrl: './demographic-tab.component.scss'
})
export class DemographicTabComponent {
  protected readonly i18n = inject(I18nService);
  private readonly formBuilder = inject(FormBuilder);

  protected readonly nationalIdError = signal(false);

  protected readonly createForm = this.formBuilder.nonNullable.group({
    firstName: [''],
    middleName: [''],
    lastName: [''],
    birthDate: [''],
    gender: [''],
    fatherName: [''],
    motherName: [''],
    nationalId: ['']
  });

  protected sanitizeLetters(event: Event, controlName: LetterFieldName): void {
    const input = event.target as HTMLInputElement;
    const lettersOnly = input.value.replace(/[^a-zA-ZçÇğĞıİöÖşŞüÜ\s]/g, '');
    this.createForm.controls[controlName].setValue(lettersOnly);
  }

  protected sanitizeNationalId(event: Event): void {
    const input = event.target as HTMLInputElement;
    const digitsOnly = input.value.replace(/\D/g, '').slice(0, 11);
    this.nationalIdError.set(input.value !== digitsOnly);
    this.createForm.controls.nationalId.setValue(digitsOnly);
  }
}
