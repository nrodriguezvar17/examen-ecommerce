/** Catalog product. Mirrors the `GET /api/products` contract. */
export interface Product {
  readonly id: number;
  readonly name: string;
  readonly unitPrice: number;
  readonly category: string;
  readonly stock: number;
}
