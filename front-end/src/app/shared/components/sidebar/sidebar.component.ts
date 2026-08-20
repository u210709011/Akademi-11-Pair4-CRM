import { Component, signal, inject, ChangeDetectionStrategy } from '@angular/core';
import { Router } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { AuthService } from '../../../core/auth';

type SidebarItem = 'b2c' | 'b2b' | 'approvals';

const ROUTE_BY_ITEM: Record<SidebarItem, string> = {
  b2c: '/search-customer',
  b2b: '/b2b',
  approvals: '/approvals'
};

@Component({
  selector: 'app-sidebar',
  imports: [TranslatePipe],
  templateUrl: './sidebar.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './sidebar.component.scss'
})
export class SidebarComponent {
    private readonly router = inject(Router);
    private readonly authService = inject(AuthService);

  protected readonly activeItem = signal<SidebarItem>('b2c');

  protected selectItem(item: SidebarItem): void {
    this.activeItem.set(item);

    const route = ROUTE_BY_ITEM[item];
    if (route) {
      this.router.navigateByUrl(route);
    }
  }

  // UC-EACRML-001 Alt Senaryo 5: cikis - oturumu sonlandirir ve Login ekranina doner.
  protected logout(): void {
    this.authService.logout().subscribe(() => this.router.navigateByUrl('/login'));
  }
}
