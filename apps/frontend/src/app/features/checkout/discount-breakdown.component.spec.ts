import { signal } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { DiscountBreakdown } from '../../core/models/discount-breakdown.model';
import { CheckoutFacade } from '../../core/state/checkout.facade';
import { DiscountBreakdownComponent } from './discount-breakdown.component';

describe('DiscountBreakdownComponent (HU2)', () => {
  const breakdown = signal<DiscountBreakdown | null>(null);

  beforeEach(async () => {
    breakdown.set(null);
    await TestBed.configureTestingModule({
      imports: [DiscountBreakdownComponent],
      providers: [{ provide: CheckoutFacade, useValue: { breakdown } }],
    }).compileComponents();
  });

  it('renders nothing while there is no quote', () => {
    const fixture = TestBed.createComponent(DiscountBreakdownComponent);
    fixture.detectChanges();
    expect((fixture.nativeElement as HTMLElement).textContent?.trim()).toBe('');
  });

  it('renders each discount line, the effective rate, the savings and the final total', () => {
    breakdown.set({
      originalTotal: 870,
      lines: [
        { type: 'CATEGORY', amount: 85 },
        { type: 'VOLUME', amount: 39.25 },
        { type: 'COUPON', amount: 111.86 },
      ],
      totalDiscount: 236.11,
      effectiveRate: 0.2714,
      finalTotal: 633.89,
      capReached: false,
    });

    const fixture = TestBed.createComponent(DiscountBreakdownComponent);
    fixture.detectChanges();
    const text = (fixture.nativeElement as HTMLElement).textContent ?? '';

    expect(text).toContain('Descuento de categoría');
    expect(text).toContain('Descuento por volumen');
    expect(text).toContain('Descuento por cupón');
    expect(text).toContain('27.14%');
    expect(text).toContain('236.11');
    expect(text).toContain('633.89');
  });

  it('explains the 35% cap when it was reached (HU4)', () => {
    breakdown.set({
      originalTotal: 125,
      lines: [{ type: 'COUPON', amount: 53.44 }],
      totalDiscount: 43.75,
      effectiveRate: 0.35,
      finalTotal: 81.25,
      capReached: true,
    });
    const fixture = TestBed.createComponent(DiscountBreakdownComponent);
    fixture.detectChanges();
    expect((fixture.nativeElement as HTMLElement).textContent).toContain(
      'el ahorro se limita a ese tope',
    );
  });

  it('shows a note when there are no applicable discounts', () => {
    breakdown.set({
      originalTotal: 10,
      lines: [],
      totalDiscount: 0,
      effectiveRate: 0,
      finalTotal: 10,
      capReached: false,
    });
    const fixture = TestBed.createComponent(DiscountBreakdownComponent);
    fixture.detectChanges();
    expect((fixture.nativeElement as HTMLElement).textContent).toContain('Sin descuentos aplicables');
  });
});
