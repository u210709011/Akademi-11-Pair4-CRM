import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { I18nService } from '../../../../../core/i18n';

type PhoneFieldName = 'homePhone' | 'mobilePhone' | 'fax';

@Component({
  selector: 'app-contact-tab',
  imports: [ReactiveFormsModule],
  templateUrl: './contact-tab.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './contact-tab.component.scss'
})
export class ContactTabComponent {
  protected readonly i18n = inject(I18nService);
  private readonly formBuilder = inject(FormBuilder);

  protected readonly createForm = this.formBuilder.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    homePhone: [''],
    mobilePhone: ['', Validators.required],
    fax: ['']
  });

  protected sanitizePhone(event: Event, controlName: PhoneFieldName): void {
    const input = event.target as HTMLInputElement;
    let digitsOnly = input.value.replace(/\D/g, '').slice(0, 10);

    if (controlName === 'mobilePhone') {
      while (digitsOnly.length > 0 && digitsOnly[0] !== '5') {
        digitsOnly = digitsOnly.slice(1);
      }
    }

    this.createForm.controls[controlName].setValue(digitsOnly);
  }
}
