import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { App } from './app';
import { Product } from './core/models/product.model';
import { CatalogService } from './core/services/catalog.service';
import { CheckoutService } from './core/services/checkout.service';

describe('App', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: CatalogService, useValue: { products: () => of<readonly Product[]>([]) } },
        { provide: CheckoutService, useValue: { quote: () => of(null) } },
      ],
    }).compileComponents();
  });

  it('creates the app', () => {
    const fixture = TestBed.createComponent(App);
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('renders the header logo and the footer', async () => {
    const fixture = TestBed.createComponent(App);
    await fixture.whenStable();
    const compiled = fixture.nativeElement as HTMLElement;

    expect(compiled.querySelector('.topbar img')?.getAttribute('alt')).toBe('Davitienda');
    expect(compiled.querySelector('app-site-footer')?.textContent).toContain('Davitienda');
  });
});
