import { Injectable, inject } from '@angular/core';
import { Observable, delay, map } from 'rxjs';
import { DiscountBreakdown, DiscountLine } from '../models/discount-breakdown.model';
import { Product } from '../models/product.model';
import { CatalogApi } from './catalog-api';
import { CheckoutApi, QuoteRequest } from './checkout-api';

/**
 * PROTOTYPE ONLY. Replicates the backend cumulative-discount cascade client-side so the UI
 * (HU2, HU4) works with no server. In production the source of truth is the backend
 * (`POST /api/checkout/quote`) and this class is replaced by `HttpCheckoutApi` in Fase 1 —
 * see docs/ia.md (rejected suggestion #3). Do not treat as canonical.
 */
@Injectable()
export class MockCheckoutApi extends CheckoutApi {
  private readonly catalog = inject(CatalogApi);

  private readonly categoryRates: Readonly<Record<string, number>> = { Tecnología: 0.1 };
  private readonly volumeThreshold = 100;
  private readonly volumeRate = 0.05;
  private readonly capRate = 0.35;
  private readonly coupons: Readonly<Record<string, { rate: number; active: boolean }>> = {
    WELCOME2026: { rate: 0.15, active: true },
    BLACKFRIDAY2025: { rate: 0.25, active: false },
  };

  quote(request: QuoteRequest): Observable<DiscountBreakdown> {
    return this.catalog.products().pipe(
      map((products) => this.compute(request, products)),
      delay(120),
    );
  }

  private round2(n: number): number {
    return Math.round((n + Number.EPSILON) * 100) / 100;
  }

  private round4(n: number): number {
    return Math.round((n + Number.EPSILON) * 10000) / 10000;
  }

  private compute(request: QuoteRequest, products: readonly Product[]): DiscountBreakdown {
    const byId = new Map(products.map((p) => [p.id, p]));
    const resolved = request.items
      .map((item) => ({ product: byId.get(item.productId), quantity: item.quantity }))
      .filter((line): line is { product: Product; quantity: number } =>
        line.product !== undefined && line.quantity > 0,
      );

    const originalTotal = this.round2(
      resolved.reduce((sum, l) => sum + l.product.unitPrice * l.quantity, 0),
    );

    // Rule 1 — category discount (only the discounted category's subtotal).
    const categorySubtotal = this.round2(
      resolved
        .filter((l) => (this.categoryRates[l.product.category] ?? 0) > 0)
        .reduce((sum, l) => sum + l.product.unitPrice * l.quantity, 0),
    );
    const d1 = this.round2(categorySubtotal * (this.categoryRates['Tecnología'] ?? 0));
    const t1 = this.round2(originalTotal - d1);

    // Rule 2 — volume discount (strictly greater than the threshold).
    const d2 = t1 > this.volumeThreshold ? this.round2(t1 * this.volumeRate) : 0;
    const t2 = this.round2(t1 - d2);

    // Rule 3 — coupon discount (active coupon only).
    const coupon = request.couponCode
      ? this.coupons[request.couponCode.trim().toUpperCase()]
      : undefined;
    const d3 = coupon && coupon.active ? this.round2(t2 * coupon.rate) : 0;
    const t3 = this.round2(t2 - d3);

    const lines: DiscountLine[] = [];
    if (d1 > 0) {
      lines.push({ type: 'CATEGORY', amount: d1 });
    }
    if (d2 > 0) {
      lines.push({ type: 'VOLUME', amount: d2 });
    }
    if (d3 > 0) {
      lines.push({ type: 'COUPON', amount: d3 });
    }

    // Rule 4 — absolute 35% cap.
    const rawDiscount = this.round2(originalTotal - t3);
    const rawRate = originalTotal > 0 ? this.round4(rawDiscount / originalTotal) : 0;

    if (rawRate > this.capRate) {
      const totalDiscount = this.round2(originalTotal * this.capRate);
      return {
        originalTotal,
        lines,
        totalDiscount,
        effectiveRate: this.capRate,
        finalTotal: this.round2(originalTotal - totalDiscount),
        capReached: true,
      };
    }

    return {
      originalTotal,
      lines,
      totalDiscount: rawDiscount,
      effectiveRate: rawRate,
      finalTotal: t3,
      capReached: false,
    };
  }
}
