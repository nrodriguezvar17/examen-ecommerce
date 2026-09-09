import { Routes } from '@angular/router';
import { OrdersPageComponent } from './features/orders/orders-page.component';
import { ShopPageComponent } from './features/shop/shop-page.component';

export const routes: Routes = [
  { path: '', component: ShopPageComponent, title: 'Davitienda' },
  { path: 'compras', component: OrdersPageComponent, title: 'Mis compras · Davitienda' },
  { path: '**', redirectTo: '' },
];
