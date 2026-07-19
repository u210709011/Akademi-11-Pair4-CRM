import { Routes } from '@angular/router';

import { AuthLayoutComponent } from './layouts/auth-layout/auth-layout.component';
import { MainLayoutComponent } from './layouts/main-layout/main-layout.component';

import { LoginComponent } from './features/auth/login/login.component';
import { SearchCustomerComponent } from './features/customer/search-customer/search-customer.component';
import { CreateCustomerComponent } from './features/customer/create-customer/create-customer.component';
import { DetailCustomerComponent } from './features/customer/detail-customer/detail-customer.component';
import { ApprovalsComponent } from './features/approvals/approvals.component';
import { B2bComponent } from './features/b2b/b2b.component';

export const routes: Routes = [
  {
    path: '',
    component: AuthLayoutComponent,
    children: [
      {
        path: 'login',
        component: LoginComponent
      }
    ]
  },
  {
    path: '',
    component: MainLayoutComponent,
    children: [
      {
        path: 'search-customer',
        component: SearchCustomerComponent
      },
      {
        path: 'create-customer',
        component: CreateCustomerComponent
      },
      {
        path: 'detail-customer/:custId',
        component: DetailCustomerComponent
      },
      {
        path: 'approvals',
        component: ApprovalsComponent
      },
      {
        path: 'b2b',
        component: B2bComponent
      }
    ]
  },
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full'
  }
];