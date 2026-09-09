import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { DiscountBreakdown } from '../models/discount-breakdown.model';

export interface QuoteItem {
  readonly productId: number;
  readonly quantity: number;
}

export interface QuoteRequest {
  readonly items: readonly QuoteItem[];
  readonly couponCode: string | null;
}

/** Data-access service for the discount quote (HU2): `POST /api/checkout/quote`. */
@Injectable({ providedIn: 'root' })
export class CheckoutService {
  private readonly http = inject(HttpClient);

  quote(request: QuoteRequest): Observable<DiscountBreakdown> {
    return this.http.post<DiscountBreakdown>('/api/checkout/quote', {
      items: request.items,
      couponCode: request.couponCode,
    });
  }
}
