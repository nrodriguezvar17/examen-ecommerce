import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { CheckoutApi } from './checkout-api';
import { HttpCheckoutApi } from './http-checkout.api';

describe('HttpCheckoutApi', () => {
  let api: CheckoutApi;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: CheckoutApi, useClass: HttpCheckoutApi },
      ],
    });
    api = TestBed.inject(CheckoutApi);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('POSTs the cart and coupon to /api/checkout/quote', () => {
    let result: unknown;
    api
      .quote({ items: [{ productId: 2, quantity: 2 }], couponCode: 'WELCOME2026' })
      .subscribe((value) => (result = value));

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
    expect(result).toBeTruthy();
  });
});
