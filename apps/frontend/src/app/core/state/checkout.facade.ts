import { HttpErrorResponse } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed, toObservable } from '@angular/core/rxjs-interop';
import { debounceTime, of, switchMap, tap } from 'rxjs';
import { DiscountBreakdown } from '../models/discount-breakdown.model';
import { CartStore } from './cart.store';
import { CatalogStore } from './catalog.store';
import { CheckoutService, OrderConfirmation, QuoteRequest } from '../services/checkout.service';

/**
 * <b>Facade</b>: the single entry point the checkout UI talks to. Hides the coordination
 * between the {@link CartStore} and the checkout API — debounced quote recompute on every
 * cart / coupon change (HU2), coupon-error detection, and order confirmation (HU3).
 */
@Injectable({ providedIn: 'root' })
export class CheckoutFacade {
  private readonly cart = inject(CartStore);
  private readonly catalog = inject(CatalogStore);
  private readonly checkout = inject(CheckoutService);

  private readonly _coupon = signal<string | null>(null);
  private readonly _breakdown = signal<DiscountBreakdown | null>(null);
  private readonly _loading = signal(false);
  private readonly _couponError = signal<string | null>(null);
  private readonly _confirming = signal(false);
  private readonly _confirmation = signal<OrderConfirmation | null>(null);
  private readonly _checkoutError = signal<string | null>(null);

  readonly coupon = this._coupon.asReadonly();
  readonly breakdown = this._breakdown.asReadonly();
  readonly loading = this._loading.asReadonly();
  readonly couponError = this._couponError.asReadonly();
  readonly confirming = this._confirming.asReadonly();
  readonly confirmation = this._confirmation.asReadonly();
  readonly checkoutError = this._checkoutError.asReadonly();
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
        switchMap((req) => (req ? this.checkout.quote(req) : of(null))),
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

  /** HU3 — confirms the order on the backend, then empties the cart and refreshes stock. */
  confirm(): void {
    const req = this.request();
    if (req === null || this._confirming()) {
      return;
    }
    this._confirming.set(true);
    this._checkoutError.set(null);
    this._confirmation.set(null);
    this.checkout.confirm(req).subscribe({
      next: (confirmation) => {
        this._confirming.set(false);
        this._confirmation.set(confirmation);
        this._breakdown.set(null);
        this._coupon.set(null);
        this.cart.clear();
        this.catalog.reload();
      },
      error: (error: HttpErrorResponse) => {
        this._confirming.set(false);
        this._checkoutError.set(this.messageFor(error));
      },
    });
  }

  dismissConfirmation(): void {
    this._confirmation.set(null);
  }

  private messageFor(error: HttpErrorResponse): string {
    const detail = (error.error as { message?: string } | null)?.message;
    if (error.status === 409) {
      return detail ?? 'No hay stock suficiente para completar la compra.';
    }
    return detail ?? 'No pudimos confirmar la compra. Intenta de nuevo.';
  }
}
