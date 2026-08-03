import { Component, signal, ChangeDetectionStrategy } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from '../../shared/components/navbar/navbar.component';
import { SidebarComponent } from '../../shared/components/sidebar/sidebar.component';

@Component({
  selector: 'app-main-layout',
  imports: [RouterOutlet, NavbarComponent, SidebarComponent],
  templateUrl: './main-layout.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './main-layout.component.scss'
})
export class MainLayoutComponent {
  protected readonly sidebarOpen = signal(true);

  protected toggleSidebar(): void {
    this.sidebarOpen.update(open => !open);
  }
}
