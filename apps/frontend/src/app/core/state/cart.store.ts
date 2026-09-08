import { Injectable, computed, signal } from '@angular/core';
import { CartItem } from '../models/cart.model';
import { Product } from '../models/product.model';

/**
 * Estado del carrito. Patrón <b>Observer</b>: el estado vive en signals y los valores
 * derivados (subtotal, número de ítems) son `computed`. Los componentes reaccionan a los
 * cambios sin suscripciones manuales — base de la actualización en vivo de HU1.
 */
@Injectable({ providedIn: 'root' })
export class CartStore {
  private readonly _items = signal<readonly CartItem[]>([]);

  readonly items = this._items.asReadonly();
  readonly itemCount = computed((): number =>
    this._items().reduce((total, item) => total + item.quantity, 0),
  );
  readonly subtotal = computed((): number =>
    this._items().reduce((total, item) => total + item.unitPrice * item.quantity, 0),
  );
  readonly isEmpty = computed((): boolean => this._items().length === 0);

  add(product: Product, quantity = 1): void {
    if (quantity <= 0) {
      return;
    }
    this._items.update((items) => {
      const existing = items.find((item) => item.productId === product.id);
      if (existing) {
        return items.map((item) =>
          item.productId === product.id
            ? { ...item, quantity: item.quantity + quantity }
            : item,
        );
      }
      const line: CartItem = {
        productId: product.id,
        name: product.name,
        unitPrice: product.unitPrice,
        category: product.category,
        quantity,
      };
      return [...items, line];
    });
  }

  setQuantity(productId: number, quantity: number): void {
    if (quantity <= 0) {
      this.remove(productId);
      return;
    }
    this._items.update((items) =>
      items.map((item) => (item.productId === productId ? { ...item, quantity } : item)),
    );
  }

  remove(productId: number): void {
    this._items.update((items) => items.filter((item) => item.productId !== productId));
  }

  clear(): void {
    this._items.set([]);
  }
}
