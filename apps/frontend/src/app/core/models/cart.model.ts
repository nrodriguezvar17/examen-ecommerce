/** Línea del carrito en el cliente. El precio se guarda para calcular el subtotal en vivo (HU1). */
export interface CartItem {
  readonly productId: number;
  readonly name: string;
  readonly unitPrice: number;
  readonly category: string;
  readonly quantity: number;
}
