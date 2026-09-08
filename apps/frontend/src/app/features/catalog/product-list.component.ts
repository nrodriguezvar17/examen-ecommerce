import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { CatalogApi } from '../../core/api/catalog-api';
import { Product } from '../../core/models/product.model';
import { CartStore } from '../../core/state/cart.store';
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

  protected readonly products = toSignal(inject(CatalogApi).products(), {
    initialValue: [] as readonly Product[],
  });
  protected readonly loading = computed((): boolean => this.products().length === 0);

  onAdd(product: Product): void {
    this.cart.add(product);
  }
}
