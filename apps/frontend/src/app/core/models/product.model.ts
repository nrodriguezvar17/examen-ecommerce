/** Producto del catálogo. Refleja el contrato de `GET /api/products`. */
export interface Product {
  readonly id: number;
  readonly name: string;
  readonly unitPrice: number;
  readonly category: string;
  readonly stock: number;
}
