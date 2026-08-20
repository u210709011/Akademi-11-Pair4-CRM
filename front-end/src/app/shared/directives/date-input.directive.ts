import { Directive, HostListener } from '@angular/core';


@Directive({
  selector: 'input[appDateInput]'
})
export class DateInputDirective {
  @HostListener('keydown', ['$event'])
  onKeydown(event: KeyboardEvent): void {
    if (event.ctrlKey || event.metaKey) {
      return;
    }
    // Backspace/Tab/ok tuslari vb. - Backspace disindakiler serbest birakilir.
    if (event.key.length > 1) {
      if (event.key === 'Backspace' || event.key === 'Delete') {
        event.preventDefault();
        this.applyDigits(event, this.currentDigits(event).slice(0, -1));
      }
      return;
    }

    event.preventDefault();

    if (!/[0-9]/.test(event.key)) {
      return;
    }

    const digits = this.currentDigits(event);
    if (digits.length >= 8) {
      return;
    }

    this.applyDigits(event, digits + event.key);
  }

  private currentDigits(event: KeyboardEvent): string {
    const input = event.target as HTMLInputElement;
    return input.value.replace(/\D/g, '').slice(0, 8);
  }

  private applyDigits(event: KeyboardEvent, digits: string): void {
    const input = event.target as HTMLInputElement;
    input.value = this.format(digits);
    const caret = input.value.length;
    input.setSelectionRange(caret, caret);
    input.dispatchEvent(new Event('input', { bubbles: true }));
  }

  private format(digits: string): string {
    if (digits.length > 4) {
      return `${digits.slice(0, 2)}/${digits.slice(2, 4)}/${digits.slice(4)}`;
    }
    if (digits.length > 2) {
      return `${digits.slice(0, 2)}/${digits.slice(2)}`;
    }
    return digits;
  }
}
