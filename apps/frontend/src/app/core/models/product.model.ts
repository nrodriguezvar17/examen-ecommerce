/** Catalog product as `GET /api/products` and `GET /api/products/{id}` return it. */
export interface Product {
  readonly id: number;
  readonly sku: string;
  readonly name: string;
  readonly displayName: string;
  readonly description: string;
  /** Storefront image URL (may be null); shown in the catalog list and the detail page. */
  readonly imageUrl: string | null;
  readonly unitPrice: number;
  readonly category: string;
  readonly stock: number;
  readonly ratingAverage: number;
  readonly reviewCount: number;
}
