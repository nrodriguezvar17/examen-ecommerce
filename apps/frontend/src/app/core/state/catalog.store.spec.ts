import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { Product } from '../models/product.model';
import { CatalogService } from '../services/catalog.service';
import { CatalogStore } from './catalog.store';

const mouse: Product = {
  id: 2, sku: 'TEC-MOU', name: 'Mouse', displayName: 'Mouse inalámbrico',
  description: '', unitPrice: 25, category: 'Tecnología', stock: 40,
  ratingAverage: 4, reviewCount: 3,
};

describe('CatalogStore', () => {
  let products: ReturnType<typeof vi.fn>;

  beforeEach(() => {
    products = vi.fn().mockReturnValue(of([mouse]));
    TestBed.configureTestingModule({
      providers: [{ provide: CatalogService, useValue: { products } }],
    });
  });

  it('starts empty and loading', () => {
    const store = TestBed.inject(CatalogStore);
    expect(store.products()).toEqual([]);
    expect(store.loading()).toBe(true);
  });

  it('reload() fills the list and clears loading', () => {
    const store = TestBed.inject(CatalogStore);
    store.reload();
    expect(products).toHaveBeenCalledTimes(1);
    expect(store.products()).toEqual([mouse]);
    expect(store.loading()).toBe(false);
  });

  it('reload() can run again to refresh stock after a purchase', () => {
    const store = TestBed.inject(CatalogStore);
    store.reload();
    products.mockReturnValueOnce(of([{ ...mouse, stock: 37 }]));
    store.reload();
    expect(products).toHaveBeenCalledTimes(2);
    expect(store.products()[0].stock).toBe(37);
  });
});
