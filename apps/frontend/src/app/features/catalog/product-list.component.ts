import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { Product } from '../../core/models/product.model';
import { CartStore } from '../../core/state/cart.store';
import { CatalogStore } from '../../core/state/catalog.store';
import { ProductCardComponent } from './product-card.component';
import { ProductDetailDialogComponent } from './product-detail-dialog.component';

@Component({
  selector: 'app-product-list',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [ProductCardComponent, ProductDetailDialogComponent],
  templateUrl: './product-list.component.html',
  styleUrl: './product-list.component.scss',
})
export class ProductListComponent {
  private readonly cart = inject(CartStore);
  protected readonly catalog = inject(CatalogStore);

  protected readonly products = this.catalog.products;
  protected readonly loading = this.catalog.loading;
  protected readonly selectedId = signal<number | null>(null);

  constructor() {
    this.catalog.reload();
  }

  onAdd(product: Product): void {
    this.cart.add(product);
  }
}
