import { DiscountType } from '../core/models/discount-breakdown.model';

/** Human labels for each discount rule. Shared by the quote breakdown and the order detail. */
export const DISCOUNT_LABELS: Readonly<Record<DiscountType, string>> = {
  CATEGORY: 'Descuento de categoría',
  VOLUME: 'Descuento por volumen',
  COUPON: 'Descuento por cupón',
};
