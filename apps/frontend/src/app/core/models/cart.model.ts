/** Cart line on the client. The price feeds the live subtotal; `stock` caps the quantity (HU1). */
export interface CartItem {
  readonly productId: number;
  readonly name: string;
  readonly unitPrice: number;
  readonly category: string;
  readonly quantity: number;
  /** Units available in the catalog — the quantity can never exceed this. */
  readonly stock: number;
}
