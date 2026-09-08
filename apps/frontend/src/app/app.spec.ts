import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { App } from './app';
import { CatalogApi } from './core/api/catalog-api';
import { CheckoutApi } from './core/api/checkout-api';
import { Product } from './core/models/product.model';

const emptyCatalog: Pick<CatalogApi, 'products'> = {
  products: () => of<readonly Product[]>([]),
};

describe('App', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [
        { provide: CatalogApi, useValue: emptyCatalog },
        { provide: CheckoutApi, useValue: { quote: () => of(null) } },
      ],
    }).compileComponents();
  });

  it('creates the app', () => {
    const fixture = TestBed.createComponent(App);
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('renders the Davitienda topbar', async () => {
    const fixture = TestBed.createComponent(App);
    await fixture.whenStable();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.brand')?.textContent).toContain('Davi');
  });
});
