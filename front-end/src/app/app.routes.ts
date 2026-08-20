import { Routes } from '@angular/router';

import { authGuard } from './core/auth';
import { AuthLayoutComponent } from './layouts/auth-layout/auth-layout.component';
import { MainLayoutComponent } from './layouts/main-layout/main-layout.component';

export const routes: Routes = [
  {
    path: '',
    component: AuthLayoutComponent,
    children: [
      {
        path: '',
        redirectTo: 'login',
        pathMatch: 'full'
      },
      {
        path: 'login',
        loadComponent: () => import('./features/auth/login/login.component').then(m => m.LoginComponent)
      }
    ]
  },
  {
    path: '',
    component: MainLayoutComponent,
    canActivate: [authGuard],
    children: [
      {
        path: 'search-customer',
        loadComponent: () =>
          import('./features/customer/search-customer/search-customer.component').then(m => m.SearchCustomerComponent)
      },
      {
        path: 'create-customer',
        loadComponent: () =>
          import('./features/customer/create-customer/create-customer.component').then(m => m.CreateCustomerComponent)
      },
      {
        path: 'detail-customer/:custId',
        loadComponent: () =>
          import('./features/customer/detail-customer/detail-customer.component').then(m => m.DetailCustomerComponent)
      },
      {
        path: 'detail-customer/:custId/update',
        loadComponent: () =>
          import('./features/customer/update-customer/update-customer.component').then(m => m.UpdateCustomerComponent)
      },
      {
        path: 'new-sale/:custId/:custAcctId',
        loadComponent: () => import('./features/customer/new-sale/new-sale.component').then(m => m.NewSaleComponent)
      },
      {
        path: 'approvals',
        loadComponent: () => import('./features/approvals/approvals.component').then(m => m.ApprovalsComponent)
      },
      {
        path: 'b2b',
        loadComponent: () => import('./features/b2b/b2b.component').then(m => m.B2bComponent)
      }
    ]
  }
];
