import { Component, EventEmitter, Input, Output } from '@angular/core';

export type ButtonVariant = 'primary' | 'secondary' | 'danger';

// Neredeyse her sayfadaki modal-actions footer'inda (cancel/save/delete-confirm + kaydederken
// gosterilen spinner) birebir ayni markup tekrar ediyordu (detail-customer'da tek basina 9 kez) -
// bkz. styles.scss'teki .cancel-button/.save-button/.delete-confirm-button + create-customer/
// new-sale/update-customer'in kendi scss'lerinde bunlarin neredeyse birebir kopyalari. Hepsi bu
// component'e tasindi.
@Component({
  selector: 'app-button',
  imports: [],
  templateUrl: './button.component.html',
  styleUrl: './button.component.scss'
})
export class ButtonComponent {
  @Input() variant: ButtonVariant = 'primary';
  @Input() type: 'button' | 'submit' = 'button';
  @Input() disabled = false;
  @Input() loading = false;
  /** loading=true iken gosterilecek metin ("Deleting..." gibi) - verilmezse label aynen kalir. */
  @Input() loadingLabel?: string;
  @Output() readonly buttonClick = new EventEmitter<void>();

  protected onClick(): void {
    if (!this.disabled && !this.loading) {
      this.buttonClick.emit();
    }
  }
}
