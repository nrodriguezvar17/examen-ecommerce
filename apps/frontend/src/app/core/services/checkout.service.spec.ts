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
});
