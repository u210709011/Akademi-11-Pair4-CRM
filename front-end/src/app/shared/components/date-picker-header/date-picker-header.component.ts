import { Component, inject } from '@angular/core';
import { DateAdapter } from '@angular/material/core';
import { MatCalendar } from '@angular/material/datepicker';

@Component({
  selector: 'app-date-picker-header',
  imports: [],
  templateUrl: './date-picker-header.component.html',
  styleUrl: './date-picker-header.component.scss'
})
export class DatePickerHeaderComponent<D> {
  protected readonly calendar = inject<MatCalendar<D>>(MatCalendar);
  private readonly dateAdapter = inject<DateAdapter<D>>(DateAdapter);

  protected get monthLabel(): string {
    return (this.calendar.activeDate as unknown as Date).toLocaleDateString('en-US', { month: 'long' });
  }

  protected get yearLabel(): string {
    return String((this.calendar.activeDate as unknown as Date).getFullYear());
  }

  protected previousClicked(): void {
    this.calendar.activeDate = this.dateAdapter.addCalendarMonths(this.calendar.activeDate, -1);
  }

  protected nextClicked(): void {
    this.calendar.activeDate = this.dateAdapter.addCalendarMonths(this.calendar.activeDate, 1);
  }
}
