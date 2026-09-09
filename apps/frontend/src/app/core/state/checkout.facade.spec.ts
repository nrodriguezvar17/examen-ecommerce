import { ApplicationRef } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { DiscountBreakdown } from '../models/discount-breakdown.model';
import { Product } from '../models/product.model';
import { CheckoutService } from '../services/checkout.service';
import { CartStore } from './cart.store';
import { CheckoutFacade } from './checkout.facade';

const laptop: Product = {
  id: 1, sku: 'TEC-LAP', name: 'Laptop', displayName: 'Laptop',
  description: '', unitPrice: 1200, category: 'Tecnología', stock: 8,
  ratingAverage: 0, reviewCount: 0,
};

function breakdown(lines: DiscountBreakdown['lines']): DiscountBreakdown {
  return {
    originalTotal: 1200, lines, totalDiscount: 120,
    effectiveRate: 0.1, finalTotal: 1080, capReached: false,
  };
}

const wait = (ms: number): Promise<void> => new Promise((resolve) => setTimeout(resolve, ms));

describe('CheckoutFacade (HU2)', () => {
  let quote: ReturnType<typeof vi.fn>;
  let appRef: ApplicationRef;

  beforeEach(() => {
    quote = vi.fn().mockReturnValue(of(breakdown([{ type: 'CATEGORY', amount: 120 }])));
    TestBed.configureTestingModule({
      providers: [{ provide: CheckoutService, useValue: { quote } }],
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

  it('flags an invalid coupon when the quote has no COUPON line', async () => {
    const facade = TestBed.inject(CheckoutFacade);
    TestBed.inject(CartStore).add(laptop);
    facade.applyCoupon('NOPE');
    appRef.tick();

    await wait(320);
    expect(facade.couponError()).toBe('Cupón no válido o expirado');
  });
});
