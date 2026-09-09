import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Order } from '../../core/models/order.model';
import { OrderCardComponent } from './order-card.component';

const order: Order = {
  radicado: 'ORD-20260908143025017',
  createdAt: '2026-09-08T19:30:25Z',
  status: 'COMPRADO',
  couponCode: 'WELCOME2026',
  lines: [
    { productId: 2, productName: 'Mouse', unitPrice: 25, category: 'Tecnología', quantity: 2, lineTotal: 50 },
    { productId: 6, productName: 'Cuaderno', unitPrice: 5, category: 'Papelería', quantity: 4, lineTotal: 20 },
  ],
  discounts: [{ type: 'CATEGORY', rate: 0.1, amount: 5 }],
  originalTotal: 70,
  totalDiscount: 5,
  effectiveRate: 0.0714,
  finalTotal: 65,
  capReached: false,
};

describe('OrderCardComponent', () => {
  let fixture: ComponentFixture<OrderCardComponent>;

  const create = (value: Order): void => {
    fixture = TestBed.createComponent(OrderCardComponent);
    fixture.componentRef.setInput('order', value);
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({ imports: [OrderCardComponent] }).compileComponents();
  });

  it('shows the radicado, item count and final total, collapsed by default', () => {
    create(order);
    const el = fixture.nativeElement as HTMLElement;

    expect(el.querySelector('.radicado')?.textContent).toContain('ORD-20260908143025017');
    expect(el.querySelector('.meta')?.textContent).toContain('6 art.');
    expect(el.querySelector('.total')?.textContent).toContain('$65.00');
    expect(el.querySelector('.detail')).toBeNull();
  });

  it('expands to the line-by-line breakdown on click', () => {
    create(order);
    (fixture.nativeElement as HTMLElement).querySelector<HTMLButtonElement>('.summary')!.click();
    fixture.detectChanges();
    const text = (fixture.nativeElement as HTMLElement).textContent ?? '';

    expect(text).toContain('Mouse');
    expect(text).toContain('Descuento de categoría');
    expect(text).toContain('Ahorro total');
    expect(text).toContain('WELCOME2026');
  });

  it('shows the cap note when the order hit the 35% limit', () => {
    create({ ...order, capReached: true });
    (fixture.nativeElement as HTMLElement).querySelector<HTMLButtonElement>('.summary')!.click();
    fixture.detectChanges();

    expect((fixture.nativeElement as HTMLElement).textContent).toContain(
      'Se alcanzó el tope del 35 %',
    );
  });
});
