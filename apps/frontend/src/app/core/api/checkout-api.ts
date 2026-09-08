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

/** Port for the discount quote. Implementation: {@link HttpCheckoutApi} (`POST /api/checkout/quote`). */
export abstract class CheckoutApi {
  abstract quote(request: QuoteRequest): Observable<DiscountBreakdown>;
}
