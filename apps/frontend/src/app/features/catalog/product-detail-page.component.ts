import { ChangeDetectionStrategy, Component, computed, inject, input } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';
import { catchError, map, of, switchMap } from 'rxjs';
import { ProductDetail } from '../../core/models/product-detail.model';
import { CatalogService } from '../../core/services/catalog.service';
import { CartStore } from '../../core/state/cart.store';
import { MoneyPipe } from '../../shared/money.pipe';
import { StarRatingComponent } from '../../shared/star-rating.component';

interface DetailState {
  readonly loaded: boolean;
  readonly product: ProductDetail | null;
}

/** Route `/producto/:id` — full product page (image, brand, description, reviews, add to cart). */
@Component({
  selector: 'app-product-detail-page',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink, MoneyPipe, StarRatingComponent],
  templateUrl: './product-detail-page.component.html',
  styleUrl: './product-detail-page.component.scss',
})
export class ProductDetailPageComponent {
  private readonly catalog = inject(CatalogService);
  private readonly cart = inject(CartStore);

  /** Bound from the route param by `withComponentInputBinding()`. */
  readonly id = input.required<string>();

  private readonly state = toSignal(
    toObservable(this.id).pipe(
      switchMap((id) =>
        this.catalog.detail(Number(id)).pipe(
          map((product): DetailState => ({ loaded: true, product })),
          catchError(() => of<DetailState>({ loaded: true, product: null })),
        ),
      ),
    ),
    { initialValue: { loaded: false, product: null } as DetailState },
  );

  protected readonly product = computed((): ProductDetail | null => this.state().product);
  protected readonly loading = computed((): boolean => !this.state().loaded);
  protected readonly notFound = computed(
    (): boolean => this.state().loaded && this.state().product === null,
  );

  protected readonly inCart = computed((): number => {
    const current = this.product();
    return current ? this.cart.quantityOf(current.id) : 0;
  });
  protected readonly soldOut = computed((): boolean => {
    const current = this.product();
    return !!current && this.inCart() >= current.stock;
  });

  protected addToCart(): void {
    const current = this.product();
    if (current && !this.soldOut()) {
      this.cart.add(current);
    }
  }
}
