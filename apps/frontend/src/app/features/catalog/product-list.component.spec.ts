import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { Subject, of } from 'rxjs';
import { Product } from '../../core/models/product.model';
import { CatalogService } from '../../core/services/catalog.service';
import { ProductListComponent } from './product-list.component';

const product: Product = {
  id: 1, sku: 'TEC-LAP-014', name: 'Laptop', displayName: 'Laptop Pro 14"',
  description: '', imageUrl: null, unitPrice: 1200, category: 'Tecnología',
  stock: 8, ratingAverage: 4.5, reviewCount: 2,
};

describe('ProductListComponent', () => {
  const setup = async (products: CatalogService['products']): Promise<HTMLElement> => {
    await TestBed.configureTestingModule({
      imports: [ProductListComponent],
      providers: [provideRouter([]), { provide: CatalogService, useValue: { products } }],
    }).compileComponents();
    const fixture = TestBed.createComponent(ProductListComponent);
    fixture.detectChanges();
    return fixture.nativeElement as HTMLElement;
  };

  it('shows a loading message until the catalog arrives', async () => {
    const el = await setup(() => new Subject<readonly Product[]>());
    expect(el.textContent).toContain('Cargando productos');
  });

  it('renders one card per product once loaded', async () => {
    const productsFn = vi.fn().mockReturnValue(of<readonly Product[]>([product, { ...product, id: 2 }]));
    const el = await setup(productsFn);
    expect(el.querySelectorAll('app-product-card')).toHaveLength(2);
    expect(productsFn).toHaveBeenCalledTimes(1); // reload() on construction
  });
});
