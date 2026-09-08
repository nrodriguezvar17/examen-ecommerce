/** Discount breakdown returned by `POST /api/checkout/quote` and `POST /api/checkout`. */
export type DiscountType = 'CATEGORY' | 'VOLUME' | 'COUPON';

export interface DiscountLine {
  readonly type: DiscountType;
  readonly amount: number;
}

export interface DiscountBreakdown {
  readonly originalTotal: number;
  readonly lines: readonly DiscountLine[];
  readonly totalDiscount: number;
  /** Effective rate over the original total (0..1). */
  readonly effectiveRate: number;
  readonly finalTotal: number;
  /** true -> the 35% cap was reached; triggers the persistent alert (HU4). */
  readonly capReached: boolean;
}
