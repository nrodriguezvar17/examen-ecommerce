import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { Product } from '../../core/models/product.model';
import { CartStore } from '../../core/state/cart.store';
import { CatalogStore } from '../../core/state/catalog.store';
import { ProductCardComponent } from './product-card.component';

@Component({
  selector: 'app-product-list',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [ProductCardComponent],
  templateUrl: './product-list.component.html',
  styleUrl: './product-list.component.scss',
})
export class ProductListComponent {
  private readonly cart = inject(CartStore);
  protected readonly catalog = inject(CatalogStore);

  protected readonly products = this.catalog.products;
  protected readonly loading = this.catalog.loading;

  constructor() {
    this.catalog.reload();
  }

  onAdd(product: Product): void {
    this.cart.add(product);
  }
}
