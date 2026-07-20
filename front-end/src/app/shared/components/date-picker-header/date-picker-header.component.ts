import { Component, inject } from '@angular/core';
import { DateAdapter } from '@angular/material/core';
import { MatCalendar } from '@angular/material/datepicker';

const YEARS_PER_PAGE = 24;

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

// ok tuşları ile sıralanan yıllar arasında ilerleyebilmek için eklendi
  protected previousClicked(): void {
    this.calendar.activeDate = this.shiftActiveDate(-1);
  }

  protected nextClicked(): void {
    this.calendar.activeDate = this.shiftActiveDate(1);
  }

  private shiftActiveDate(direction: 1 | -1): D {
    const view = this.calendar.currentView;

    if (view === 'month') {
      return this.dateAdapter.addCalendarMonths(this.calendar.activeDate, direction);
    }

    const years = view === 'year' ? direction : direction * YEARS_PER_PAGE;
    return this.dateAdapter.addCalendarYears(this.calendar.activeDate, years);
  }

  protected openMonthView(): void {
    this.calendar.currentView = 'year';
  }

  protected openYearView(): void {
    this.calendar.currentView = 'multi-year';
  }
}
