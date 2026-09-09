import { ChangeDetectionStrategy, Component, computed, inject, input, output } from '@angular/core';
import { MoneyPipe } from '../../shared/money.pipe';
import { StarRatingComponent } from '../../shared/star-rating.component';
import { Product } from '../../core/models/product.model';
import { CartStore } from '../../core/state/cart.store';

@Component({
  selector: 'app-product-card',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [MoneyPipe, StarRatingComponent],
  templateUrl: './product-card.component.html',
  styleUrl: './product-card.component.scss',
})
export class ProductCardComponent {
  private readonly cart = inject(CartStore);

  readonly product = input.required<Product>();
  readonly add = output<Product>();
  readonly details = output<Product>();

  /** true when every available unit of this product is already in the cart. */
  readonly atStockLimit = computed(
    (): boolean => this.cart.quantityOf(this.product().id) >= this.product().stock,
  );
}
