import { Component, ElementRef, HostListener, computed, forwardRef, inject, signal } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

const WEEKDAY_LABELS = ['S', 'M', 'T', 'W', 'T', 'F', 'S'];

@Component({
  selector: 'app-date-picker',
  imports: [],
  templateUrl: './date-picker.component.html',
  styleUrl: './date-picker.component.scss',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => DatePickerComponent),
      multi: true
    }
  ]
})
export class DatePickerComponent implements ControlValueAccessor {
  private readonly elementRef = inject(ElementRef<HTMLElement>);

  protected readonly weekdayLabels = WEEKDAY_LABELS;

  protected readonly isOpen = signal(false);
  protected readonly selectedDate = signal<Date | null>(null);
  protected readonly viewDate = signal(new Date());
  protected readonly disabled = signal(false);

  private onChange: (value: string) => void = () => {};
  private onTouched: () => void = () => {};

  protected readonly displayValue = computed(() => {
    const date = this.selectedDate();
    return date ? this.formatDisplay(date) : '';
  });

  protected readonly monthLabel = computed(() =>
    this.viewDate().toLocaleDateString('en-US', { month: 'long' })
  );

  protected readonly yearLabel = computed(() => this.viewDate().getFullYear());

  protected readonly calendarDays = computed(() => {
    const view = this.viewDate();
    const year = view.getFullYear();
    const month = view.getMonth();
    const firstWeekday = new Date(year, month, 1).getDay();
    const daysInMonth = new Date(year, month + 1, 0).getDate();

    const days: (number | null)[] = [];
    for (let i = 0; i < firstWeekday; i++) {
      days.push(null);
    }
    for (let day = 1; day <= daysInMonth; day++) {
      days.push(day);
    }
    return days;
  });

  writeValue(value: string | null): void {
    if (!value) {
      this.selectedDate.set(null);
      return;
    }

    const parsed = new Date(value);
    if (!Number.isNaN(parsed.getTime())) {
      this.selectedDate.set(parsed);
      this.viewDate.set(parsed);
    }
  }

  registerOnChange(fn: (value: string) => void): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: () => void): void {
    this.onTouched = fn;
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled.set(isDisabled);
  }

  protected togglePanel(): void {
    if (this.disabled()) {
      return;
    }
    this.isOpen.update(open => !open);
  }

  protected previousMonth(): void {
    const view = this.viewDate();
    this.viewDate.set(new Date(view.getFullYear(), view.getMonth() - 1, 1));
  }

  protected nextMonth(): void {
    const view = this.viewDate();
    this.viewDate.set(new Date(view.getFullYear(), view.getMonth() + 1, 1));
  }

  protected isSelectedDay(day: number): boolean {
    const selected = this.selectedDate();
    if (!selected) {
      return false;
    }
    const view = this.viewDate();
    return (
      selected.getFullYear() === view.getFullYear() &&
      selected.getMonth() === view.getMonth() &&
      selected.getDate() === day
    );
  }

  protected selectDay(day: number): void {
    const view = this.viewDate();
    const date = new Date(view.getFullYear(), view.getMonth(), day);
    this.selectedDate.set(date);
    this.onChange(this.formatIso(date));
    this.onTouched();
    this.isOpen.set(false);
  }

  @HostListener('document:click', ['$event'])
  protected onDocumentClick(event: MouseEvent): void {
    if (!this.elementRef.nativeElement.contains(event.target as Node)) {
      this.isOpen.set(false);
    }
  }

  private formatDisplay(date: Date): string {
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    return `${day} / ${month} / ${date.getFullYear()}`;
  }

  private formatIso(date: Date): string {
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    return `${date.getFullYear()}-${month}-${day}`;
  }
}
