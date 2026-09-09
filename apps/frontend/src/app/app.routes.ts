import { Routes } from '@angular/router';
import { ProductDetailPageComponent } from './features/catalog/product-detail-page.component';
import { LandingPageComponent } from './features/landing/landing-page.component';
import { OrdersPageComponent } from './features/orders/orders-page.component';
import { ShopPageComponent } from './features/shop/shop-page.component';

export const routes: Routes = [
  { path: '', component: LandingPageComponent, title: 'Davitienda' },
  { path: 'store', component: ShopPageComponent, title: 'Tienda · Davitienda' },
  { path: 'producto/:id', component: ProductDetailPageComponent, title: 'Producto · Davitienda' },
  { path: 'shopping', component: OrdersPageComponent, title: 'Mis compras · Davitienda' },
  { path: '**', redirectTo: '' },
];
