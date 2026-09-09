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

/** Persisted-order confirmation returned by `POST /api/checkout` (HU3). */
export interface OrderConfirmation {
  readonly radicado: string;
  readonly createdAt: string;
  readonly status: string;
  readonly breakdown: DiscountBreakdown;
}

/** Data-access service for the checkout endpoints: the quote (HU2) and the order (HU3). */
@Injectable({ providedIn: 'root' })
export class CheckoutService {
  private readonly http = inject(HttpClient);

  /** `POST /api/checkout/quote` — recalculates the breakdown, no side effects (HU2). */
  quote(request: QuoteRequest): Observable<DiscountBreakdown> {
    return this.http.post<DiscountBreakdown>('/api/checkout/quote', this.toBody(request));
  }

  /** `POST /api/checkout` — validates stock, persists the order and decrements stock (HU3). */
  confirm(request: QuoteRequest): Observable<OrderConfirmation> {
    return this.http.post<OrderConfirmation>('/api/checkout', this.toBody(request));
  }

  private toBody(request: QuoteRequest): { items: readonly QuoteItem[]; couponCode: string | null } {
    return { items: request.items, couponCode: request.couponCode };
  }
}
