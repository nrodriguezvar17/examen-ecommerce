import { ChangeDetectionStrategy, Component } from '@angular/core';
import { CartPanelComponent } from '../cart/cart-panel.component';
import { ProductListComponent } from '../catalog/product-list.component';

/** Route `/` — the storefront: catalog on the left, live cart on the right (HU1–HU4). */
@Component({
  selector: 'app-shop-page',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [ProductListComponent, CartPanelComponent],
  templateUrl: './shop-page.component.html',
  styleUrl: './shop-page.component.scss',
})
export class ShopPageComponent {}
