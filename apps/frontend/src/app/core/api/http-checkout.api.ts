import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { DiscountBreakdown } from '../models/discount-breakdown.model';
import { CheckoutApi, QuoteRequest } from './checkout-api';

/** Real implementation of {@link CheckoutApi}: calls `POST /api/checkout/quote`. */
@Injectable()
export class HttpCheckoutApi extends CheckoutApi {
  private readonly http = inject(HttpClient);

  quote(request: QuoteRequest): Observable<DiscountBreakdown> {
    return this.http.post<DiscountBreakdown>('/api/checkout/quote', {
      items: request.items,
      couponCode: request.couponCode,
    });
  }
}
