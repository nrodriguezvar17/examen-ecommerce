import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { OrdersService } from './orders.service';

describe('OrdersService', () => {
  let service: OrdersService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(OrdersService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('GETs /api/orders for the list', () => {
    let received: readonly unknown[] | undefined;
    service.list().subscribe((orders) => (received = orders));

    const req = httpMock.expectOne('/api/orders');
    expect(req.request.method).toBe('GET');
    req.flush([{ radicado: 'ORD-1' }, { radicado: 'ORD-2' }]);
    expect(received).toHaveLength(2);
  });

  it('GETs /api/orders/:radicado for one order', () => {
    let received: { radicado?: string } | undefined;
    service.get('ORD-20260908143025017').subscribe((order) => (received = order));

    const req = httpMock.expectOne('/api/orders/ORD-20260908143025017');
    expect(req.request.method).toBe('GET');
    req.flush({ radicado: 'ORD-20260908143025017' });
    expect(received?.radicado).toBe('ORD-20260908143025017');
  });
});
