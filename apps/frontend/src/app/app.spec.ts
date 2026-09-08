import { TestBed } from '@angular/core/testing';
import { App } from './app';
import { CatalogApi } from './core/api/catalog-api';
import { CheckoutApi } from './core/api/checkout-api';
import { MockCatalogApi } from './core/api/mock-catalog.api';
import { MockCheckoutApi } from './core/api/mock-checkout.api';

describe('App', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [
        { provide: CatalogApi, useClass: MockCatalogApi },
        { provide: CheckoutApi, useClass: MockCheckoutApi },
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
