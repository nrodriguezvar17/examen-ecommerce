import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { CatalogService } from './catalog.service';

describe('CatalogService', () => {
  let service: CatalogService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(CatalogService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('GETs /api/products', () => {
    let received: readonly unknown[] | undefined;
    service.products().subscribe((products) => (received = products));

    const req = httpMock.expectOne('/api/products');
    expect(req.request.method).toBe('GET');
    req.flush([{ id: 1 }]);
    expect(received).toHaveLength(1);
  });

  it('GETs /api/products/:id for the detail screen', () => {
    let received: { brand?: string | null } | undefined;
    service.detail(2).subscribe((detail) => (received = detail));

    const req = httpMock.expectOne('/api/products/2');
    expect(req.request.method).toBe('GET');
    req.flush({ id: 2, brand: 'LogiCorp', reviews: [] });
    expect(received?.brand).toBe('LogiCorp');
  });
});
