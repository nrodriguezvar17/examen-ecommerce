/** Desglose de descuentos devuelto por `POST /api/checkout/quote` y `POST /api/checkout`. */
export type DiscountType = 'CATEGORY' | 'VOLUME' | 'COUPON';

export interface DiscountLine {
  readonly type: DiscountType;
  readonly amount: number;
}

export interface DiscountBreakdown {
  readonly originalTotal: number;
  readonly lines: readonly DiscountLine[];
  readonly totalDiscount: number;
  /** Porcentaje efectivo sobre el total original (0..1). */
  readonly effectiveRate: number;
  readonly finalTotal: number;
  /** true → se alcanzó el tope del 35 %; dispara la alerta persistente (HU4). */
  readonly capReached: boolean;
}
