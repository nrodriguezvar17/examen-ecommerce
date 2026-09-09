import { Routes } from '@angular/router';
import { ProductDetailPageComponent } from './features/catalog/product-detail-page.component';
import { OrdersPageComponent } from './features/orders/orders-page.component';
import { ShopPageComponent } from './features/shop/shop-page.component';

export const routes: Routes = [
  { path: '', component: ShopPageComponent, title: 'Davitienda' },
  { path: 'producto/:id', component: ProductDetailPageComponent, title: 'Producto · Davitienda' },
  { path: 'compras', component: OrdersPageComponent, title: 'Mis compras · Davitienda' },
  { path: '**', redirectTo: '' },
];
