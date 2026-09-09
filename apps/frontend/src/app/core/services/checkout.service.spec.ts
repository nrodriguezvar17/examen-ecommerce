import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { CheckoutService } from './checkout.service';

describe('CheckoutService', () => {
  let service: CheckoutService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(CheckoutService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('POSTs the cart and coupon to /api/checkout/quote', () => {
    let received: unknown;
    service
      .quote({ items: [{ productId: 2, quantity: 2 }], couponCode: 'WELCOME2026' })
      .subscribe((value) => (received = value));

    const req = httpMock.expectOne('/api/checkout/quote');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({
      items: [{ productId: 2, quantity: 2 }],
      couponCode: 'WELCOME2026',
    });
    req.flush({
      originalTotal: 70,
      lines: [],
      totalDiscount: 0,
      effectiveRate: 0,
      finalTotal: 70,
      capReached: false,
    });
    expect(received).toBeTruthy();
  });

  it('POSTs the order to /api/checkout and returns the confirmation (HU3)', () => {
    let received: { radicado?: string } | undefined;
    service
      .confirm({ items: [{ productId: 2, quantity: 3 }], couponCode: null })
      .subscribe((value) => (received = value));

    const req = httpMock.expectOne('/api/checkout');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({
      items: [{ productId: 2, quantity: 3 }],
      couponCode: null,
    });
    req.flush({
      radicado: 'ORD-20260908143025017',
      createdAt: '2026-09-08T19:30:25Z',
      status: 'COMPRADO',
      breakdown: {
        originalTotal: 75,
        lines: [{ type: 'CATEGORY', amount: 7.5 }],
        totalDiscount: 7.5,
        effectiveRate: 0.1,
        finalTotal: 67.5,
        capReached: false,
      },
    });
    expect(received?.radicado).toBe('ORD-20260908143025017');
  });
});
