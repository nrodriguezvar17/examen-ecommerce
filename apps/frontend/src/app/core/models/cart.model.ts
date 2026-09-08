/** Cart line on the client. The price is kept to compute the live subtotal (HU1). */
export interface CartItem {
  readonly productId: number;
  readonly name: string;
  readonly unitPrice: number;
  readonly category: string;
  readonly quantity: number;
}
