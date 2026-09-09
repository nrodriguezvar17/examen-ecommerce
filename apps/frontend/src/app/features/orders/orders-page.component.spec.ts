import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { Subject, of } from 'rxjs';
import { Order } from '../../core/models/order.model';
import { OrdersService } from '../../core/services/orders.service';
import { OrdersPageComponent } from './orders-page.component';

const order: Order = {
  radicado: 'ORD-1', createdAt: '2026-09-08T19:30:25Z', status: 'COMPRADO', couponCode: null,
  lines: [{ productId: 2, productName: 'Mouse', unitPrice: 25, category: 'Tecnología', quantity: 1, lineTotal: 25 }],
  discounts: [], originalTotal: 25, totalDiscount: 0, effectiveRate: 0, finalTotal: 25, capReached: false,
};

describe('OrdersPageComponent', () => {
  const setup = async (list: OrdersService['list']): Promise<void> => {
    await TestBed.configureTestingModule({
      imports: [OrdersPageComponent],
      providers: [provideRouter([]), { provide: OrdersService, useValue: { list } }],
    }).compileComponents();
  };

  it('shows a loading message until the list arrives', async () => {
    await setup(() => new Subject<readonly Order[]>());
    const fixture = TestBed.createComponent(OrdersPageComponent);
    fixture.detectChanges();
    expect((fixture.nativeElement as HTMLElement).textContent).toContain('Cargando tus compras');
  });

  it('shows the empty state with no orders', async () => {
    await setup(() => of<readonly Order[]>([]));
    const fixture = TestBed.createComponent(OrdersPageComponent);
    fixture.detectChanges();
    expect((fixture.nativeElement as HTMLElement).textContent).toContain(
      'Todavía no has confirmado ninguna compra',
    );
  });

  it('renders one card per order', async () => {
    await setup(() => of<readonly Order[]>([order, { ...order, radicado: 'ORD-2' }]));
    const fixture = TestBed.createComponent(OrdersPageComponent);
    fixture.detectChanges();
    expect((fixture.nativeElement as HTMLElement).querySelectorAll('app-order-card')).toHaveLength(2);
  });
});
