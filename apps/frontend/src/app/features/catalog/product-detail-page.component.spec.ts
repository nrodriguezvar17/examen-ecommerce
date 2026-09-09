import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { Subject, of, throwError } from 'rxjs';
import { ProductDetail } from '../../core/models/product-detail.model';
import { CatalogService } from '../../core/services/catalog.service';
import { CartStore } from '../../core/state/cart.store';
import { ProductDetailPageComponent } from './product-detail-page.component';

const detail: ProductDetail = {
  id: 2, sku: 'TEC-MOU-001', name: 'Mouse', displayName: 'Mouse inalámbrico silencioso',
  description: 'Mouse óptico.', imageUrl: 'https://loremflickr.com/600/400/computer,mouse?lock=1',
  unitPrice: 25, category: 'Tecnología', stock: 40, ratingAverage: 5, reviewCount: 1,
  brand: 'NovaTech',
  reviews: [
    { id: 9, rating: 5, title: 'Silencioso', comment: 'Casi no se oye.', reviewerName: 'Sofía R.', createdAt: '2026-01-10T12:00:00Z' },
  ],
};

describe('ProductDetailPageComponent', () => {
  const create = async (
    detailFn: CatalogService['detail'],
  ): Promise<ComponentFixture<ProductDetailPageComponent>> => {
    await TestBed.configureTestingModule({
      imports: [ProductDetailPageComponent],
      providers: [provideRouter([]), { provide: CatalogService, useValue: { detail: detailFn } }],
    }).compileComponents();
    const fixture = TestBed.createComponent(ProductDetailPageComponent);
    fixture.componentRef.setInput('id', '2');
    fixture.detectChanges();
    return fixture;
  };

  const textOf = (fixture: ComponentFixture<ProductDetailPageComponent>): string =>
    (fixture.nativeElement as HTMLElement).textContent ?? '';

  it('shows a loading state until the product arrives', async () => {
    const fixture = await create(() => new Subject<ProductDetail>());
    expect(textOf(fixture)).toContain('Cargando el producto');
  });

  it('renders the product, its brand and its reviews', async () => {
    const fixture = await create(() => of(detail));
    const text = textOf(fixture);
    expect(text).toContain('Mouse inalámbrico silencioso');
    expect(text).toContain('Marca: NovaTech');
    expect(text).toContain('Sofía R.');
    expect(text).toContain('$25.00');
  });

  it('shows a not-found state when the detail request fails', async () => {
    const fixture = await create(() => throwError(() => new Error('404')));
    expect(textOf(fixture)).toContain('No encontramos este producto');
  });

  it('adds the product to the cart, capped at the stock', async () => {
    const fixture = await create(() => of({ ...detail, stock: 1 }));
    fixture.componentInstance['addToCart']();
    fixture.componentInstance['addToCart']();
    expect(TestBed.inject(CartStore).quantityOf(2)).toBe(1);
  });
});
