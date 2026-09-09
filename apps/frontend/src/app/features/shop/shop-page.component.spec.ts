import { signal } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { Product } from '../../core/models/product.model';
import { CatalogService } from '../../core/services/catalog.service';
import { CheckoutFacade } from '../../core/state/checkout.facade';
import { ShopPageComponent } from './shop-page.component';

const checkoutFacadeStub = {
  loading: signal(false),
  confirming: signal(false),
  checkoutError: signal(null),
  confirmation: signal(null),
  coupon: signal(null),
  couponError: signal(null),
  breakdown: signal(null),
  capReached: signal(false),
  confirm: vi.fn(),
  dismissConfirmation: vi.fn(),
  applyCoupon: vi.fn(),
  clearCoupon: vi.fn(),
};

describe('ShopPageComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ShopPageComponent],
      providers: [
        provideRouter([]),
        { provide: CatalogService, useValue: { products: () => of<readonly Product[]>([]) } },
        { provide: CheckoutFacade, useValue: checkoutFacadeStub },
      ],
    }).compileComponents();
  });

  it('lays out the catalog and the cart panel', () => {
    const fixture = TestBed.createComponent(ShopPageComponent);
    fixture.detectChanges();
    const el = fixture.nativeElement as HTMLElement;

    expect(el.querySelector('app-product-list')).not.toBeNull();
    expect(el.querySelector('app-cart-panel')).not.toBeNull();
  });
});
