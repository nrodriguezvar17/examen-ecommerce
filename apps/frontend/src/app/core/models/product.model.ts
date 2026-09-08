/** Catalog product. Mirrors the `GET /api/products` contract. */
export interface Product {
  readonly id: number;
  readonly sku: string;
  /** Canonical name (internal). */
  readonly name: string;
  /** Storefront name shown to the customer. */
  readonly displayName: string;
  readonly description: string;
  readonly unitPrice: number;
  readonly category: string;
  readonly stock: number;
  readonly ratingAverage: number;
  readonly reviewCount: number;
}
