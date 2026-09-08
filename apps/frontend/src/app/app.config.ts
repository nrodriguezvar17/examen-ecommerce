import { provideHttpClient } from '@angular/common/http';
import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';
import { CatalogApi } from './core/api/catalog-api';
import { CheckoutApi } from './core/api/checkout-api';
import { HttpCatalogApi } from './core/api/http-catalog.api';
import { MockCheckoutApi } from './core/api/mock-checkout.api';
import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    provideHttpClient(),
    // Catálogo: implementación HTTP real (GET /api/products).
    { provide: CatalogApi, useClass: HttpCatalogApi },
    // Checkout: mock por ahora; se reemplaza por HttpCheckoutApi cuando exista
    // POST /api/checkout/quote.
    { provide: CheckoutApi, useClass: MockCheckoutApi },
  ],
};
