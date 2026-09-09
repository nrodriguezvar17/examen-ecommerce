import { signal } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { Product } from '../../core/models/product.model';
import { CartStore } from '../../core/state/cart.store';
import { CheckoutFacade } from '../../core/state/checkout.facade';
import { CartPanelComponent } from './cart-panel.component';

const product: Product = {
  id: 2, sku: 'TEC-MOU-001', name: 'Mouse', displayName: 'Mouse inalámbrico',
  description: '', imageUrl: null, unitPrice: 25, category: 'Tecnología',
  stock: 40, ratingAverage: 0, reviewCount: 0,
};

function facadeStub(): Record<string, unknown> {
  return {
    loading: signal(false),
    confirming: signal(false),
    checkoutError: signal<string | null>(null),
    confirmation: signal<{ radicado: string; status: string; breakdown: { finalTotal: number } } | null>(null),
    coupon: signal<string | null>(null),
    couponError: signal<string | null>(null),
    breakdown: signal(null),
    capReached: signal(false),
    confirm: vi.fn(),
    dismissConfirmation: vi.fn(),
    applyCoupon: vi.fn(),
    clearCoupon: vi.fn(),
  };
}

describe('CartPanelComponent', () => {
  let facade: ReturnType<typeof facadeStub>;

  const create = (): { el: HTMLElement; instance: CartPanelComponent } => {
    const fixture = TestBed.createComponent(CartPanelComponent);
    fixture.detectChanges();
    return { el: fixture.nativeElement as HTMLElement, instance: fixture.componentInstance };
  };

  beforeEach(async () => {
    facade = facadeStub();
    await TestBed.configureTestingModule({
      imports: [CartPanelComponent],
      providers: [{ provide: CheckoutFacade, useValue: facade }],
    }).compileComponents();
  });

  it('shows the empty state when the cart has no items', () => {
    const { el } = create();
    expect(el.textContent).toContain('Agrega productos para empezar');
  });

  it('lists the cart lines, the subtotal and the confirm button', () => {
    TestBed.inject(CartStore).add(product, 2);
    const { el } = create();
    expect(el.querySelector('.name')?.textContent).toContain('Mouse inalámbrico');
    expect(el.querySelector('.subtotal')?.textContent).toContain('$50.00');
    expect(el.querySelector('.confirm')?.textContent).toContain('Confirmar compra');
  });

  it('delegates the confirm button to the facade', () => {
    TestBed.inject(CartStore).add(product);
    const { el } = create();
    el.querySelector<HTMLButtonElement>('.confirm')!.click();
    expect(facade['confirm']).toHaveBeenCalled();
  });

  it('renders the confirmation with its radicado', () => {
    (facade['confirmation'] as ReturnType<typeof signal>).set({
      radicado: 'ORD-20260908143025017', status: 'COMPRADO', breakdown: { finalTotal: 45 },
    });
    const { el } = create();
    expect(el.querySelector('.ok')?.textContent).toContain('ORD-20260908143025017');
  });

  it('shows the checkout error as an alert', () => {
    (facade['checkoutError'] as ReturnType<typeof signal>).set('No hay stock suficiente');
    TestBed.inject(CartStore).add(product);
    const { el } = create();
    expect(el.querySelector('[role="alert"]')?.textContent).toContain('No hay stock suficiente');
  });

  it('the +/- steppers and the trash button drive the CartStore', () => {
    const cart = TestBed.inject(CartStore);
    cart.add(product, 2);
    const { el } = create();
    const [minus, plus] = el.querySelectorAll<HTMLButtonElement>('.qty .step');

    plus.click();
    expect(cart.quantityOf(2)).toBe(3);
    minus.click();
    minus.click();
    expect(cart.quantityOf(2)).toBe(1);

    el.querySelector<HTMLButtonElement>('.remove')!.click();
    expect(cart.isEmpty()).toBe(true);
  });

  it('dismisses the confirmation via the facade', () => {
    (facade['confirmation'] as ReturnType<typeof signal>).set({
      radicado: 'ORD-1', status: 'COMPRADO', breakdown: { finalTotal: 10 },
    });
    const { el } = create();
    el.querySelector<HTMLButtonElement>('.ok button')!.click();
    expect(facade['dismissConfirmation']).toHaveBeenCalled();
  });
});
