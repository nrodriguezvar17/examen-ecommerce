import { HttpErrorResponse } from '@angular/common/http';
import { ApplicationRef } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { DiscountBreakdown } from '../models/discount-breakdown.model';
import { Product } from '../models/product.model';
import { CheckoutService } from '../services/checkout.service';
import { CartStore } from './cart.store';
import { CatalogStore } from './catalog.store';
import { CheckoutFacade } from './checkout.facade';

const laptop: Product = {
  id: 1, sku: 'TEC-LAP', name: 'Laptop', displayName: 'Laptop',
  description: '', imageUrl: null, unitPrice: 1200, category: 'Tecnología', stock: 8,
  ratingAverage: 0, reviewCount: 0,
};

function breakdown(lines: DiscountBreakdown['lines']): DiscountBreakdown {
  return {
    originalTotal: 1200, lines, totalDiscount: 120,
    effectiveRate: 0.1, finalTotal: 1080, capReached: false,
  };
}

const wait = (ms: number): Promise<void> => new Promise((resolve) => setTimeout(resolve, ms));

describe('CheckoutFacade', () => {
  let quote: ReturnType<typeof vi.fn>;
  let confirm: ReturnType<typeof vi.fn>;
  let reload: ReturnType<typeof vi.fn>;
  let appRef: ApplicationRef;

  beforeEach(() => {
    quote = vi.fn().mockReturnValue(of(breakdown([{ type: 'CATEGORY', amount: 120 }])));
    confirm = vi.fn();
    reload = vi.fn();
    TestBed.configureTestingModule({
      providers: [
        { provide: CheckoutService, useValue: { quote, confirm } },
        { provide: CatalogStore, useValue: { reload } },
      ],
    });
    appRef = TestBed.inject(ApplicationRef);
  });

  it('does not quote while the cart is empty', async () => {
    TestBed.inject(CheckoutFacade);
    appRef.tick();
    await wait(300);
    expect(quote).not.toHaveBeenCalled();
  });

  it('debounces cart changes and exposes the breakdown', async () => {
    const facade = TestBed.inject(CheckoutFacade);
    TestBed.inject(CartStore).add(laptop);
    appRef.tick();

    await wait(120);
    expect(quote).not.toHaveBeenCalled();

    await wait(200);
    appRef.tick();
    expect(quote).toHaveBeenCalledTimes(1);
    expect(facade.breakdown()?.finalTotal).toBe(1080);
    expect(facade.capReached()).toBe(false);
  });

  it('exposes capReached when the quote hits the 35% cap (HU4)', async () => {
    quote.mockReturnValue(
      of({
        originalTotal: 125, lines: [{ type: 'COUPON', amount: 53.44 }],
        totalDiscount: 43.75, effectiveRate: 0.35, finalTotal: 81.25, capReached: true,
      }),
    );
    const facade = TestBed.inject(CheckoutFacade);
    TestBed.inject(CartStore).add(laptop);
    appRef.tick();

    await wait(320);
    expect(facade.capReached()).toBe(true);
    expect(facade.breakdown()?.finalTotal).toBe(81.25);
  });

  it('flags an invalid coupon when the quote has no COUPON line', async () => {
    const facade = TestBed.inject(CheckoutFacade);
    TestBed.inject(CartStore).add(laptop);
    facade.applyCoupon('NOPE');
    appRef.tick();

    await wait(320);
    expect(facade.couponError()).toBe('Cupón no válido o expirado');
  });

  it('confirm() persists the order, empties the cart and refreshes the catalog (HU3)', () => {
    confirm.mockReturnValue(
      of({
        radicado: 'ORD-20260908143025017',
        createdAt: '2026-09-08T19:30:25Z',
        status: 'COMPRADO',
        breakdown: breakdown([{ type: 'CATEGORY', amount: 120 }]),
      }),
    );
    const facade = TestBed.inject(CheckoutFacade);
    const cart = TestBed.inject(CartStore);
    cart.add(laptop);

    facade.confirm();

    expect(confirm).toHaveBeenCalledWith({
      items: [{ productId: 1, quantity: 1 }],
      couponCode: null,
    });
    expect(facade.confirmation()?.radicado).toBe('ORD-20260908143025017');
    expect(facade.confirming()).toBe(false);
    expect(cart.isEmpty()).toBe(true);
    expect(reload).toHaveBeenCalledTimes(1);
  });

  it('confirm() surfaces the backend message on a 409 and keeps the cart', () => {
    confirm.mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            status: 409,
            error: { status: 409, message: "Stock insuficiente para 'Laptop': se pidieron 1 y hay 0" },
          }),
      ),
    );
    const facade = TestBed.inject(CheckoutFacade);
    const cart = TestBed.inject(CartStore);
    cart.add(laptop);

    facade.confirm();

    expect(facade.checkoutError()).toBe("Stock insuficiente para 'Laptop': se pidieron 1 y hay 0");
    expect(facade.confirmation()).toBeNull();
    expect(cart.isEmpty()).toBe(false);
    expect(reload).not.toHaveBeenCalled();
  });

  it('confirm() is a no-op while the cart is empty', () => {
    const facade = TestBed.inject(CheckoutFacade);
    facade.confirm();
    expect(confirm).not.toHaveBeenCalled();
  });
});
