import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { Product } from '../../core/models/product.model';
import { CartStore } from '../../core/state/cart.store';
import { ProductCardComponent } from './product-card.component';

const base: Product = {
  id: 2, sku: 'TEC-MOU-001', name: 'Mouse', displayName: 'Mouse inalámbrico',
  description: 'Óptico', imageUrl: 'https://loremflickr.com/600/400/computer,mouse?lock=1',
  unitPrice: 25, category: 'Tecnología', stock: 3, ratingAverage: 4, reviewCount: 2,
};

describe('ProductCardComponent', () => {
  let fixture: ComponentFixture<ProductCardComponent>;

  const render = (product: Product): HTMLElement => {
    fixture = TestBed.createComponent(ProductCardComponent);
    fixture.componentRef.setInput('product', product);
    fixture.detectChanges();
    return fixture.nativeElement as HTMLElement;
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProductCardComponent],
      providers: [provideRouter([])],
    }).compileComponents();
  });

  it('shows name, price, category, image and a link to the detail route', () => {
    const el = render(base);
    expect(el.querySelector('h3')?.textContent).toContain('Mouse inalámbrico');
    expect(el.textContent).toContain('$25.00');
    expect(el.querySelector('.dv-badge')?.textContent).toContain('Tecnología');
    expect(el.querySelector('img')?.getAttribute('src')).toContain('loremflickr');
    expect(el.querySelector('a.thumb')?.getAttribute('href')).toBe('/producto/2');
  });

  it('falls back to the category initial when there is no image', () => {
    const el = render({ ...base, imageUrl: null });
    expect(el.querySelector('img')).toBeNull();
    expect(el.querySelector('a.thumb')?.textContent?.trim()).toBe('T');
  });

  it('emits add() with the product when "Agregar" is clicked', () => {
    const el = render(base);
    let emitted: Product | undefined;
    fixture.componentInstance.add.subscribe((p) => (emitted = p));
    el.querySelector<HTMLButtonElement>('.dv-btn--primary')!.click();
    expect(emitted?.id).toBe(2);
  });

  it('disables the button with "Sin stock" when stock is 0', () => {
    const el = render({ ...base, stock: 0 });
    const btn = el.querySelector<HTMLButtonElement>('.foot button')!;
    expect(btn.disabled).toBe(true);
    expect(btn.textContent).toContain('Sin stock');
  });

  it('disables with "Sin más stock" when the cart already holds every unit', () => {
    TestBed.inject(CartStore).add(base, 3);
    const el = render(base);
    const btn = el.querySelector<HTMLButtonElement>('.foot button')!;
    expect(btn.disabled).toBe(true);
    expect(btn.textContent).toContain('Sin más stock');
  });
});
