import { TestBed } from '@angular/core/testing';
import { Product } from '../models/product.model';
import { CartStore } from './cart.store';

const laptop: Product = {
  id: 1, sku: 'TEC-LAP-999', name: 'Laptop', displayName: 'Laptop',
  description: '', unitPrice: 800, category: 'Tecnología', stock: 5,
  ratingAverage: 0, reviewCount: 0,
};
const book: Product = {
  id: 2, sku: 'LIB-999', name: 'Libro', displayName: 'Libro',
  description: '', unitPrice: 30, category: 'Libros', stock: 10,
  ratingAverage: 0, reviewCount: 0,
};

describe('CartStore', () => {
  let store: CartStore;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    store = TestBed.inject(CartStore);
  });

  it('empieza vacío', () => {
    expect(store.isEmpty()).toBe(true);
    expect(store.subtotal()).toBe(0);
    expect(store.itemCount()).toBe(0);
  });

  it('acumula cantidad al agregar el mismo producto', () => {
    store.add(laptop);
    store.add(laptop, 2);
    expect(store.itemCount()).toBe(3);
    expect(store.subtotal()).toBe(2400);
  });

  it('calcula el subtotal con varios productos', () => {
    store.add(laptop);
    store.add(book, 2);
    expect(store.subtotal()).toBe(860);
  });

  it('setQuantity actualiza la cantidad de una línea existente', () => {
    store.add(laptop);
    store.setQuantity(1, 4);
    expect(store.itemCount()).toBe(4);
  });

  it('setQuantity(0) elimina la línea', () => {
    store.add(laptop);
    store.setQuantity(1, 0);
    expect(store.isEmpty()).toBe(true);
  });

  it('ignora cantidades no positivas al agregar', () => {
    store.add(laptop, 0);
    store.add(laptop, -3);
    expect(store.isEmpty()).toBe(true);
  });

  it('no deja agregar más unidades que el stock disponible', () => {
    store.add(laptop, 99);
    expect(store.quantityOf(1)).toBe(5);
    store.add(laptop, 4);
    expect(store.quantityOf(1)).toBe(5);
  });

  it('setQuantity nunca supera el stock de la línea', () => {
    store.add(laptop);
    store.setQuantity(1, 99);
    expect(store.quantityOf(1)).toBe(5);
  });

  it('no agrega un producto sin stock', () => {
    store.add({ ...book, stock: 0 });
    expect(store.isEmpty()).toBe(true);
  });

  it('quantityOf refleja lo que hay en el carrito', () => {
    store.add(book, 2);
    expect(store.quantityOf(2)).toBe(2);
    expect(store.quantityOf(1)).toBe(0);
  });

  it('remove de un id inexistente no cambia el estado', () => {
    store.add(laptop);
    store.remove(999);
    expect(store.itemCount()).toBe(1);
  });

  it('clear vacía el carrito', () => {
    store.add(laptop);
    store.add(book);
    store.clear();
    expect(store.isEmpty()).toBe(true);
  });
});
