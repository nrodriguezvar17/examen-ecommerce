import { HttpClient, HttpContext, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { AuthTokenStore } from '../auth/auth-token.store';
import { API_BASE_URL } from './api-base-url.token';
import { SKIP_LOADER, apiInterceptor } from './api.interceptor';
import { LoadingService } from './loading.service';

describe('apiInterceptor', () => {
  let http!: HttpClient;
  let httpMock!: HttpTestingController;
  let auth!: AuthTokenStore;
  let loading!: LoadingService;

  function setup(baseUrl = ''): void {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([apiInterceptor])),
        provideHttpClientTesting(),
        { provide: API_BASE_URL, useValue: baseUrl },
      ],
    });
    http = TestBed.inject(HttpClient);
    httpMock = TestBed.inject(HttpTestingController);
    auth = TestBed.inject(AuthTokenStore);
    loading = TestBed.inject(LoadingService);
  }

  afterEach(() => httpMock.verify());

  it('does not add Authorization when there is no token', () => {
    setup();
    http.get('/api/products').subscribe();
    const req = httpMock.expectOne('/api/products');
    expect(req.request.headers.has('Authorization')).toBe(false);
    req.flush([]);
  });

  it('adds the bearer token when present', () => {
    setup();
    auth.setToken('tok.1');
    http.get('/api/products').subscribe();
    const req = httpMock.expectOne('/api/products');
    expect(req.request.headers.get('Authorization')).toBe('Bearer tok.1');
    req.flush([]);
  });

  it('prepends API_BASE_URL to relative /api requests', () => {
    setup('https://api.example.com');
    http.get('/api/products').subscribe();
    httpMock.expectOne('https://api.example.com/api/products').flush([]);
  });

  it('drives the loader and releases it on completion', () => {
    setup();
    http.get('/api/products').subscribe();
    expect(loading.loading()).toBe(true);
    httpMock.expectOne('/api/products').flush([]);
    expect(loading.loading()).toBe(false);
  });

  it('skips the loader when SKIP_LOADER is set', () => {
    setup();
    http.get('/api/products', { context: new HttpContext().set(SKIP_LOADER, true) }).subscribe();
    expect(loading.loading()).toBe(false);
    httpMock.expectOne('/api/products').flush([]);
  });
});
