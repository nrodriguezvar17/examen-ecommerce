import { ChangeDetectionStrategy, Component, computed, inject, input, output } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { switchMap } from 'rxjs';
import { CatalogService } from '../../core/services/catalog.service';
import { CartStore } from '../../core/state/cart.store';
import { MoneyPipe } from '../../shared/money.pipe';
import { StarRatingComponent } from '../../shared/star-rating.component';

/** Modal with the full product info: brand, description, rating and every review (HU1). */
@Component({
  selector: 'app-product-detail-dialog',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [MoneyPipe, StarRatingComponent],
  templateUrl: './product-detail-dialog.component.html',
  styleUrl: './product-detail-dialog.component.scss',
  host: { '(document:keydown.escape)': 'close()' },
})
export class ProductDetailDialogComponent {
  private readonly cart = inject(CartStore);
  private readonly catalog = inject(CatalogService);

  readonly productId = input.required<number>();
  readonly closed = output<void>();

  protected readonly detail = toSignal(
    toObservable(this.productId).pipe(switchMap((id) => this.catalog.detail(id))),
    { initialValue: null },
  );

  protected readonly inCart = computed((): number => {
    const product = this.detail();
    return product ? this.cart.quantityOf(product.id) : 0;
  });
  protected readonly soldOut = computed((): boolean => {
    const product = this.detail();
    return !!product && this.inCart() >= product.stock;
  });

  protected addToCart(): void {
    const product = this.detail();
    if (product && !this.soldOut()) {
      this.cart.add(product);
    }
  }

  protected close(): void {
    this.closed.emit();
  }
}
