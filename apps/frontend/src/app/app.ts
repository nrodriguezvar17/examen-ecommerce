import { ChangeDetectionStrategy, Component } from '@angular/core';
import { CartPanelComponent } from './features/cart/cart-panel.component';
import { ProductListComponent } from './features/catalog/product-list.component';
import { GlobalLoaderComponent } from './shared/global-loader/global-loader.component';
import { SiteFooterComponent } from './shared/site-footer/site-footer.component';

@Component({
  selector: 'app-root',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [GlobalLoaderComponent, ProductListComponent, CartPanelComponent, SiteFooterComponent],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {}
