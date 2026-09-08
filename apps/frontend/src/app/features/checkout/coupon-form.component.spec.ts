import { signal } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { CheckoutFacade } from '../../core/state/checkout.facade';
import { CouponFormComponent } from './coupon-form.component';

describe('CouponFormComponent (HU2)', () => {
  const coupon = signal<string | null>(null);
  const couponError = signal<string | null>(null);
  const facade = {
    applyCoupon: vi.fn(),
    clearCoupon: vi.fn(),
    coupon,
    couponError,
  };

  beforeEach(async () => {
    coupon.set(null);
    couponError.set(null);
    facade.applyCoupon.mockClear();
    facade.clearCoupon.mockClear();
    await TestBed.configureTestingModule({
      imports: [CouponFormComponent],
      providers: [{ provide: CheckoutFacade, useValue: facade }],
    }).compileComponents();
  });

  it('applies the typed code on submit', () => {
    const fixture = TestBed.createComponent(CouponFormComponent);
    fixture.detectChanges();

    const component = fixture.componentInstance as unknown as { code: string; apply(): void };
    component.code = 'welcome2026';
    component.apply();

    expect(facade.applyCoupon).toHaveBeenCalledWith('welcome2026');
  });

  it('clears the coupon on "quitar"', () => {
    coupon.set('WELCOME2026');
    const fixture = TestBed.createComponent(CouponFormComponent);
    fixture.detectChanges();

    (fixture.nativeElement as HTMLElement).querySelector<HTMLButtonElement>('.link')?.click();

    expect(facade.clearCoupon).toHaveBeenCalled();
  });

  it('shows the applied coupon and the coupon error', () => {
    coupon.set('WELCOME2026');
    couponError.set('Cupón no válido o expirado');

    const fixture = TestBed.createComponent(CouponFormComponent);
    fixture.detectChanges();

    const text = (fixture.nativeElement as HTMLElement).textContent ?? '';
    expect(text).toContain('WELCOME2026');
    expect(text).toContain('Cupón no válido o expirado');
  });
});
