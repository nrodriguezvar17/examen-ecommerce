import { DiscountType } from './discount-breakdown.model';

/** One purchased line (a price/name snapshot taken at checkout time). */
export interface OrderLine {
  readonly productId: number;
  readonly productName: string;
  readonly unitPrice: number;
  readonly category: string;
  readonly quantity: number;
  readonly lineTotal: number;
}

/** Raw discount applied by one rule (before the 35% cap). */
export interface OrderDiscount {
  readonly type: DiscountType;
  readonly rate: number;
  readonly amount: number;
}

/** A confirmed order, as returned by `GET /api/orders` and `GET /api/orders/{radicado}`. */
export interface Order {
  readonly radicado: string;
  readonly createdAt: string;
  readonly status: string;
  readonly couponCode: string | null;
  readonly lines: readonly OrderLine[];
  readonly discounts: readonly OrderDiscount[];
  readonly originalTotal: number;
  readonly totalDiscount: number;
  readonly effectiveRate: number;
  readonly finalTotal: number;
  readonly capReached: boolean;
}
