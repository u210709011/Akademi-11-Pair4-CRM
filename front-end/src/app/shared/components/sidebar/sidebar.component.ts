import { Component, signal, inject, ChangeDetectionStrategy } from '@angular/core';
import { I18nService } from '../../../core/i18n';

type SidebarItem = 'b2c' | 'b2b' | 'approvals';

@Component({
  selector: 'app-sidebar',
  imports: [],
  templateUrl: './sidebar.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './sidebar.component.scss'
})
export class SidebarComponent {
    protected readonly i18n = inject(I18nService);

  protected readonly activeItem = signal<SidebarItem>('b2c');

  protected selectItem(item: SidebarItem): void {
    this.activeItem.set(item);
  }
}
