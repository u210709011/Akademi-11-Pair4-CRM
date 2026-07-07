import { Routes } from '@angular/router';

import { AuthLayoutComponent } from './layouts/auth-layout/auth-layout.component';
import { MainLayoutComponent } from './layouts/main-layout/main-layout.component';

import { LoginComponent } from './features/auth/login/login.component';
import { SearchCustomerComponent } from './features/customer/search-customer/search-customer.component';
import { CreateCustomerComponent } from './features/customer/create-customer/create-customer.component';

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
      }
    ]
  },
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full'
  }
];