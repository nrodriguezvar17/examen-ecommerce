import { Injectable, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed, toObservable } from '@angular/core/rxjs-interop';
import { debounceTime, of, switchMap, tap } from 'rxjs';
import { CheckoutApi, QuoteRequest } from '../api/checkout-api';
import { DiscountBreakdown } from '../models/discount-breakdown.model';
import { CartStore } from './cart.store';

/**
 * <b>Facade</b>: the single entry point the checkout UI talks to. Hides the coordination
 * between the {@link CartStore} and the quote API — debounced recompute on every cart /
 * coupon change, mapping to a view model, coupon-error detection.
 */
@Injectable({ providedIn: 'root' })
export class CheckoutFacade {
  private readonly cart = inject(CartStore);
  private readonly api = inject(CheckoutApi);

  private readonly _coupon = signal<string | null>(null);
  private readonly _breakdown = signal<DiscountBreakdown | null>(null);
  private readonly _loading = signal(false);
  private readonly _couponError = signal<string | null>(null);

  readonly coupon = this._coupon.asReadonly();
  readonly breakdown = this._breakdown.asReadonly();
  readonly loading = this._loading.asReadonly();
  readonly couponError = this._couponError.asReadonly();
  readonly capReached = computed((): boolean => this._breakdown()?.capReached ?? false);

  private readonly request = computed((): QuoteRequest | null => {
    const items = this.cart.items();
    if (items.length === 0) {
      return null;
    }
    return {
      items: items.map((item) => ({ productId: item.productId, quantity: item.quantity })),
      couponCode: this._coupon(),
    };
  });

  constructor() {
    toObservable(this.request)
      .pipe(
        tap(() => {
          this._loading.set(true);
          this._couponError.set(null);
        }),
        debounceTime(250),
        switchMap((req) => (req ? this.api.quote(req) : of(null))),
        takeUntilDestroyed(),
      )
      .subscribe((breakdown) => {
        this._loading.set(false);
        this._breakdown.set(breakdown);
        const couponEntered = this._coupon() !== null;
        const couponApplied = breakdown?.lines.some((line) => line.type === 'COUPON') ?? false;
        if (breakdown && couponEntered && !couponApplied) {
          this._couponError.set('Cupón no válido o expirado');
        }
      });
  }

  applyCoupon(code: string): void {
    const trimmed = code.trim();
    this._coupon.set(trimmed.length > 0 ? trimmed : null);
  }

  clearCoupon(): void {
    this._coupon.set(null);
    this._couponError.set(null);
  }
}
