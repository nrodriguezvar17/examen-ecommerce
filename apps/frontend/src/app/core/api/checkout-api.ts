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

/**
 * Port for the discount quote. Implementations: `HttpCheckoutApi` (Fase 1, calls
 * `POST /api/checkout/quote`) and `MockCheckoutApi` (prototype, client-side cascade).
 */
export abstract class CheckoutApi {
  abstract quote(request: QuoteRequest): Observable<DiscountBreakdown>;
}
